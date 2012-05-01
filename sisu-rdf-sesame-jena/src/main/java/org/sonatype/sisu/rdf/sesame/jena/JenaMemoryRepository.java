package org.sonatype.sisu.rdf.sesame.jena;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.sonatype.sisu.rdf.sesame.jena.internal.JenaRepositoryBase;
import org.sonatype.sisu.rdf.sesame.jena.internal.JenaRepositoryConnection;
import org.sonatype.sisu.rdf.sesame.jena.internal.LockOnCloseRepositoryConnection;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaMemoryRepository
    extends JenaRepositoryBase
    implements Repository
{

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
        return DatasetFactory.create( ModelFactory.createDefaultModel() );
    }

    @Override
    public String toString()
    {
        return "Jena Memory";
    }

}
