package org.sonatype.sisu.sparql.endpoint;

import java.util.Map;

import javax.inject.Inject;

import org.openrdf.repository.Repository;
import org.sonatype.sisu.rdf.RepositoryIdentity;

public class LookupSparqlRepositorySource
    extends RequestPathSparqlRepositorySource
{

    private final Map<String, Repository> repositories;

    @Inject
    public LookupSparqlRepositorySource( Map<String, Repository> repositories )
    {
        this.repositories = repositories;
    }

    @Override
    protected Repository get( RepositoryIdentity id )
    {
        return repositories.get( id.stringValue() );
    }

}
