package org.sonatype.sisu.rdf.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.FileUtils;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.sonatype.sisu.rdf.RepositoryHub;
import org.sonatype.sisu.rdf.RepositoryIdentity;

// TODO concurrent access to repositories
@Named
@Singleton
class DefaultRepositoryHub
    implements RepositoryHub
{

    private final Map<RepositoryIdentity, Repository> repositories;

    private final DefaultRepositoryFactory defaultRepositoryFactory;

    private final ReentrantReadWriteLock lock;

    @Inject
    private Logger logger;

    @Inject
    DefaultRepositoryHub( DefaultRepositoryFactory defaultRepositoryFactory )
    {
        this.defaultRepositoryFactory = defaultRepositoryFactory;
        repositories = new HashMap<RepositoryIdentity, Repository>();
        lock = new ReentrantReadWriteLock();
    }

    @Override
    public Repository add( RepositoryIdentity id )
    {
        return add( id, defaultRepositoryFactory );
    }

    @Override
    public Repository add( RepositoryIdentity id, RepositoryFactory repositoryFactory )
    {
        lock.writeLock().lock();
        try
        {
            Repository repository = get( id );
            if ( repository == null )
            {
                try
                {
                    repository = repositoryFactory.create( id );
                    repository.initialize();
                    logger.info( String.format( "RDF repository [%s] has been succesfully initialized", id ) );
                }
                catch ( RepositoryException e )
                {
                    logger.warn( String.format( "Could not initialize RDF repository [%s]", id ), e );
                }
                repositories.put( id, repository );
            }
            return repository;
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void delete( RepositoryIdentity id )
    {
        lock.writeLock().lock();
        try
        {
            Repository repository = get( id );
            if ( repository == null )
            {
                return;
            }

            shutdown( id );
            final File repoDir = repository.getDataDir();
            if ( repoDir == null )
            {
                return;
            }
            try
            {
                FileUtils.deleteDirectory( repoDir );
            }
            catch ( IOException e )
            {
                logger.warn( String.format( "Could not remove storage dir [%s] of RDF repository [%s (%s)]",
                    repoDir.getAbsolutePath(), id, repository ), e );
            }
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void shutdown()
    {
        lock.writeLock().lock();
        try
        {
            Iterator<Entry<RepositoryIdentity, Repository>> it = repositories.entrySet().iterator();
            while ( it.hasNext() )
            {
                Entry<RepositoryIdentity, Repository> entry = it.next();
                it.remove();
                shutdown( entry.getKey().stringValue(), entry.getValue() );
            }
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void shutdown( RepositoryIdentity id )
    {
        lock.writeLock().lock();
        try
        {
            Repository repository = repositories.get( id );
            if ( repository == null )
            {
                return;
            }

            shutdown( id.stringValue(), repository );
            repositories.remove( id );
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Repository get( RepositoryIdentity id )
    {
        lock.readLock().lock();
        try
        {
            return repositories.get( id );
        }
        finally
        {
            lock.readLock().unlock();
        }
    }

    private void shutdown( String id, Repository repository )
    {
        try
        {
            repository.shutDown();
            logger.info( String.format( "RDF repository [%s] has been succesfully shutdown", id ) );
        }
        catch ( RepositoryException e )
        {
            logger.warn( String.format( "Could not shutdown RDF repository [%s]", id ), e );
        }
    }

}
