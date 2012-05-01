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

import org.apache.maven.model.Model;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.sonatype.sisu.rdf.ItemPath;
import org.sonatype.sisu.rdf.StatementsProducer;
import org.sonatype.sisu.rdf.StatementsProducerContext;
import org.sonatype.sisu.rdf.maven.MavenToRDF;
import org.sonatype.sisu.rdf.maven.ModelResolver;

@Named( value = "pom" )
@Singleton
public class POMStatementsProducer
    implements StatementsProducer
{

    private final MavenToRDF mavenToRDF;

    private final ModelResolver modelResolver;

    @Inject
    private Logger logger;

    @Inject
    public POMStatementsProducer( MavenToRDF mavenToRDF, ModelResolver modelResolver )
    {
        this.mavenToRDF = mavenToRDF;
        this.modelResolver = modelResolver;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Statement> parse( final ItemPath path, StatementsProducerContext context )
    {
        assert path != null : "Parsed path must be specified (cannot be null)";

        final Gav gav = GavCalculator.pathToGav( path.path() );

        if ( gav == null || !isAPom( gav ) )
        {
            return Collections.emptyList();
        }

        logger.debug( String.format( "Producing POM RDF statements for item [%s]", path ) );

        try
        {
            final Model model = modelResolver.resolve( path.file(), context.remoteRepositories() );
            return mavenToRDF.model( model );
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
     * Checks if the gav refers to a pom or a main artifact.
     *
     * @param gav gav to check
     * @return true if gav refers to a pom or a main artifact, false otherwise
     */
    private boolean isAPom( final Gav gav )
    {
        // TODO shall we extract POM from main artifact?
        return "pom".equals( gav.getExtension() )
            && gav.getClassifier() == null
            && !gav.isHash()
            && !gav.isSignature();
    }

}