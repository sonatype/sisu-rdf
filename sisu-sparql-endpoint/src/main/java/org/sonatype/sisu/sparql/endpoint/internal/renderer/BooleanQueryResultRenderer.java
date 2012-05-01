package org.sonatype.sisu.sparql.endpoint.internal.renderer;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.sonatype.sisu.sparql.endpoint.internal.ProtocolUtil.getAcceptableService;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterFactory;
import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.slf4j.Logger;
import org.sonatype.sisu.sparql.endpoint.internal.ClientHTTPException;

@Named( "boolean" )
@Singleton
public class BooleanQueryResultRenderer
    extends QueryResultRenderer<BooleanQuery>
{

    @Inject
    private Logger logger;

    private final BooleanQueryResultWriterRegistry registry;

    @Inject
    BooleanQueryResultRenderer( BooleanQueryResultWriterRegistry registry )
    {
        this.registry = registry;
    }

    @Override
    public void render( BooleanQuery query, HttpServletRequest request, HttpServletResponse response )
        throws IOException, ClientHTTPException
    {
        BooleanQueryResultWriterFactory brWriterFactory = getAcceptableService( request, response, registry );
        BooleanQueryResultFormat brFormat = brWriterFactory.getBooleanQueryResultFormat();

        response.setStatus( SC_OK );
        setContentType( response, brFormat );
        setContentDisposition( "query-result", response, brFormat );

        OutputStream out = response.getOutputStream();
        try
        {
            BooleanQueryResultWriter qrWriter = brWriterFactory.getWriter( out );
            boolean value = query.evaluate();
            qrWriter.write( value );
        }
        catch ( QueryEvaluationException e )
        {
            logger.error( "Query evaluation error", e );
            response.sendError( SC_INTERNAL_SERVER_ERROR, "Query evaluation error: " + e.getMessage() );
        }
        finally
        {
            out.close();
        }

        logEndOfRequest( request );
    }

}
