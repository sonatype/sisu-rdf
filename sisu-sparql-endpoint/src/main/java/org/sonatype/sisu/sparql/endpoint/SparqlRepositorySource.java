package org.sonatype.sisu.sparql.endpoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.openrdf.repository.Repository;

public interface SparqlRepositorySource
{
    Repository repositoryFor( HttpServletRequest request ) 
        throws ServletException;
}
