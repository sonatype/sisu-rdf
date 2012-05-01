package org.sonatype.sisu.rdf.sesame.jena.internal;

import info.aduna.iteration.CloseableIteratorIteration;

import java.util.Iterator;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.QueryEvaluationException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;

class JenaGraphQueryResult
    extends CloseableIteratorIteration<Statement, QueryEvaluationException>
{

    private final Query query;

    private final Dataset jenaDataset;

    private QueryExecution queryExecution;

    private Model results;

    private StmtIterator statements;

    private final JenaUnmarshaller jenaUnmarshaller;

    JenaGraphQueryResult( Query query, Dataset jenaDataset, ValueFactory valueFactory )
    {
        this.query = query;
        this.jenaDataset = jenaDataset;
        this.jenaUnmarshaller = new JenaUnmarshaller( valueFactory );

        setIterator( new IteratorAdapter() );
    }

    public Map<String, String> getNamespaces()
    {
        initialize();
        return results.getNsPrefixMap();
    }

    private void initialize()
    {
        if ( results == null && !isClosed() )
        {
            jenaDataset.getLock().enterCriticalSection( Lock.READ );
            queryExecution = QueryExecutionFactory.create( query, jenaDataset );
            // queryExecution.getContext().set( TDB.symUnionDefaultGraph, true );
            results = queryExecution.execConstruct();
            statements = results.listStatements();
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
                statements = null;
                jenaDataset.getLock().leaveCriticalSection();
            }
        }
    }

    private class IteratorAdapter
        implements Iterator<Statement>
    {

        @Override
        public boolean hasNext()
        {
            initialize();
            return !isClosed() && statements.hasNext();
        }

        @Override
        public Statement next()
        {
            if ( !hasNext() )
            {
                return null;
            }
            return jenaUnmarshaller.unmarshallStatement( statements.next() );
        }

        @Override
        public void remove()
        {
            initialize();
            statements.remove();
        }

    }

}
