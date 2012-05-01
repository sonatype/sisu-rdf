package org.sonatype.sisu.rdf.query.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultDiff;

class DefaultQueryResultDiff
    implements QueryResultDiff
{

    private final QueryResult result1;

    private final QueryResult result2;

    private Collection<QueryResultBindingSet> added;

    private Collection<QueryResultBindingSet> removed;

    DefaultQueryResultDiff( QueryResult result1, QueryResult result2 )
    {
        this.result1 = result1;
        this.result2 = result2;
    }

    @Override
    public Iterable<QueryResultBindingSet> added()
    {
        diff();
        return added;
    }

    @Override
    public Iterable<QueryResultBindingSet> removed()
    {
        diff();
        return removed;
    }

    private void diff()
    {
        if ( added != null )
        {
            return;
        }
        List<QueryResultBindingSet> sets1 = list( result1 );
        List<QueryResultBindingSet> sets2 = list( result2 );
        added = new ArrayList<QueryResultBindingSet>( sets1 );
        added.removeAll( sets2 );
        removed = new ArrayList<QueryResultBindingSet>( sets2 );
        removed.removeAll( sets1 );
    }

    private static List<QueryResultBindingSet> list( Iterable<QueryResultBindingSet> sets )
    {
        List<QueryResultBindingSet> list = new ArrayList<QueryResultBindingSet>();
        if ( sets!= null )
        {
            for ( QueryResultBindingSet set : sets )
            {
                list.add( set );
            }
        }
        return list;
    }

    @Override
    public String toString()
    {
        return "added " + added() + ", removed " + removed();
    }

}
