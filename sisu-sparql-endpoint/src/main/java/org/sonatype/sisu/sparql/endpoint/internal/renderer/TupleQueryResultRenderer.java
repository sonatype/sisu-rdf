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

import org.slf4j.Logger;
import org.sonatype.sisu.sparql.endpoint.internal.ClientHTTPException;

import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryResultUtil;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;

@Named( "tuple" )
@Singleton
public class TupleQueryResultRenderer
    extends QueryResultRenderer<TupleQuery>
{

    @Inject
    private Logger logger;

    private final TupleQueryResultWriterRegistry registry;

    @Inject
    TupleQueryResultRenderer( TupleQueryResultWriterRegistry registry )
    {
        this.registry = registry;
    }

    @Override
    public void render( TupleQuery query, HttpServletRequest request, HttpServletResponse response )
        throws IOException, ClientHTTPException
    {
        TupleQueryResultWriterFactory qrWriterFactory = getAcceptableService( request, response, registry );
        TupleQueryResultFormat qrFormat = qrWriterFactory.getTupleQueryResultFormat();

        response.setStatus( SC_OK );
        setContentType( response, qrFormat );
        setContentDisposition( "query-result", response, qrFormat );

        OutputStream out = response.getOutputStream();
        try
        {
            TupleQueryResultWriter qrWriter = qrWriterFactory.getWriter( out );
            TupleQueryResult queryResult = query.evaluate();
            QueryResultUtil.report( queryResult, qrWriter );
        }
        catch ( QueryEvaluationException e )
        {
            logger.error( "Query evaluation error", e );
            response.sendError( SC_INTERNAL_SERVER_ERROR, "Query evaluation error: " + e.getMessage() );
        }
        catch ( TupleQueryResultHandlerException e )
        {
            logger.error( "Serialization error", e );
            response.sendError( SC_INTERNAL_SERVER_ERROR, "Serialization error: " + e.getMessage() );
        }
        finally
        {
            out.close();
        }
        logEndOfRequest( request );
    }
}
