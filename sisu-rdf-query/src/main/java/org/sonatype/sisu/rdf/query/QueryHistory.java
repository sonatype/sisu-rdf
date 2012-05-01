package org.sonatype.sisu.rdf.query;


public interface QueryHistory
{
    QueryLog commit( QueryResult result );
    
    // returns the query log previous to provided log
    QueryLog prev( QueryLog log);
}
