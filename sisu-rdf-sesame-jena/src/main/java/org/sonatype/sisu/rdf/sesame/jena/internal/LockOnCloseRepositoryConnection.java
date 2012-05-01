package org.sonatype.sisu.rdf.sesame.jena.internal;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.base.RepositoryConnectionWrapper;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

public class LockOnCloseRepositoryConnection
    extends RepositoryConnectionWrapper
    implements RepositoryConnection
{

    protected final ReentrantReadWriteLock connectionLock;

    public LockOnCloseRepositoryConnection( Repository repository )
    {
        super( repository );
        connectionLock = new ReentrantReadWriteLock();
    }

    public LockOnCloseRepositoryConnection( Repository repository, RepositoryConnection delegate )
    {
        super( repository, delegate );
        connectionLock = new ReentrantReadWriteLock();
    }

    @Override
    public void close()
        throws RepositoryException
    {
        connectionLock.writeLock().lock();
        try
        {
            super.close();
        }
        finally
        {
            connectionLock.writeLock().unlock();
        }
    }

    @Override
    public Query prepareQuery( QueryLanguage ql, String query, String baseURI )
        throws RepositoryException, MalformedQueryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.prepareQuery( ql, query, baseURI );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public TupleQuery prepareTupleQuery( QueryLanguage ql, String query, String baseURI )
        throws RepositoryException, MalformedQueryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.prepareTupleQuery( ql, query, baseURI );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public GraphQuery prepareGraphQuery( QueryLanguage ql, String query, String baseURI )
        throws RepositoryException, MalformedQueryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.prepareGraphQuery( ql, query, baseURI );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public BooleanQuery prepareBooleanQuery( QueryLanguage ql, String query, String baseURI )
        throws RepositoryException, MalformedQueryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.prepareBooleanQuery( ql, query, baseURI );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public RepositoryResult<Resource> getContextIDs()
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.getContextIDs();
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public RepositoryResult<Statement> getStatements( Resource subj, URI pred, Value obj, boolean includeInferred,
                                                      Resource... contexts )
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.getStatements( subj, pred, obj, includeInferred, contexts );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public boolean hasStatement( Resource subj, URI pred, Value obj, boolean includeInferred, Resource... contexts )
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.hasStatement( subj, pred, obj, includeInferred, contexts );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public void exportStatements( Resource subj, URI pred, Value obj, boolean includeInferred, RDFHandler handler,
                                  Resource... contexts )
        throws RepositoryException, RDFHandlerException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            super.exportStatements( subj, pred, obj, includeInferred, handler, contexts );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public long size( Resource... contexts )
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.size( contexts );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public void commit()
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            super.commit();
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public void rollback()
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            super.rollback();
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public RepositoryResult<Namespace> getNamespaces()
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.getNamespaces();
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public String getNamespace( String prefix )
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            return super.getNamespace( prefix );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public void setNamespace( String prefix, String name )
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            super.setNamespace( prefix, name );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public void removeNamespace( String prefix )
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            super.removeNamespace( prefix );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public void clearNamespaces()
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            super.clearNamespaces();
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    protected void addWithoutCommit( Resource subject, URI predicate, Value object, Resource... contexts )
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            super.addWithoutCommit( subject, predicate, object, contexts );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    protected void removeWithoutCommit( Resource subject, URI predicate, Value object, Resource... contexts )
        throws RepositoryException
    {
        connectionLock.readLock().lock();
        try
        {
            verifyIsOpen();
            super.removeWithoutCommit( subject, predicate, object, contexts );
        }
        finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public String toString()
    {
        try
        {
            return getDelegate().toString();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        try
        {
            return getDelegate().equals( obj );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public int hashCode()
    {
        try
        {
            return getDelegate().hashCode();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( e );
        }
    }

    protected void verifyIsOpen()
        throws RepositoryException
    {
        if ( !isOpen() )
        {
            throw new IllegalStateException( "Connection has been closed" );
        }
    }
}
