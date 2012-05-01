package org.sonatype.sisu.rdf.query;

public interface QueryHistoryHub
{
    QueryHistory historyFor( QueryHistoryId queryId );
}
