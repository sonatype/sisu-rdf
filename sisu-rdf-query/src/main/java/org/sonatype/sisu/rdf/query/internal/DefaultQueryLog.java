package org.sonatype.sisu.rdf.query.internal;

import org.sonatype.sisu.rdf.query.QueryLog;
import org.sonatype.sisu.rdf.query.QueryResult;

class DefaultQueryLog
    implements QueryLog
{

    private final QueryResult result;

    private final long elapsedTime;

    DefaultQueryLog( QueryResult result, long elapsedTime )
    {
        this.result = result;
        this.elapsedTime = elapsedTime;
    }

    @Override
    public QueryResult result()
    {
        return result;
    }

    @Override
    public long timestamp()
    {
        return elapsedTime;
    }

}
