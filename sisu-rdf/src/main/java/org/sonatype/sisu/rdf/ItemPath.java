/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 *
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 * Sonatype and Sonatype Nexus are trademarks of Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation.
 * M2Eclipse is a trademark of the Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.sisu.rdf;

import java.io.File;

/**
 * Path to an item in a maven repository.
 *
 * @author Alin Dreghiciu
 */
public class ItemPath
{

    /**
     * Path to item in repository.
     */
    private final String path;

    /**
     * Path to file on file system.
     */
    private final File file;

    /**
     * Repository root file.
     */
    private final File repositoryRoot;

    public ItemPath( final File repositoryRoot,
                     final String path )
    {
        assert repositoryRoot != null : "Item repository root must be specified (cannot be null)";
        assert path != null : "Item path must be specified (cannot be null)";
        assert path.trim().length() != 0 : "Item path must be specified (cannot be empty)";

        this.repositoryRoot = repositoryRoot;
        String canonicalPath = path.replace( "\\", "/" );
        if ( canonicalPath.startsWith( "/" ) )
        {
            canonicalPath = canonicalPath.substring( 1 );
        }
        this.path = canonicalPath;
        this.file = new File( repositoryRoot, canonicalPath );
    }

    /**
     * Getter.
     *
     * @return item path
     */
    public String path()
    {
        return path;
    }

    /**
     * Getter.
     *
     * @return item repository root file
     */
    public File repositoryRoot()
    {
        return repositoryRoot;
    }

    /**
     * @return path to file on file system.
     */
    public File file()
    {
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof ItemPath ) )
        {
            return false;
        }

        ItemPath itemPath = (ItemPath) o;

        if ( !path.equals( itemPath.path ) )
        {
            return false;
        }
        if ( !repositoryRoot.equals( itemPath.repositoryRoot ) )
        {
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int result = repositoryRoot.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return path;
    }

}
