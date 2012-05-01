package org.sonatype.sisu.sparql.endpoint.internal.renderer;

import static org.openrdf.http.protocol.Protocol.QUERY_PARAM_NAME;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.Query;
import org.slf4j.Logger;
import org.sonatype.sisu.sparql.endpoint.internal.ClientHTTPException;

import info.aduna.lang.FileFormat;

abstract class QueryResultRenderer<T extends Query>
{

    /**
     * Key by which the query result is stored in the model.
     */
    public static final String QUERY_RESULT_KEY = "queryResult";

    /**
     * Key by which the query result writer factory is stored in the model.
     */
    public static final String FACTORY_KEY = "factory";

    /**
     * Key by which a filename hint is stored in the model. The filename hint may be used to present the client with a
     * suggestion for a filename to use for storing the result.
     */
    public static final String FILENAME_HINT_KEY = "filenameHint";

    @Inject
    private Logger logger;

    void setContentType( HttpServletResponse response, FileFormat fileFormat )
        throws IOException
    {
        String mimeType = fileFormat.getDefaultMIMEType();
        if ( fileFormat.hasCharset() )
        {
            Charset charset = fileFormat.getCharset();
            mimeType += "; charset=" + charset.name();
        }
        response.setContentType( mimeType );
    }

    void setContentDisposition( String filename, HttpServletResponse response, FileFormat fileFormat )
        throws IOException
    {
        if ( filename == null || filename.length() == 0 )
        {
            filename = "result";
        }

        if ( fileFormat.getDefaultFileExtension() != null )
        {
            filename += "." + fileFormat.getDefaultFileExtension();
        }

        response.setHeader( "Content-Disposition", "attachment; filename=" + filename );
    }

    void logEndOfRequest( HttpServletRequest request )
    {
        if ( logger.isInfoEnabled() )
        {
            String queryStr = request.getParameter( QUERY_PARAM_NAME );
            int qryCode = String.valueOf( queryStr ).hashCode();
            logger.info( "Request for query {} is finished", qryCode );
        }
    }

    abstract void render( T query, HttpServletRequest request, HttpServletResponse response )
        throws IOException, ClientHTTPException;

}
