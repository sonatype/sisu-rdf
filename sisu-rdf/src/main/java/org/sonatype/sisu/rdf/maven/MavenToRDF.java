package org.sonatype.sisu.rdf.maven;

import static org.sonatype.sisu.rdf.Builder.literal;
import static org.sonatype.sisu.rdf.Builder.statement;
import static org.sonatype.sisu.rdf.maven.MavenBuilder.mavenPredicate;
import static org.sonatype.sisu.rdf.maven.MavenBuilder.mavenProperty;
import static org.sonatype.sisu.rdf.maven.MavenBuilder.mavenResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.sonatype.sisu.rdf.Builder;
import org.sonatype.sisu.rdf.maven.internal.sp.Gav;

@Named
@Singleton
public class MavenToRDF
{

    public Collection<Statement> model( Model model )
    {
        return model( model, null );
    }

    public Collection<Statement> model( Model model, String defaultLicenseUrl )
    {
        Collection<Statement> statements = new ArrayList<Statement>();

        Resource project = project( model );
        Resource projectVersion = projectVersion( model );

        statements.add( statement( project, RDF.TYPE, MAVEN.PROJECT_TYPE ) );
        statements.add( statement( project, RDFS.LABEL, projectLabel( model ) ) );
        statements.add( statement( project, MAVEN.GROUP_ID, groupId( model ) ) );
        statements.add( statement( project, MAVEN.ARTIFACT_ID, artifactId( model ) ) );
        statements.add( statement( project, MAVEN.PROJECT_VERSION, projectVersion ) );

        statements.add( statement( projectVersion, RDF.TYPE, MAVEN.PROJECT_VERSION_TYPE ) );
        statements.add( statement( projectVersion, RDFS.LABEL, projectVersionLabel( model ) ) );
        statements.add( statement( projectVersion, MAVEN.VERSION, version( model ) ) );
        statements.add( statement( projectVersion, MAVEN.PACKAGING, packaging( model ) ) );
        statements.add( statement( projectVersion, MAVEN.PROJECT, project ) );
        if ( model.getParent() != null )
        {
            statements.add( statement( projectVersion, MAVEN.PARENT, parent( model ) ) );
        }
        if ( model.getName() != null )
        {
            statements.add( statement( projectVersion, MAVEN.NAME, name( model ) ) );
        }
        if ( model.getUrl() != null )
        {
            statements.add( statement( projectVersion, MAVEN.URL, url( model ) ) );
        }
        if ( model.getDescription() != null )
        {
            statements.add( statement( projectVersion, MAVEN.DESCRIPTION, description( model ) ) );
        }
        if ( model.getLicenses() != null && model.getLicenses().size() > 0 )
        {
            for ( License license : model.getLicenses() )
            {
                if ( license.getUrl() != null )
                {
                    statements.add( statement( projectVersion, MAVEN.LICENSE, license( license.getUrl() ) ) );
                }
            }
        }
        else if ( defaultLicenseUrl != null )
        {
            statements.add( statement( projectVersion, MAVEN.LICENSE, license( defaultLicenseUrl ) ) );
        }

        List<Dependency> dependencies = model.getDependencies();
        if ( dependencies != null )
        {
            for ( Dependency dependency : dependencies )
            {
                Resource dep = dependency( model, dependency );
                statements.add( statement( projectVersion, MAVEN.DEPENDS, dep ) );

                Resource usedBy = dependency( dependency );
                statements.add( statement( dep, RDF.TYPE, MAVEN.DEPENDENCY_TYPE ) );
                statements.add( statement( dep, RDFS.LABEL, dependencyLabel( dependency ) ) );

                statements.add( statement( dep, MAVEN.TYPE, type( dependency ) ) );
                statements.add( statement( dep, MAVEN.SCOPE, scope( dependency ) ) );

                statements.add( statement( dep, MAVEN.PROJECT, project ) );
                statements.add( statement( dep, MAVEN.PROJECT_VERSION, projectVersion ) );
                statements.add( statement( dep, MAVEN.DEPENDENCY, usedBy ) );

                statements.add( statement( usedBy, MAVEN.USED_BY, projectVersion ) );
            }
        }

        final Properties properties = model.getProperties();
        if ( properties != null )
        {
            for ( String name : properties.stringPropertyNames() )
            {
                if ( name.startsWith( "rdf." ) || name.startsWith( "rdf:" ) )
                {
                    final String propertyName = name.substring( 4 );
                    if ( !propertyName.isEmpty() )
                    {
                        final String propertyValue = properties.getProperty( name );
                        if ( propertyValue != null && !propertyValue.isEmpty() )
                        {
                            statements.add(
                                statement( projectVersion, mavenProperty( propertyName ), literal( propertyValue ) )
                            );
                        }
                    }
                }
            }
        }

        return statements;
    }

    public Collection<Statement> artifact( String groupId, String artifactId, String version, String classifier,
                                           String extension )
    {
        Collection<Statement> statements = new ArrayList<Statement>();

        Resource artifactUri = artifactUri( groupId, artifactId, version, classifier, extension );
        statements.add( statement( artifactUri, RDF.TYPE, MAVEN.ARTIFACT_TYPE ) );
        statements.add( statement( artifactUri, MAVEN.EXTENSION, extension( extension ) ) );
        if ( classifier != null )
        {
            statements.add( statement( artifactUri, MAVEN.CLASSIFIER, classifier( classifier ) ) );
        }

        Resource project = project( groupId, artifactId );
        Resource projectVersion = projectVersion( groupId, artifactId, version );
        statements.add( statement( artifactUri, MAVEN.PROJECT, project ) );
        statements.add( statement( artifactUri, MAVEN.PROJECT_VERSION, projectVersion ) );
        statements.add( statement( projectVersion, MAVEN.ARTIFACT, artifactUri ) );

        return statements;
    }

    public Collection<Statement> artifactHash( String groupId, String artifactId, String version, String classifier,
                                               String extension, String algorithm, String value )
    {
        Collection<Statement> statements = new ArrayList<Statement>();

        Resource artifactUri = artifactUri( groupId, artifactId, version, classifier, extension );
        statements.add( statement( artifactUri, hashType( algorithm ), hash( value ) ) );

        return statements;
    }

    public Collection<Statement> artifactSignature( String groupId, String artifactId, String version,
                                                    String classifier, String extension, String algorithm,
                                                    String value )
    {
        Collection<Statement> statements = new ArrayList<Statement>();

        Resource artifactUri = artifactUri( groupId, artifactId, version, classifier, extension );
        statements.add( statement( artifactUri, signatureType( algorithm ), signature( value ) ) );

        return statements;
    }

    public Statement projectProperty( String groupId, String artifactId, String name, String value )
    {
        return statement( project( groupId, artifactId ), mavenPredicate( name ), literal( value ) );
    }

    public Resource contextFor( String source, String path )
    {
        if ( path.startsWith( "/" ) && path.length() > 1 )
        {
            path = path.substring( 1 );
        }
        if ( source == null )
        {
            return mavenResource( path );
        }
        else
        {
            return mavenResource( String.format( "%s/%s", source, path ) );
        }
    }

    public Resource contextFor( String source, Gav gav )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( gav.getGroupId().replace( ".", "/" ) );
        builder.append( "/" );
        builder.append( gav.getArtifactId() );
        builder.append( "/" );
        builder.append( gav.getArtifactId() );
        builder.append( "-" );
        builder.append( gav.getVersion() );
        if ( gav.getClassifier() != null )
        {
            builder.append( "-" );
            builder.append( gav.getClassifier() );
        }
        builder.append( "." );
        builder.append( gav.getExtension() );
        if ( gav.isHash() )
        {
            builder.append( "." );
            builder.append( gav.getHashType().toString() );
        }
        if ( gav.isSignature() )
        {
            builder.append( "." );
            builder.append( gav.getSignatureType().toString() );
        }

        return contextFor( source, builder.toString() );
    }

    public Collection<Statement> contextualize( Collection<Statement> statements, Resource context )
    {
        Collection<Statement> contextualized = new ArrayList<Statement>();
        if ( statements != null && statements.size() > 0 )
        {
            for ( Statement statement : statements )
            {
                contextualized.add( statement( statement.getSubject(), statement.getPredicate(), statement.getObject(),
                                               context ) );
            }
        }
        return contextualized;
    }

    public Gav gavOfProjectVersion( String projectVersionUri )
    {
        String gavString = projectVersionUri.replace( MAVEN.URI_NAMESPACE, "" );
        String[] segments = gavString.split( ":" );
        if ( segments.length != 3 )
        {
            return null;
        }
        try
        {
            return new Gav( segments[0], segments[1], segments[2], null, "pom", null, null, null, false, false, null,
                            false, null );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private Value hash( String hash )
    {
        return literal( hash );
    }

    private URI hashType( String algorithm )
    {
        return mavenPredicate( algorithm );
    }

    private Value signature( String signature )
    {
        return literal( signature );
    }

    private URI signatureType( String algorithm )
    {
        return mavenPredicate( algorithm );
    }

    private Value extension( String value )
    {
        return literal( value );
    }

    private Value classifier( String value )
    {
        return literal( value );
    }

    private Value description( Model model )
    {
        return literal( model.getDescription() );
    }

    private Value url( Model model )
    {
        return Builder.resource( model.getUrl() );
    }

    private Value license( String license )
    {
        return Builder.resource( license );
    }

    private Value name( Model model )
    {
        return literal( model.getName() );
    }

    private Value scope( Dependency dependency )
    {
        return literal( dependency.getScope() );
    }

    private Value type( Dependency dependency )
    {
        return literal( dependency.getType() );
    }

    private Value packaging( Model model )
    {
        return literal( model.getPackaging() );
    }

    private Value version( Model model )
    {
        return literal( model.getVersion() );
    }

    private Value artifactId( Model model )
    {
        return literal( model.getArtifactId() );
    }

    private Value groupId( Model model )
    {
        return literal( model.getGroupId() );
    }

    private Resource project( Model model )
    {
        return mavenResource( projectId( model ) );
    }

    private Resource project( String groupId, String artifactId )
    {
        return mavenResource( projectId( groupId, artifactId ) );
    }

    private String projectId( Model model )
    {
        return projectId( model.getGroupId(), model.getArtifactId() );
    }

    private String projectId( String groupId, String artifactId )
    {
        return String.format( "%s:%s", groupId, artifactId );
    }

    private Value projectLabel( Model model )
    {
        return literal( projectId( model ) );
    }

    private Resource projectVersion( Model model )
    {
        return mavenResource( projectVersionId( model ) );
    }

    private Resource projectVersion( String groupId, String artifactId, String version )
    {
        return mavenResource( projectVersionId( groupId, artifactId, version ) );
    }

    private String projectVersionId( Model model )
    {
        return projectVersionId( model.getGroupId(), model.getArtifactId(), model.getVersion() );
    }

    private String projectVersionId( String groupId, String artifactId, String version )
    {
        return String.format( "%s:%s:%s", groupId, artifactId, version );
    }

    private Value projectVersionLabel( Model model )
    {
        return literal( projectVersionId( model ) );
    }

    private Resource parent( Model model )
    {
        return mavenResource( String.format( "%s:%s:%s", model.getParent().getGroupId(),
                                             model.getParent().getArtifactId(),
                                             model.getParent().getVersion() ) );
    }

    private Resource dependency( Dependency dependency )
    {
        return mavenResource( depenencyId( dependency ) );
    }

    private String depenencyId( Dependency dependency )
    {
        return String.format( "%s:%s:%s", dependency.getGroupId(), dependency.getArtifactId(),
                              dependency.getVersion() );
    }

    private Value dependencyLabel( Dependency dependency )
    {
        return literal( depenencyId( dependency ) );
    }

    private Resource dependency( Model model, Dependency dependency )
    {
        return mavenResource( String.format( "%s:%s:%s/%s:%s:%s", model.getGroupId(), model.getArtifactId(),
                                             model.getVersion(),
                                             dependency.getGroupId(), dependency.getArtifactId(),
                                             dependency.getVersion() ) );
    }

    private Resource artifactUri( String groupId, String artifactId, String version, String classifier,
                                  String extension )
    {
        return mavenResource( artifactId( groupId, artifactId, version, classifier, extension ) );
    }

    private String artifactId( String groupId, String artifactId, String version, String classifier, String extension )
    {
        if ( classifier == null )
        {
            return String.format( "%s:%s:%s:%s", groupId, artifactId, version, extension );
        }

        return String.format( "%s:%s:%s:%s:%s", groupId, artifactId, version, classifier, extension );
    }

}
