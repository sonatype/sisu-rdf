package org.sonatype.sisu.rdf.query.internal;

import java.util.Collection;
import java.util.Iterator;

import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultDiff;

class DefaultQueryResult
    implements QueryResult
{

    private final Collection<QueryResultBindingSet> bindingSets;

    DefaultQueryResult( Collection<QueryResultBindingSet> bindingSets )
    {
        this.bindingSets = bindingSets;
    }

    @Override
    public Iterator<QueryResultBindingSet> iterator()
    {
        return bindingSets.iterator();
    }

    @Override
    public QueryResultDiff diff( QueryResult result )
    {
        return new DefaultQueryResultDiff( this, result );
    }

    @Override
    public String toString()
    {
        return bindingSets.toString();
    }

}
