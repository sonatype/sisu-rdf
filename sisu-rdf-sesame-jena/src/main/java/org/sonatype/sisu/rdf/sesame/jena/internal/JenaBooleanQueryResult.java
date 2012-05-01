package org.sonatype.sisu.rdf.sesame.jena.internal;

import info.aduna.iteration.CloseableIteratorIteration;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.shared.Lock;

class JenaBooleanQueryResult
    extends CloseableIteratorIteration<BindingSet, QueryEvaluationException>
{

    private final Query query;

    private final Dataset jenaDataset;

    private QueryExecution queryExecution;

    JenaBooleanQueryResult( Query query, Dataset jenaDataset )
    {
        this.query = query;
        this.jenaDataset = jenaDataset;
    }

    public boolean getValue()
    {
        try
        {
            jenaDataset.getLock().enterCriticalSection( Lock.READ );
            queryExecution = QueryExecutionFactory.create( query, jenaDataset );
            boolean result = queryExecution.execAsk();
            queryExecution.close();

            return result;
        }
        finally
        {
            jenaDataset.getLock().leaveCriticalSection();
        }
    }
}
