package org.sonatype.sisu.rdf.sesame.jena.internal;

import java.util.Arrays;
import java.util.Collection;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.Query;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.impl.MapBindingSet;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

abstract class JenaQueryBase
    implements Query
{

    private Dataset dataset;

    private final MapBindingSet bindings;

    private final String query;

    private final com.hp.hpl.jena.query.Dataset jenaDataset;

    private final ValueFactory valueFactory;

    private final String baseURI;

    private final URI unionGraph;

    private final URI defaultGraph;

    JenaQueryBase( String query, ValueFactory valueFactory, com.hp.hpl.jena.query.Dataset jenaDataset, String baseURI )
    {
        this.query = query;
        this.valueFactory = valueFactory;
        this.baseURI = baseURI;

        dataset = new DatasetImpl();
        bindings = new MapBindingSet();
        this.jenaDataset = jenaDataset;

        defaultGraph = valueFactory.createURI( Quad.defaultGraphIRI.getURI() );
        unionGraph = valueFactory.createURI( Quad.unionGraph.getURI() );
    }

    @Override
    public void setBinding( String name, Value value )
    {
        assert value instanceof Literal || value instanceof URI;
        bindings.addBinding( name, value );
    }

    @Override
    public void removeBinding( String name )
    {
        bindings.removeBinding( name );
    }

    @Override
    public void clearBindings()
    {
        bindings.clear();
    }

    @Override
    public BindingSet getBindings()
    {
        return bindings;
    }

    @Override
    public void setDataset( Dataset dataset )
    {
        this.dataset = dataset;
        if ( this.dataset == null )
        {
            this.dataset = new DatasetImpl();
        }
    }

    @Override
    public Dataset getDataset()
    {
        return dataset;
    }

    @Override
    public void setIncludeInferred( boolean includeInferred )
    {
        if ( !includeInferred )
        {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean getIncludeInferred()
    {
        return true;
    }

    @Override
    public void setMaxQueryTime( int maxQueryTime )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxQueryTime()
    {
        return 0;
    }

    com.hp.hpl.jena.query.Dataset getJenaDataset()
    {
        return jenaDataset;
    }

    public ValueFactory getValueFactory()
    {
        return valueFactory;
    }

    com.hp.hpl.jena.query.Query getQuery()
    {
        com.hp.hpl.jena.query.Query jenaQuery = QueryFactory.create( query );

        if ( bindings != null && bindings.size() != 0 )
        {
            String originalProjection = jenaQuery.getProject().toString();
            String projection = originalProjection;

            String originalQueryPattern = jenaQuery.getQueryPattern().toString();
            String queryPattern = originalQueryPattern;

            for ( String name : bindings.getBindingNames() )
            {
                String replacement = getReplacement( bindings.getValue( name ) );
                if ( replacement != null )
                {
                    String pattern = "[\\?\\$]" + name + "(?=\\W)";
                    projection = projection.replaceAll( pattern, "" );
                    queryPattern = queryPattern.replaceAll( pattern, replacement );
                }
            }

            String actualQuery = query.replace( originalProjection, projection );
            actualQuery = actualQuery.replace( originalQueryPattern, queryPattern );
            jenaQuery = QueryFactory.create( actualQuery );

        }

        if ( baseURI != null && baseURI.trim().length() > 0 )
        {
            jenaQuery.setBaseURI( baseURI );
        }
        Collection<URI> defaultGraphs = dataset.getDefaultGraphs();
        if ( ( defaultGraphs == null || defaultGraphs.isEmpty() )
               && ( jenaQuery.getGraphURIs() == null || jenaQuery.getGraphURIs().isEmpty() )
               && !hasServiceElement( jenaQuery ) )
        {
            defaultGraphs = Arrays.asList( defaultGraph, unionGraph );
        }
        if ( defaultGraphs != null && !defaultGraphs.isEmpty() )
        {
            jenaQuery.getGraphURIs().clear();
            for ( URI graph : defaultGraphs )
            {
                jenaQuery.addGraphURI( graph.stringValue() );
            }
        }

        Collection<URI> namedGraphs = dataset.getNamedGraphs();
        if ( namedGraphs != null && !namedGraphs.isEmpty() )
        {
            jenaQuery.getNamedGraphURIs().clear();
            for ( URI graph : namedGraphs )
            {
                jenaQuery.addNamedGraphURI( graph.stringValue() );
            }
        }

        return jenaQuery;
    }

    private boolean hasServiceElement( com.hp.hpl.jena.query.Query jenaQuery )
    {
        Element queryPattern = jenaQuery.getQueryPattern();
        if ( queryPattern == null )
        {
            return false;
        }
        ServiceElementChecker checker = new ServiceElementChecker();
        ElementWalker.walk( queryPattern, checker );
        return checker.hasServiceElement();
    }

    private String getReplacement( Value value )
    {
        StringBuilder sb = new StringBuilder();
        if ( value instanceof URI )
        {
            return appendValue( sb, (URI) value ).toString();
        }
        else if ( value instanceof Literal )
        {
            return appendValue( sb, (Literal) value ).toString();
        }
        else
        {
            throw new IllegalArgumentException(
                    "BNode references not supported by SPARQL end-points" );
        }
    }

    private StringBuilder appendValue( StringBuilder sb, URI uri )
    {
        sb.append( "<" ).append( uri.stringValue() ).append( ">" );
        return sb;
    }

    private StringBuilder appendValue( StringBuilder sb, Literal lit )
    {
        sb.append( '"' );
        sb.append( lit.getLabel().replace( "\"", "\\\"" ) );
        sb.append( '"' );

        if ( lit.getLanguage() != null )
        {
            sb.append( '@' );
            sb.append( lit.getLanguage() );
        }

        if ( lit.getDatatype() != null )
        {
            sb.append( "^^<" );
            sb.append( lit.getDatatype().stringValue() );
            sb.append( '>' );
        }
        return sb;
    }

    private static class ServiceElementChecker
        extends ElementVisitorBase
    {

        private boolean hasServiceElement = false;

        @Override
        public void visit( ElementService el )
        {
            hasServiceElement = true;
        }

        public boolean hasServiceElement()
        {
            return hasServiceElement;
        }

    }

}
