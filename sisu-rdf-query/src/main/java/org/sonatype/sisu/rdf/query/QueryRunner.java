package org.sonatype.sisu.rdf.query;

import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;

public interface QueryRunner
{

    void execute( Repository repository, String query, QueryLanguage queryLanguage, QueryResultsProcessor processor,
                  Parameter... bindings );

}
