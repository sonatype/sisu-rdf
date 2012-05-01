package org.sonatype.sisu.rdf.sesame.jena.internal;

import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.GraphQueryResultImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import com.hp.hpl.jena.query.Dataset;

class JenaGraphQuery
    extends JenaQueryBase
    implements GraphQuery
{

    JenaGraphQuery( String query, ValueFactory valueFactory, Dataset jenaDataset, String baseURI )
    {
        super( query, valueFactory, jenaDataset, baseURI );
    }

    @Override
    public GraphQueryResult evaluate()
        throws QueryEvaluationException
    {
        JenaGraphQueryResult result = new JenaGraphQueryResult( getQuery(), getJenaDataset(), getValueFactory() );
        return new GraphQueryResultImpl( result.getNamespaces(), result );
    }

    @Override
    public void evaluate( RDFHandler handler )
        throws QueryEvaluationException, RDFHandlerException
    {
        JenaGraphQueryResult result = null;
        try
        {
            result = new JenaGraphQueryResult( getQuery(), getJenaDataset(), getValueFactory() );
            handler.startRDF();
            Map<String, String> namespaces = result.getNamespaces();
            for ( Map.Entry<String, String> entry : namespaces.entrySet() )
            {
                handler.handleNamespace( entry.getKey(), entry.getValue() );
            }
            while ( result.hasNext() )
            {
                Statement statement = result.next();
                handler.handleStatement( statement );
            }
            handler.endRDF();
        }
        finally
        {
            if ( result != null )
            {
                result.close();
            }
        }
    }

}
