package org.sonatype.sisu.rdf.query.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.sonatype.sisu.rdf.query.QueryResultBinding;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;

class DefaultQueryResultBindingSet
    implements QueryResultBindingSet
{

    private final Collection<QueryResultBinding> bindings;

    DefaultQueryResultBindingSet( Collection<QueryResultBinding> bindings )
    {
        this.bindings = bindings;
    }

    @Override
    public Iterator<QueryResultBinding> iterator()
    {
        return bindings.iterator();
    }

    @Override
    public QueryResultBinding get( String name )
    {
        for ( QueryResultBinding binding : bindings )
        {
            if ( binding.name().equals( name ) )
            {
                return binding;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return bindings.toString();
    }

    @Override
    public int hashCode()
    {
        List<QueryResultBinding> sorted = sort( bindings );
        final int prime = 31;
        int result = 1;
        result = prime * result + sorted.hashCode();
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        DefaultQueryResultBindingSet other = (DefaultQueryResultBindingSet) obj;
        if ( bindings == null )
        {
            if ( other.bindings != null )
                return false;
        }
        else if ( !sort( bindings ).equals( sort( other.bindings ) ) )
            return false;
        return true;
    }

    private static List<QueryResultBinding> sort( Collection<QueryResultBinding> bindings )
    {
        ArrayList<QueryResultBinding> sortedBindings = new ArrayList<QueryResultBinding>( bindings );
        Collections.sort( sortedBindings, new Comparator<QueryResultBinding>()
        {

            @Override
            public int compare( QueryResultBinding o1, QueryResultBinding o2 )
            {
                return o1.name().compareTo( o2.name() );
            }

        } );
        return sortedBindings;
    }

}
