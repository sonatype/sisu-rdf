package org.sonatype.sisu.sparql.endpoint.internal;

import javax.servlet.ServletException;

public class HTTPException
    extends ServletException
{

    private static final long serialVersionUID = 8184499995099944946L;
    
    private int statusCode;

    public HTTPException( int statusCode )
    {
        super();
        setStatusCode( statusCode );
    }

    public HTTPException( int statusCode, String message )
    {
        super( message );
        setStatusCode( statusCode );
    }

    public HTTPException( int statusCode, String message, Throwable t )
    {
        super( message, t );
        setStatusCode( statusCode );
    }

    public HTTPException( int statusCode, Throwable t )
    {
        super( t );
        setStatusCode( statusCode );
    }

    public final int getStatusCode()
    {
        return statusCode;
    }

    protected void setStatusCode( int statusCode )
    {
        this.statusCode = statusCode;
    }
}
