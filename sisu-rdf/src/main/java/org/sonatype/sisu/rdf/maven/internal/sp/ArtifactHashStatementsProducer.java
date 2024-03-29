/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.rdf.maven.internal.sp;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Collections;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.sonatype.sisu.rdf.ItemPath;
import org.sonatype.sisu.rdf.StatementsProducer;
import org.sonatype.sisu.rdf.StatementsProducerContext;
import org.sonatype.sisu.rdf.maven.MavenToRDF;

@Named( value = "artifact-hash" )
@Singleton
public class ArtifactHashStatementsProducer
    implements StatementsProducer
{

    private final MavenToRDF mavenToRDF;

    @Inject
    private Logger logger;

    @Inject
    public ArtifactHashStatementsProducer( MavenToRDF mavenToRDF )
    {
        this.mavenToRDF = mavenToRDF;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Statement> parse( final ItemPath path, StatementsProducerContext context )
    {
        assert path != null : "Parsed path must be specified (cannot be null)";

        final Gav gav = GavCalculator.pathToGav( path.path() );

        if ( gav == null || !gav.isHash() )
        {
            return Collections.emptyList();
        }

        logger.debug( String.format( "Producing artifact hash RDF statements for item [%s]", path ) );

        try
        {
            Collection<Statement> statements =
                mavenToRDF.artifactHash( gav.getGroupId(), gav.getArtifactId(), gav.getVersion(), gav.getClassifier(),
                                         gav.getExtension(), gav.getHashType().toString(), readChecksum( path ) );
            return statements;
        }
        catch ( Exception ignore )
        {
            logger.warn(
                format(
                    "Could not index content of [%s] because [%s]. Skipped", gav.getName(), ignore.getMessage()
                ) );
            return Collections.emptyList();
        }
    }

    /**
     * Reads the content of a checksum file (md5/sha1)
     *
     * @param path checksum item file path
     * @return read checksum or null if the value cannot be read
     */
    private String readChecksum( final ItemPath path )
    {
        try
        {
            final String checksumFileContent = FileUtils.fileRead( path.file() );
            final String checksum = chomp( checksumFileContent ).trim().split( " " )[0];

            return checksum;
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    /**
     * <p>
     * Remove the last newline, and everything after it from a String.
     * </p>
     *
     * @param str String to chomp the newline from
     * @return String without chomped newline
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String chomp( String str )
    {
        return chomp( str, "\n" );
    }

    /**
     * <p>
     * Remove the last value of a supplied String, and everything after it from a String.
     * </p>
     *
     * @param str String to chomp from
     * @param sep String to chomp
     * @return String without chomped ending
     * @throws NullPointerException if str or sep is <code>null</code>
     */
    public static String chomp( String str, String sep )
    {
        int idx = str.lastIndexOf( sep );
        if ( idx != -1 )
        {
            return str.substring( 0, idx );
        }
        else
        {
            return str;
        }
    }

}