package org.sonatype.sisu.rdf.internal;

import static org.sonatype.sisu.rdf.Names.LOCAL_STORAGE;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.sonatype.sisu.rdf.RepositoryHub;
import org.sonatype.sisu.rdf.RepositoryIdentity;

@Named( "sesame-native" )
@Singleton
public class DefaultRepositoryFactory
    implements RepositoryHub.RepositoryFactory
{

    private final File storageDir;

    @Inject
    public DefaultRepositoryFactory( @Named( LOCAL_STORAGE ) File storageDir )
    {
        this.storageDir = storageDir;
    }

    @Override
    public Repository create( RepositoryIdentity id )
        throws RepositoryException
    {
        final File repoDir = new File( storageDir, id.stringValue() );
        repoDir.mkdirs();
        Repository repository = new SailRepository( new NativeStore( repoDir, "spoc,posc,opsc,cspo" ) )
        {
            @Override
            public String toString()
            {
                return "Sesame Native [storage=" + repoDir.getAbsolutePath() + "]";
            }
        };

        return repository;
    }

}
