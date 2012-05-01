package org.sonatype.sisu.rdf.query;

import org.openrdf.query.TupleQueryResult;

public interface QueryResultsProcessor
{

    void process( TupleQueryResult result );

}
