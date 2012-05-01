package org.sonatype.sisu.sparql.endpoint.internal;

import java.net.HttpURLConnection;

public class ClientHTTPException
    extends HTTPException
{

    private static final long serialVersionUID = -2410305576588721590L;

    private static final int DEFAULT_STATUS_CODE = HttpURLConnection.HTTP_BAD_REQUEST;

    /**
     * Creates a {@link ClientHTTPException} with status code 400 "Bad Request".
     */
    public ClientHTTPException()
    {
        this( DEFAULT_STATUS_CODE );
    }

    /**
     * Creates a {@link ClientHTTPException} with status code 400 "Bad Request".
     */
    public ClientHTTPException( String msg )
    {
        this( DEFAULT_STATUS_CODE, msg );
    }

    /**
     * Creates a {@link ClientHTTPException} with status code 400 "Bad Request".
     */
    public ClientHTTPException( String msg, Throwable t )
    {
        this( DEFAULT_STATUS_CODE, t );
    }

    /**
     * Creates a {@link ClientHTTPException} with the specified status code.
     * 
     * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 4xx range.
     */
    public ClientHTTPException( int statusCode )
    {
        super( statusCode );
    }

    /**
     * Creates a {@link ClientHTTPException} with the specified status code.
     * 
     * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 4xx range.
     */
    public ClientHTTPException( int statusCode, String message )
    {
        super( statusCode, message );
    }

    /**
     * Creates a {@link ClientHTTPException} with the specified status code.
     * 
     * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 4xx range.
     */
    public ClientHTTPException( int statusCode, String message, Throwable t )
    {
        super( statusCode, message, t );
    }

    /**
     * Creates a {@link ClientHTTPException} with the specified status code.
     * 
     * @throws IllegalArgumentException If <tt>statusCode</tt> is not in the 4xx range.
     */
    public ClientHTTPException( int statusCode, Throwable t )
    {
        super( statusCode, t );
    }

    @Override
    protected void setStatusCode( int statusCode )
    {
        if ( statusCode < 400 || statusCode > 499 )
        {
            throw new IllegalArgumentException( "Status code must be in the 4xx range, is: " + statusCode );
        }

        super.setStatusCode( statusCode );
    }
}