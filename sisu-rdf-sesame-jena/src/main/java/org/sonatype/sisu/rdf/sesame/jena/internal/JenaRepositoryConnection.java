package org.sonatype.sisu.rdf.sesame.jena.internal;

import static org.openrdf.query.QueryLanguage.SPARQL;
import info.aduna.iteration.CloseableIteration;
import info.aduna.iteration.CloseableIteratorIteration;
import info.aduna.iteration.ConvertingIteration;
import info.aduna.iteration.EmptyIteration;
import info.aduna.iteration.ExceptionConvertingIteration;
import info.aduna.iteration.SingletonIteration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.base.RepositoryConnectionBase;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.Quad;

public class JenaRepositoryConnection
    extends RepositoryConnectionBase
    implements RepositoryConnection
{

    private static final String EVERYTHING = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";

    private static final String SOMETHING = "ASK { ?s ?p ?o }";

    private static final String NAMEDGRAPHS = "SELECT DISTINCT ?_ WHERE { GRAPH ?_ { ?s ?p ?o } }";

    private static final String COUNT = "SELECT ( COUNT(*) as ?count ) WHERE { ?s ?p ?o } GROUP BY ?s";

    private PrefixHashSet subjects;

    private final Dataset jenaDataset;

    private final JenaMarshaller jenaMarshaller;

    private final Set<String> uncommitedGraphs;

    public JenaRepositoryConnection( JenaRepositoryBase repository, Dataset jenaDataset )
    {
        super( repository );
        this.jenaDataset = jenaDataset;
        this.jenaMarshaller = new JenaMarshaller();
        this.uncommitedGraphs = new HashSet<String>();
    }

    @Override
    public Query prepareQuery( QueryLanguage ql, String query, String baseURI )
        throws RepositoryException, MalformedQueryException
    {
        String upperCase = query.toUpperCase();

        if ( upperCase.contains( "SELECT" ) )
        {
            return prepareTupleQuery( ql, query, baseURI );
        }
        if ( upperCase.contains( "CONSTRUCT" ) )
        {
            return prepareGraphQuery( ql, query, baseURI );
        }
        if ( upperCase.contains( "ASK" ) )
        {
            return prepareBooleanQuery( ql, query, baseURI );
        }

        throw new IllegalArgumentException( "Unsupported query type: " + query );
    }

    @Override
    public TupleQuery prepareTupleQuery( QueryLanguage ql, String query, String baseURI )
        throws RepositoryException, MalformedQueryException
    {
        if ( SPARQL.equals( ql ) )
        {
            return new JenaTupleQuery( query, getValueFactory(), jenaDataset, baseURI );
        }
        throw new UnsupportedQueryLanguageException( "Unsupported query language " + ql );
    }

    @Override
    public GraphQuery prepareGraphQuery( QueryLanguage ql, String query, String baseURI )
        throws RepositoryException, MalformedQueryException
    {
        if ( SPARQL.equals( ql ) )
        {
            return new JenaGraphQuery( query, getValueFactory(), jenaDataset, baseURI );
        }
        throw new UnsupportedQueryLanguageException( "Unsupported query language " + ql );
    }

    @Override
    public BooleanQuery prepareBooleanQuery( QueryLanguage ql, String query, String baseURI )
        throws RepositoryException, MalformedQueryException
    {
        if ( SPARQL.equals( ql ) )
        {
            return new JenaBooleanQuery( query, getValueFactory(), jenaDataset, baseURI );
        }
        throw new UnsupportedQueryLanguageException( "Unsupported query language " + ql );
    }

    @Override
    public RepositoryResult<Resource> getContextIDs()
        throws RepositoryException
    {
        try
        {
            TupleQuery query = prepareTupleQuery( SPARQL, NAMEDGRAPHS, "" );
            TupleQueryResult result = query.evaluate();
            return new RepositoryResult<Resource>(
                    new ExceptionConvertingIteration<Resource, RepositoryException>(
                            new ConvertingIteration<BindingSet, Resource, QueryEvaluationException>( result )
                            {

                                @Override
                                protected Resource convert( BindingSet bindings )
                                        throws QueryEvaluationException
                                {
                                    return (Resource) bindings.getValue( "_" );
                                }

                            } )
                    {

                        @Override
                        protected RepositoryException convert( Exception e )
                        {
                            return new RepositoryException( e );
                        }

                    } );
        }
        catch ( MalformedQueryException e )
        {
            throw new RepositoryException( e );
        }
        catch ( QueryEvaluationException e )
        {
            throw new RepositoryException( e );
        }
    }

    @Override
    public RepositoryResult<Statement> getStatements( Resource subj, URI pred, Value obj, boolean includeInferred,
                                                      Resource... contexts )
        throws RepositoryException
    {
        try
        {
            if ( noMatch( subj ) )
            {
                return new RepositoryResult<Statement>( new EmptyIteration<Statement, RepositoryException>() );
            }
            if ( subj != null && pred != null && obj != null )
            {
                if ( hasStatement( subj, pred, obj, includeInferred, contexts ) )
                {
                    Statement st = new StatementImpl( subj, pred, obj );
                    CloseableIteration<Statement, RepositoryException> cursor;
                    cursor = new SingletonIteration<Statement, RepositoryException>( st );
                    return new RepositoryResult<Statement>( cursor );
                }
                else
                {
                    return new RepositoryResult<Statement>( new EmptyIteration<Statement, RepositoryException>() );
                }
            }

            GraphQuery query = prepareGraphQuery( SPARQL, EVERYTHING );
            setBindings( query, subj, pred, obj, contexts );

            GraphQueryResult result = query.evaluate();
            return new RepositoryResult<Statement>(
                    new ExceptionConvertingIteration<Statement, RepositoryException>( result )
                    {

                        @Override
                        protected RepositoryException convert( Exception e )
                        {
                            return new RepositoryException( e );
                        }

                    } );
        }
        catch ( MalformedQueryException e )
        {
            throw new RepositoryException( e );
        }
        catch ( QueryEvaluationException e )
        {
            throw new RepositoryException( e );
        }
    }

    @Override
    public boolean hasStatement( Resource subj, URI pred, Value obj, boolean includeInferred, Resource... contexts )
        throws RepositoryException
    {
        try
        {
            if ( noMatch( subj ) )
            {
                return false;
            }

            BooleanQuery query = prepareBooleanQuery( SPARQL, SOMETHING );
            setBindings( query, subj, pred, obj, contexts );

            return query.evaluate();
        }
        catch ( MalformedQueryException e )
        {
            throw new RepositoryException( e );
        }
        catch ( QueryEvaluationException e )
        {
            throw new RepositoryException( e );
        }
    }

    @Override
    public void exportStatements( Resource subj, URI pred, Value obj, boolean includeInferred, RDFHandler handler,
                                  Resource... contexts )
        throws RepositoryException, RDFHandlerException
    {
        try
        {
            if ( noMatch( subj ) )
            {
                handler.startRDF();
                handler.endRDF();
            }
            else
            {
                GraphQuery query = prepareGraphQuery( SPARQL, EVERYTHING );
                setBindings( query, subj, pred, obj, contexts );

                query.evaluate( handler );
            }
        }
        catch ( MalformedQueryException e )
        {
            throw new RepositoryException( e );
        }
        catch ( QueryEvaluationException e )
        {
            throw new RepositoryException( e );
        }
    }

    private boolean noMatch( Resource subj )
    {
        return subjects != null && subj != null
                && !subjects.match( subj.stringValue() );
    }

    private void setBindings( Query query, Resource subj, URI pred, Value obj,
                              Resource... contexts )
        throws RepositoryException
    {
        if ( subj != null )
        {
            query.setBinding( "s", subj );
        }
        if ( pred != null )
        {
            query.setBinding( "p", pred );
        }
        if ( obj != null )
        {
            query.setBinding( "o", obj );
        }
        if ( contexts != null && contexts.length > 0
                && ( contexts[0] != null || contexts.length > 1 ) )
        {
            DatasetImpl dataset = new DatasetImpl();
            for ( Resource ctx : contexts )
            {
                if ( ctx instanceof URI )
                {
                    dataset.addDefaultGraph( (URI) ctx );
                }
                else
                {
                    throw new RepositoryException( "Contexts must be URIs" );
                }
            }
            query.setDataset( dataset );
        }
    }

    @Override
    public long size( Resource... contexts )
        throws RepositoryException
    {
        try
        {
            TupleQuery query = prepareTupleQuery( SPARQL, COUNT, "" );
            setBindings( query, null, null, null, contexts );

            TupleQueryResult result = null;
            try
            {
                result = query.evaluate();
                if ( result.hasNext() )
                {
                    return ( (Literal) result.next().getBinding( "count" ).getValue() ).longValue()
                        + contexts.length == 0 ? getDefaultModel().size() : 0;
                }
            }
            finally
            {
                if ( result != null )
                {
                    result.close();
                }
            }
            throw new RepositoryException( "Could not size the repository" );
        }
        catch ( MalformedQueryException e )
        {
            throw new RepositoryException( e );
        }
        catch ( QueryEvaluationException e )
        {
            throw new RepositoryException( e );
        }
    }

    @Override
    public void commit()
        throws RepositoryException
    {
        Lock lock = jenaDataset.getLock();
        try
        {
            lock.enterCriticalSection( Lock.WRITE );

            for ( String context : new ArrayList<String>( uncommitedGraphs ) )
            {
                Model model = jenaDataset.getNamedModel( context );
                model.commit();
                uncommitedGraphs.remove( context );
            }
        }
        finally
        {
            lock.leaveCriticalSection();
        }
    }

    @Override
    public void rollback()
        throws RepositoryException
    {
        Lock lock = jenaDataset.getLock();
        try
        {
            lock.enterCriticalSection( Lock.WRITE );

            for ( String context : uncommitedGraphs )
            {
                Model model = jenaDataset.getNamedModel( context );
                model.abort();
                uncommitedGraphs.remove( context );
            }
        }
        finally
        {
            lock.leaveCriticalSection();
        }
    }

    @Override
    public RepositoryResult<Namespace> getNamespaces()
        throws RepositoryException
    {
        Collection<Namespace> namespaces = new ArrayList<Namespace>();
        Map<String, String> prefixMap = getDefaultModel().getNsPrefixMap();
        for ( Map.Entry<String, String> entry : prefixMap.entrySet() )
        {
            namespaces.add( new NamespaceImpl( entry.getKey(), entry.getValue() ) );
        }
        return new RepositoryResult<Namespace>( new CloseableIteratorIteration<Namespace, RepositoryException>(
            namespaces.iterator() ) );
    }

    @Override
    public String getNamespace( String prefix )
        throws RepositoryException
    {
        return getDefaultModel().getNsPrefixURI( prefix );
    }

    @Override
    public void setNamespace( String prefix, String name )
        throws RepositoryException
    {
        getDefaultModel().setNsPrefix( prefix, name );
    }

    @Override
    public void removeNamespace( String prefix )
        throws RepositoryException
    {
        getDefaultModel().removeNsPrefix( prefix );
    }

    @Override
    public void clearNamespaces()
        throws RepositoryException
    {
        Map<String, String> prefixMap = getUnionModel().getNsPrefixMap();
        Model defaultModel = getDefaultModel();
        for ( String prefix : prefixMap.keySet() )
        {
            defaultModel.removeNsPrefix( prefix );
        }
    }

    @Override
    protected void addWithoutCommit( Resource subject, URI predicate, Value object, Resource... contexts )
        throws RepositoryException
    {
        Lock lock = jenaDataset.getLock();
        try
        {
            lock.enterCriticalSection( Lock.WRITE );

            if ( contexts == null || contexts.length == 0 )
            {
                Model model = jenaDataset.getDefaultModel();
                model.begin();
                uncommitedGraphs.add( Quad.defaultGraphIRI.getURI() );
                model.add( resource( model, subject ), predicate( model, predicate ), object( model, object ) );
            }
            else
            {
                for ( Resource context : contexts )
                {
                    Model model = jenaDataset.getNamedModel( context.stringValue() );
                    model.begin();
                    uncommitedGraphs.add( context.stringValue() );
                    model.add( resource( model, subject ), predicate( model, predicate ), object( model, object ) );
                }
            }
        }
        finally
        {
            lock.leaveCriticalSection();
        }
    }

    @Override
    protected void removeWithoutCommit( Resource subject, URI predicate, Value object, Resource... contexts )
        throws RepositoryException
    {
        Lock lock = jenaDataset.getLock();
        try
        {
            lock.enterCriticalSection( Lock.WRITE );

            if ( contexts == null || contexts.length == 0 )
            {
                Model model = jenaDataset.getDefaultModel();
                model.begin();
                uncommitedGraphs.add( Quad.defaultGraphIRI.getURI() );
                model.removeAll( resource( model, subject ), predicate( model, predicate ), object( model, object ) );
            }
            else
            {
                for ( Resource context : contexts )
                {
                    Model model = jenaDataset.getNamedModel( context.stringValue() );
                    model.begin();
                    uncommitedGraphs.add( context.stringValue() );
                    model.removeAll( resource( model, subject ), predicate( model, predicate ), object( model, object ) );
                }
            }
        }
        finally
        {
            lock.leaveCriticalSection();
        }
    }

    @Override
    public void close()
        throws RepositoryException
    {
        super.close();
        ( (JenaRepositoryBase) getRepository() ).connectionClosed( this );
    }

    private RDFNode object( Model model, Value object )
    {
        return jenaMarshaller.marshallObject( model, object );
    }

    private Property predicate( Model model, URI predicate )
    {
        return jenaMarshaller.marshallPredicate( model, predicate );
    }

    private com.hp.hpl.jena.rdf.model.Resource resource( Model model, Resource subject )
    {
        return jenaMarshaller.marshallResource( model, subject );
    }

    private Model getUnionModel()
    {
        return jenaDataset.getNamedModel( Quad.unionGraph.getURI() );
    }

    private Model getDefaultModel()
    {
        return jenaDataset.getDefaultModel();
    }

}
