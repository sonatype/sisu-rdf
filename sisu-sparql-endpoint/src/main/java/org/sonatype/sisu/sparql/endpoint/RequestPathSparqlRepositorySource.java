package org.sonatype.sisu.sparql.endpoint;

import static org.sonatype.sisu.rdf.RepositoryIdentity.repositoryIdentity;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.sonatype.sisu.rdf.RepositoryIdentity;

public abstract class RequestPathSparqlRepositorySource
    implements SparqlRepositorySource
{

    @Inject
    private Logger logger;

    @Override
    public Repository repositoryFor( HttpServletRequest request )
    {
        String pathInfoStr = request.getPathInfo();
        logger.debug( "path info: {}", pathInfoStr );

        String repositoryId = null;

        if ( pathInfoStr != null && !pathInfoStr.equals( "/" ) )
        {
            String[] pathInfo = pathInfoStr.substring( 1 ).split( "/" );
            if ( pathInfo.length > 0 )
            {
                repositoryId = pathInfo[0];
                logger.debug( "repository id is '{}'", repositoryId );
            }
        }

        Repository repository = get( repositoryIdentity( repositoryId ) );
        if ( repository == null )
        {
            logger.debug( "repository with id: {} not found during lookup", repositoryId );
        }
        return repository;
    }

    protected abstract Repository get( RepositoryIdentity id );

}
