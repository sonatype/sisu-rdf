package org.sonatype.sisu.rdf.query.internal;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBinding;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultFactory;

@Named
@Singleton
public class DefaultQueryResultFactory
    implements QueryResultFactory
{

    @Override
    public QueryResult createQueryResult( Collection<QueryResultBindingSet> bindingSets )
    {
        return new DefaultQueryResult( bindingSets );
    }

    @Override
    public QueryResultBindingSet createQueryResultBindingSet( Collection<QueryResultBinding> bindings )
    {
        return new DefaultQueryResultBindingSet( bindings );
    }

    @Override
    public QueryResultBinding createQueryResultBinding( String name, String value )
    {
        return new DefaultQueryResultBinding( name, value );
    }

}
