package org.sonatype.sisu.rdf.query;

import org.apache.commons.codec.digest.DigestUtils;

public class QueryHistoryId
{
    private final String id;

    public QueryHistoryId( String id )
    {
        this.id = id;
    }

    public String id()
    {
        return id;
    }

    public static QueryHistoryId queryHistoryId( String id )
    {
        return new QueryHistoryId( id );
    }

    public static QueryHistoryId hashOf( String value )
    {
        return new QueryHistoryId( DigestUtils.md5Hex( value ) );
    }

}
