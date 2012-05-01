package org.sonatype.sisu.rdf.query;

public interface QueryResultDiff
{
    Iterable<QueryResultBindingSet> added();

    Iterable<QueryResultBindingSet> removed();
}
