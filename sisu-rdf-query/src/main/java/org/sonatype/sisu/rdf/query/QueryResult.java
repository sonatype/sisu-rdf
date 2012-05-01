package org.sonatype.sisu.rdf.query;

public interface QueryResult
    extends Iterable<QueryResultBindingSet>
{
    QueryResultDiff diff(QueryResult result);
}
