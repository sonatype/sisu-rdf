package org.sonatype.sisu.sparql.endpoint;

import info.aduna.webapp.util.HttpServerUtil;

import java.io.IOException;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
import static org.openrdf.http.protocol.Protocol.BINDING_PREFIX;
import static org.openrdf.http.protocol.Protocol.DEFAULT_GRAPH_PARAM_NAME;
import static org.openrdf.http.protocol.Protocol.INCLUDE_INFERRED_PARAM_NAME;
import static org.openrdf.http.protocol.Protocol.NAMED_GRAPH_PARAM_NAME;
import static org.openrdf.http.protocol.Protocol.QUERY_LANGUAGE_PARAM_NAME;
import static org.openrdf.http.protocol.Protocol.QUERY_PARAM_NAME;

import org.openrdf.http.protocol.Protocol;
import org.openrdf.http.protocol.error.ErrorInfo;
import org.openrdf.http.protocol.error.ErrorType;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.sonatype.sisu.sparql.endpoint.internal.ClientHTTPException;
import org.sonatype.sisu.sparql.endpoint.internal.ProtocolUtil;
import org.sonatype.sisu.sparql.endpoint.internal.renderer.BooleanQueryResultRenderer;
import org.sonatype.sisu.sparql.endpoint.internal.renderer.GraphQueryResultRenderer;
import org.sonatype.sisu.sparql.endpoint.internal.renderer.TupleQueryResultRenderer;

@Named
@Singleton
public class SparqlServlet
    extends HttpServlet
{

    private static final long serialVersionUID = 7994967560220403352L;

    private static final String METHOD_GET = "GET";

    private static final String METHOD_POST = "POST";

    private final TupleQueryResultRenderer tupleRenderer;

    private final GraphQueryResultRenderer graphRenderer;

    private final BooleanQueryResultRenderer booleanRenderer;
    
    @Inject
    private Logger logger;

    private final SparqlRepositorySource repositorySource;

    @Inject
    SparqlServlet( SparqlRepositorySource repositorySource,
                   TupleQueryResultRenderer tupleRenderer,
                   GraphQueryResultRenderer graphRenderer,
                   BooleanQueryResultRenderer booleanRenderer )
    {
        this.repositorySource = repositorySource;
        this.tupleRenderer = tupleRenderer;
        this.graphRenderer = graphRenderer;
        this.booleanRenderer = booleanRenderer;
    }

    
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        doGet( request, response );
    }


    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        String queryStr = request.getParameter( QUERY_PARAM_NAME );
        int qryCode = 0;
        if ( logger.isInfoEnabled() || logger.isDebugEnabled() )
        {
            qryCode = String.valueOf( queryStr ).hashCode();
        }

        logger.debug( "query {} = {}", qryCode, queryStr );

        if ( queryStr == null )
        {
            throw new ClientHTTPException( SC_BAD_REQUEST, "Missing parameter: " + QUERY_PARAM_NAME );
        }

        String reqMethod = request.getMethod();
        if ( METHOD_GET.equals( reqMethod ) )
        {
            logger.info( "GET query {}", qryCode );
        }
        else if ( METHOD_POST.equals( reqMethod ) )
        {
            logger.info( "POST query {}", qryCode );

            String mimeType = HttpServerUtil.getMIMEType( request.getContentType() );
            if ( !Protocol.FORM_MIME_TYPE.equals( mimeType ) )
            {
                throw new ClientHTTPException( SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported MIME type: " + mimeType );
            }
        }

        try
        {
            RepositoryConnection connection = null;

            try
            {
                Repository repository = repositorySource.repositoryFor( request );
                
                if ( repository == null )
                {
                    throw new ClientHTTPException( SC_BAD_REQUEST, "Unable to find specified repository" );
                }
                
                connection = repository.getConnection();

                Query query = getQuery( repository, connection, queryStr, request, response );

                if ( query instanceof TupleQuery )
                {
                    tupleRenderer.render( (TupleQuery) query, request, response );
                }
                else if ( query instanceof GraphQuery )
                {
                    graphRenderer.render( (GraphQuery) query, request, response ) ;
                }
                else if ( query instanceof BooleanQuery )
                {
                    booleanRenderer.render((BooleanQuery) query, request, response);
                }
                else
                {
                    throw new ClientHTTPException( SC_BAD_REQUEST, "Unsupported query type: "
                            + query.getClass().getName() );
                }
            }
            finally
            {
                if ( connection != null )
                {
                    connection.close();
                }
            }
        }
        catch ( RepositoryException e )
        {
            logger.error( "Repository error", e );
            response.sendError( SC_INTERNAL_SERVER_ERROR );
        }

    }

    private Query getQuery( Repository repository, RepositoryConnection connection, String queryStr,
                            HttpServletRequest request, HttpServletResponse response )
        throws IOException, ClientHTTPException, RepositoryException
    {
        Query result = null;

        // default query language is SPARQL
        QueryLanguage queryLn = QueryLanguage.SPARQL;

        String queryLnStr = request.getParameter( QUERY_LANGUAGE_PARAM_NAME );
        logger.debug( "query language param = {}", queryLnStr );

        if ( queryLnStr != null )
        {
            queryLn = QueryLanguage.valueOf( queryLnStr );

            if ( queryLn == null )
            {
                throw new ClientHTTPException( SC_BAD_REQUEST, "Unknown query language: " + queryLnStr );
            }
        }

        // determine if inferred triples should be included in query evaluation
        boolean includeInferred = ProtocolUtil.parseBooleanParam( request, INCLUDE_INFERRED_PARAM_NAME, true );

        // build a dataset, if specified
        String[] defaultGraphURIs = request.getParameterValues( DEFAULT_GRAPH_PARAM_NAME );
        String[] namedGraphURIs = request.getParameterValues( NAMED_GRAPH_PARAM_NAME );

        DatasetImpl dataset = null;
        if ( defaultGraphURIs != null || namedGraphURIs != null )
        {
            dataset = new DatasetImpl();

            if ( defaultGraphURIs != null )
            {
                for ( String defaultGraphURI : defaultGraphURIs )
                {
                    try
                    {
                        URI uri = connection.getValueFactory().createURI( defaultGraphURI );
                        dataset.addDefaultGraph( uri );
                    }
                    catch ( IllegalArgumentException e )
                    {
                        throw new ClientHTTPException( SC_BAD_REQUEST, "Illegal URI for default graph: "
                                + defaultGraphURI );
                    }
                }
            }

            if ( namedGraphURIs != null )
            {
                for ( String namedGraphURI : namedGraphURIs )
                {
                    try
                    {
                        URI uri = connection.getValueFactory().createURI( namedGraphURI );
                        dataset.addNamedGraph( uri );
                    }
                    catch ( IllegalArgumentException e )
                    {
                        throw new ClientHTTPException( SC_BAD_REQUEST, "Illegal URI for named graph: "
                                + namedGraphURI );
                    }
                }
            }
        }

        try
        {
            result = connection.prepareQuery( queryLn, queryStr );
            result.setIncludeInferred( includeInferred );

            if ( dataset != null )
            {
                result.setDataset( dataset );
            }

            // determine if any variable bindings have been set on this query.
            @SuppressWarnings( "unchecked" )
            Enumeration<String> parameterNames = request.getParameterNames();

            while ( parameterNames.hasMoreElements() )
            {
                String parameterName = parameterNames.nextElement();

                if ( parameterName.startsWith( BINDING_PREFIX ) && parameterName.length() > BINDING_PREFIX.length() )
                {
                    String bindingName = parameterName.substring( BINDING_PREFIX.length() );
                    Value bindingValue = ProtocolUtil.parseValueParam( request, parameterName,
                            connection.getValueFactory() );
                    result.setBinding( bindingName, bindingValue );
                }
            }
        }
        catch ( UnsupportedQueryLanguageException e )
        {
            ErrorInfo errInfo = new ErrorInfo( ErrorType.UNSUPPORTED_QUERY_LANGUAGE, queryLn.getName() );
            throw new ClientHTTPException( SC_BAD_REQUEST, errInfo.toString() );
        }
        catch ( MalformedQueryException e )
        {
            ErrorInfo errInfo = new ErrorInfo( ErrorType.MALFORMED_QUERY, e.getMessage() );
            throw new ClientHTTPException( SC_BAD_REQUEST, errInfo.toString() );
        }

        return result;
    }

}
