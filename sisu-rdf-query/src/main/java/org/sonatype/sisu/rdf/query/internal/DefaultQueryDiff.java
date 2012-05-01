package org.sonatype.sisu.rdf.query.internal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.sonatype.sisu.rdf.query.Parameter;
import org.sonatype.sisu.rdf.query.QueryDiff;
import org.sonatype.sisu.rdf.query.QueryHistory;
import org.sonatype.sisu.rdf.query.QueryHistoryHub;
import org.sonatype.sisu.rdf.query.QueryHistoryId;
import org.sonatype.sisu.rdf.query.QueryLog;
import org.sonatype.sisu.rdf.query.QueryResultDiff;
import org.sonatype.sisu.rdf.query.QueryResultFactory;
import org.sonatype.sisu.rdf.query.QueryRunner;
import org.sonatype.sisu.rdf.query.helper.ExtractingQueryResultsProcessor;

@Named
@Singleton
class DefaultQueryDiff
    implements QueryDiff
{

    private final QueryRunner queryRunner;

    private final QueryHistoryHub queryHistoryHub;

    private final QueryResultFactory queryResultFactory;

    @Inject
    DefaultQueryDiff( QueryRunner queryRunner, QueryHistoryHub queryHistoryHub,
                      QueryResultFactory queryResultFactory )
    {
        this.queryRunner = queryRunner;
        this.queryHistoryHub = queryHistoryHub;
        this.queryResultFactory = queryResultFactory;
    }

    @Override
    public QueryResultDiff diff( QueryHistoryId queryId, QueryLog queryLog, Repository repository, String query,
                                 QueryLanguage queryLanguage, Parameter... bindings )
    {
        ExtractingQueryResultsProcessor qrp = new ExtractingQueryResultsProcessor( queryResultFactory );

        queryRunner.execute( repository, query, queryLanguage, qrp, bindings );

        QueryHistory queryHistory = queryHistoryHub.historyFor( queryId );
        QueryLog last = queryHistory.commit( qrp.queryResult() );
        QueryLog prev = queryLog;
        if ( prev == null )
        {
            prev = queryHistory.prev( last );
        }
        QueryResultDiff diff = last.result().diff( prev == null ? null : prev.result() );

        return diff;
    }

    @Override
    public QueryResultDiff diffPrevious( QueryHistoryId queryId, Repository repository, String query,
                                         QueryLanguage queryLanguage, Parameter... bindings )
    {
        return diff( queryId, null, repository, query, queryLanguage, bindings );
    }

}
