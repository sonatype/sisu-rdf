package org.sonatype.sisu.rdf.query.internal.guice;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.sonatype.sisu.rdf.query.Names;

@Named( "historyhub" )
@Singleton
class HistoryHubRepositoryProvider
    implements Provider<Repository>
{

    private final File storageDir;

    private Repository repository;

    @Inject
    HistoryHubRepositoryProvider( @Named( Names.HISTORY_HUB_STORAGE ) File storageDir )
    {
        this.storageDir = new File( storageDir, "historyHub" );
    }

    @Override
    public Repository get()
    {
        if ( repository == null )
        {
            storageDir.mkdirs();
            repository = new SailRepository( new NativeStore( storageDir, "spoc,posc,cspo" ) )
            {
                @Override
                public String toString()
                {
                    return "HistoryHub [storage=" + storageDir.getAbsolutePath() + "]";
                }
            };
            try
            {
                RepositoryConnection conn = null;
                try
                {
                    repository.initialize();
                    conn = repository.getConnection();
                    conn.setNamespace( "query", GuiceModule.NS_QUERY );
                }
                finally
                {
                    if ( conn != null )
                    {
                        conn.close();
                    }
                }
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }

        }
        return repository;
    }
}
