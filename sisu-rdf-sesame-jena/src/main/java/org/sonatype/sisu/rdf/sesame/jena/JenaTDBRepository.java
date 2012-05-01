package org.sonatype.sisu.rdf.sesame.jena;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.sonatype.sisu.rdf.sesame.jena.internal.JenaRepositoryBase;
import org.sonatype.sisu.rdf.sesame.jena.internal.JenaRepositoryConnection;
import org.sonatype.sisu.rdf.sesame.jena.internal.LockOnCloseRepositoryConnection;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.tdb.TDBFactory;

public class JenaTDBRepository
    extends JenaRepositoryBase
    implements Repository
{

    private File dataDir;

    public JenaTDBRepository( File dataDir )
    {
        this.dataDir = dataDir;
    }

    @Override
    public void setDataDir( File dataDir )
    {
        if ( isInitialized() )
        {
            throw new IllegalStateException( "Repository has already been initialized" );
        }

        this.dataDir = dataDir;
    }

    @Override
    public File getDataDir()
    {
        return dataDir;
    }

    @Override
    public boolean isWritable()
        throws RepositoryException
    {
        return true;
    }

    @Override
    public RepositoryConnection getConnectionInternal( Dataset dataset )
        throws RepositoryException
    {
        return new JenaRepositoryConnection( this, dataset );
    }

    @Override
    protected RepositoryConnection wrap( RepositoryConnection connection )
    {
        return new LockOnCloseRepositoryConnection( this, connection );
    }

    @Override
    protected Dataset createDataSet()
    {
        return TDBFactory.createDataset( dataDir.getAbsolutePath() );
    }

    @Override
    public String toString()
    {
        return "Jena TDB [storage=" + dataDir.getAbsolutePath() + "]";
    }

}
