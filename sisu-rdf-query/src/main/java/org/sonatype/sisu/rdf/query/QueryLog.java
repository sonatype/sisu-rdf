package org.sonatype.sisu.rdf.query;

public interface QueryLog
{
    long timestamp();

    QueryResult result();
}
