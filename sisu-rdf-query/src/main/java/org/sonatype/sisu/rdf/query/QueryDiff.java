package org.sonatype.sisu.rdf.query;

import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;

public interface QueryDiff
{

    QueryResultDiff diff( QueryHistoryId queryId, QueryLog queryLog, Repository repository, final String query,
                          QueryLanguage queryLanguage, Parameter... bindings );

    QueryResultDiff diffPrevious( QueryHistoryId queryId, Repository repository, final String query,
                                  QueryLanguage queryLanguage, Parameter... bindings );

}
