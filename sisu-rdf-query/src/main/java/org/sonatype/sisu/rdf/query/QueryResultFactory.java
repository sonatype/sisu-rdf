package org.sonatype.sisu.rdf.query;

import java.util.Collection;

public interface QueryResultFactory
{
    QueryResult createQueryResult( Collection<QueryResultBindingSet> bindingSets );

    QueryResultBindingSet createQueryResultBindingSet( Collection<QueryResultBinding> bindings );

    QueryResultBinding createQueryResultBinding( String name, String value );
}
