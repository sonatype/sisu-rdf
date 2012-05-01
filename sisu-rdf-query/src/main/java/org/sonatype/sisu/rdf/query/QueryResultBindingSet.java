package org.sonatype.sisu.rdf.query;

public interface QueryResultBindingSet
    extends Iterable<QueryResultBinding>
{

    QueryResultBinding get( String name );

}
