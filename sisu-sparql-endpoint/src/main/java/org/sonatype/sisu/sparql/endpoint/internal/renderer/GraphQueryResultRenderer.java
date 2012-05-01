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

import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryResultUtil;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;

@Named( "graph" )
@Singleton
public class GraphQueryResultRenderer
    extends QueryResultRenderer<GraphQuery>
{

    @Inject
    private Logger logger;

    private final RDFWriterRegistry registry;

    @Inject
    GraphQueryResultRenderer( RDFWriterRegistry registry )
    {
        this.registry = registry;
    }

    @Override
    public void render( GraphQuery query, HttpServletRequest request, HttpServletResponse response )
        throws IOException, ClientHTTPException
    {
        RDFWriterFactory rdfWriterFactory = getAcceptableService( request, response, registry );
        RDFFormat rdfFormat = rdfWriterFactory.getRDFFormat();

        response.setStatus( SC_OK );
        setContentType( response, rdfFormat );
        setContentDisposition( "query-result", response, rdfFormat );

        OutputStream out = response.getOutputStream();
        try
        {
            RDFWriter rdfWriter = rdfWriterFactory.getWriter( out );
            GraphQueryResult graphQueryResult = query.evaluate();
            QueryResultUtil.report( graphQueryResult, rdfWriter );
        }
        catch ( QueryEvaluationException e )
        {
            logger.error( "Query evaluation error", e );
            response.sendError( SC_INTERNAL_SERVER_ERROR, "Query evaluation error: " + e.getMessage() );
        }
        catch ( RDFHandlerException e )
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