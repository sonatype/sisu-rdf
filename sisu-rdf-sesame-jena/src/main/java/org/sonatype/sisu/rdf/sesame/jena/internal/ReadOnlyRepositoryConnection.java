package org.sonatype.sisu.rdf.sesame.jena.internal;

import info.aduna.iteration.Iteration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.base.RepositoryConnectionWrapper;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

public class ReadOnlyRepositoryConnection
    extends RepositoryConnectionWrapper
    implements RepositoryConnection
{

    public ReadOnlyRepositoryConnection( Repository repository )
    {
        super( repository );
    }

    public ReadOnlyRepositoryConnection( Repository repository, RepositoryConnection delegate )
    {
        super( repository, delegate );
    }

    @Override
    public void commit()
        throws RepositoryException
    {
        // no-op
    }

    @Override
    public boolean isAutoCommit()
        throws RepositoryException
    {
        return false;
    }

    @Override
    public void rollback()
        throws RepositoryException
    {
        // no-op
    }

    @Override
    public void setAutoCommit( boolean autoCommit )
        throws RepositoryException
    {
        if ( !autoCommit )
        {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void add( Statement st, Resource... contexts )
            throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add( Iterable<? extends Statement> statements,
                     Resource... contexts )
        throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Exception> void add(
                                           Iteration<? extends Statement, E> statementIter,
                                           Resource... contexts )
        throws RepositoryException, E
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add( InputStream in, String baseURI, RDFFormat dataFormat,
                     Resource... contexts )
        throws IOException, RDFParseException,
            RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add( Reader reader, String baseURI, RDFFormat dataFormat,
                     Resource... contexts )
        throws IOException, RDFParseException,
            RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add( URL url, String baseURI, RDFFormat dataFormat,
                     Resource... contexts )
        throws IOException, RDFParseException,
            RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add( File file, String baseURI, RDFFormat dataFormat,
                     Resource... contexts )
        throws IOException, RDFParseException,
            RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add( Resource subject, URI predicate, Value object,
                     Resource... contexts )
        throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear( Resource... contexts )
        throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearNamespaces()
        throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove( Statement st, Resource... contexts )
            throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove( Iterable<? extends Statement> statements,
                        Resource... contexts )
        throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E extends Exception> void remove( Iteration<? extends Statement, E> statementIter,
                                              Resource... contexts )
        throws RepositoryException, E
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove( Resource subject, URI predicate, Value object,
                        Resource... contexts )
        throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNamespace( String prefix )
        throws RepositoryException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNamespace( String prefix, String name )
            throws RepositoryException
    {
        throw new UnsupportedOperationException();
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

}
