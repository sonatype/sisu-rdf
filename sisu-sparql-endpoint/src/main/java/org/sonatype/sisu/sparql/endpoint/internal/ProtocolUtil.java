package org.sonatype.sisu.sparql.endpoint.internal;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.aduna.lang.FileFormat;
import info.aduna.lang.service.FileFormatServiceRegistry;
import info.aduna.net.http.RequestHeaders;
import info.aduna.webapp.util.HttpServerUtil;

import org.openrdf.http.protocol.Protocol;
import org.openrdf.http.protocol.error.ErrorInfo;
import org.openrdf.http.protocol.error.ErrorType;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

public class ProtocolUtil
{

    public static Value parseValueParam( HttpServletRequest request, String paramName, ValueFactory vf )
        throws ClientHTTPException
    {
        String paramValue = request.getParameter( paramName );
        try
        {
            return Protocol.decodeValue( paramValue, vf );
        }
        catch ( IllegalArgumentException e )
        {
            throw new ClientHTTPException( SC_BAD_REQUEST, "Invalid value for parameter '" + paramName + "': "
                    + paramValue );
        }
    }

    public static boolean parseBooleanParam( HttpServletRequest request, String paramName, boolean defaultValue )
    {
        String paramValue = request.getParameter( paramName );
        if ( paramValue == null )
        {
            return defaultValue;
        }
        else
        {
            return Boolean.parseBoolean( paramValue );
        }
    }

    public static <FF extends FileFormat, S> S getAcceptableService( HttpServletRequest request,
                                                                     HttpServletResponse response,
                                                                     FileFormatServiceRegistry<FF, S> serviceRegistry )
        throws ClientHTTPException
    {
        // Accept-parameter takes precedence over request headers
        String mimeType = request.getParameter( Protocol.ACCEPT_PARAM_NAME );
        boolean hasAcceptParam = mimeType != null;

        if ( mimeType == null )
        {
            // Find an acceptable MIME type based on the request headers
            logAcceptableFormats( request );

            Collection<String> mimeTypes = new ArrayList<String>( 16 );
            for ( FileFormat format : serviceRegistry.getKeys() )
            {
                mimeTypes.addAll( format.getMIMETypes() );
            }

            mimeType = HttpServerUtil.selectPreferredMIMEType( mimeTypes.iterator(), request );

            response.setHeader( "Vary", RequestHeaders.ACCEPT );
        }

        if ( mimeType != null )
        {
            FF format = serviceRegistry.getFileFormatForMIMEType( mimeType );

            if ( format != null )
            {
                return serviceRegistry.get( format );
            }
        }

        if ( hasAcceptParam )
        {
            ErrorInfo errInfo = new ErrorInfo( ErrorType.UNSUPPORTED_FILE_FORMAT, mimeType );
            throw new ClientHTTPException( SC_BAD_REQUEST, errInfo.toString() );
        }
        else
        {
            // No acceptable format was found, send 406 as required by RFC 2616
            throw new ClientHTTPException( SC_NOT_ACCEPTABLE, "No acceptable file format found." );
        }
    }

    static void logAcceptableFormats( HttpServletRequest request )
    {
        Logger logger = LoggerFactory.getLogger( ProtocolUtil.class );
        if ( logger.isDebugEnabled() )
        {
            StringBuilder acceptable = new StringBuilder( 64 );

            @SuppressWarnings( "unchecked" )
            Enumeration<String> acceptHeaders = request.getHeaders( RequestHeaders.ACCEPT );

            while ( acceptHeaders.hasMoreElements() )
            {
                acceptable.append( acceptHeaders.nextElement() );

                if ( acceptHeaders.hasMoreElements() )
                {
                    acceptable.append( ',' );
                }
            }

            logger.debug( "Acceptable formats: " + acceptable );
        }
    }
}