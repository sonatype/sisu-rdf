package org.sonatype.sisu.rdf.sesame.jena.internal;

import info.aduna.iteration.CloseableIteratorIteration;

import java.util.Iterator;
import java.util.List;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.shared.Lock;

class JenaTupleQueryResult
    extends CloseableIteratorIteration<BindingSet, QueryEvaluationException>
{

    private final Query query;

    private final Dataset jenaDataset;

    private QueryExecution queryExecution;

    private ResultSet results;

    private int bindingSetSize;

    private final JenaUnmarshaller jenaUnmarshaller;

    JenaTupleQueryResult( Query query, Dataset dataset, ValueFactory valueFactory )
    {
        this.query = query;
        this.jenaDataset = dataset;
        this.jenaUnmarshaller = new JenaUnmarshaller( valueFactory );

        setIterator( new IteratorAdapter() );
    }

    List<String> getBindingNames()
    {
        initialize();
        return results.getResultVars();
    }

    private void initialize()
    {
        if ( results == null && !isClosed() )
        {
            jenaDataset.getLock().enterCriticalSection( Lock.READ );
            queryExecution = QueryExecutionFactory.create( query, jenaDataset );
            // queryExecution.getContext().set( TDB.symUnionDefaultGraph, true );
            results = queryExecution.execSelect();
        }
    }

    @Override
    protected void handleClose()
        throws QueryEvaluationException
    {
        if ( queryExecution != null )
        {
            try
            {
                queryExecution.close();
            }
            finally
            {
                queryExecution = null;
                results = null;
                bindingSetSize = 0;
                jenaDataset.getLock().leaveCriticalSection();
            }
        }
    }

    private class IteratorAdapter
        implements Iterator<BindingSet>
    {

        @Override
        public boolean hasNext()
        {
            initialize();
            return !isClosed() && results.hasNext();
        }

        @Override
        public BindingSet next()
        {
            if ( !hasNext() )
            {
                return null;
            }
            return jenaUnmarshaller.unmarshallBindingSet( results.next(), bindingSetSize );
        }

        @Override
        public void remove()
        {
            initialize();
            results.remove();
        }

    }
}
