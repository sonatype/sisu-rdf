package org.sonatype.sisu.rdf.sesame.jena.internal;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.impl.TupleQueryResultImpl;

import com.hp.hpl.jena.query.Dataset;

class JenaTupleQuery
    extends JenaQueryBase
    implements TupleQuery
{

    JenaTupleQuery( String query, ValueFactory valueFactory, Dataset jenaDataset, String baseURI )
    {
        super( query, valueFactory, jenaDataset, baseURI );
    }

    @Override
    public TupleQueryResult evaluate()
        throws QueryEvaluationException
    {
        JenaTupleQueryResult result = new JenaTupleQueryResult( getQuery(), getJenaDataset(), getValueFactory() );
        return new TupleQueryResultImpl( result.getBindingNames(), result );
    }

    @Override
    public void evaluate( TupleQueryResultHandler handler )
        throws QueryEvaluationException, TupleQueryResultHandlerException
    {
        JenaTupleQueryResult result = null;
        try
        {
            result = new JenaTupleQueryResult( getQuery(), getJenaDataset(), getValueFactory() );
            TupleQueryResultImpl queryResult = new TupleQueryResultImpl( result.getBindingNames(), result );
            handler.startQueryResult( result.getBindingNames() );
            while ( queryResult.hasNext() )
            {
                BindingSet bindingSet = queryResult.next();
                handler.handleSolution( bindingSet );
            }
            handler.endQueryResult();
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
