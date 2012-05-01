package org.sonatype.sisu.rdf.sesame.jena.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.hp.hpl.jena.query.Dataset;

public abstract class JenaRepositoryBase
    implements Repository
{

    private final static long DEFAULT_CONNECTION_TIMEOUT = 20000L;

    private final ValueFactory valueFactory;

    private boolean initialized;

    private final ReentrantReadWriteLock initializationLock;

    private final List<RepositoryConnection> activeConnections;

    private Dataset dataset;

    public JenaRepositoryBase()
    {
        valueFactory = ValueFactoryImpl.getInstance();
        initializationLock = new ReentrantReadWriteLock();
        activeConnections = new ArrayList<RepositoryConnection>();
    }

    @Override
    public void setDataDir( File dataDir )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getDataDir()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initialize()
        throws RepositoryException
    {
        initializationLock.writeLock().lock();
        try
        {
            if ( isInitialized() )
            {
                throw new IllegalStateException( "Repository has already been intialized" );
            }

            dataset = createDataSet();
            initializeInternal();
            initialized = true;
        }
        finally
        {
            initializationLock.writeLock().unlock();
        }
    }

    @Override
    public void shutDown()
        throws RepositoryException
    {
        initializationLock.writeLock().lock();
        try
        {
            if ( !isInitialized() )
            {
                return;
            }

            List<RepositoryConnection> activeConnectionsCopy;

            synchronized ( activeConnections )
            {
                if ( !activeConnections.isEmpty() )
                {
                    try
                    {
                        activeConnections.wait( DEFAULT_CONNECTION_TIMEOUT );
                    }
                    catch ( InterruptedException ignore )
                    {
                        // ignore
                    }
                }

                activeConnectionsCopy = new ArrayList<RepositoryConnection>( activeConnections );
            }

            for ( RepositoryConnection connection : activeConnectionsCopy )
            {
                try
                {
                    connection.close();
                }
                catch ( RepositoryException ignore )
                {
                    // ignore
                }
            }

            // All connections should be closed now
            synchronized ( activeConnections )
            {
                activeConnections.clear();
            }

            shutDownInternal();
            if ( dataset != null )
            {
                dataset.close();
                dataset = null;
            }
        }
        finally
        {
            initialized = false;
            initializationLock.writeLock().unlock();
        }
    }

    @Override
    public RepositoryConnection getConnection()
        throws RepositoryException
    {
        initializationLock.readLock().lock();
        try
        {
            if ( !isInitialized() )
            {
                throw new IllegalStateException( "Sail is not initialized or has been shut down" );
            }

            RepositoryConnection connection = getConnectionInternal( dataset );

            synchronized ( activeConnections )
            {
                activeConnections.add( connection );
            }

            return wrap( connection );
        }
        finally
        {
            initializationLock.readLock().unlock();
        }
    }

    @Override
    public ValueFactory getValueFactory()
    {
        return valueFactory;
    }

    protected abstract RepositoryConnection getConnectionInternal( Dataset dataset )
        throws RepositoryException;

    @Override
    public abstract String toString();

    protected void initializeInternal()
        throws RepositoryException
    {
    }

    protected void shutDownInternal()
        throws RepositoryException
    {

    }

    protected RepositoryConnection wrap( RepositoryConnection connection )
    {
        return connection;
    }

    protected boolean isInitialized()
    {
        return initialized;
    }

    void connectionClosed( RepositoryConnection connection )
    {
        synchronized ( activeConnections )
        {
            if ( activeConnections.contains( connection ) )
            {
                activeConnections.remove( connection );

                if ( activeConnections.isEmpty() )
                {
                    activeConnections.notifyAll();
                }
            }
        }
    }

    protected abstract Dataset createDataSet();
}
