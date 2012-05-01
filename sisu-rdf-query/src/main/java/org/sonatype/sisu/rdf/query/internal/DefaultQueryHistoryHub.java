package org.sonatype.sisu.rdf.query.internal;

import static org.sonatype.sisu.rdf.query.Parameter.parameter;

import java.util.LinkedList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.sonatype.sisu.rdf.query.QueryHistory;
import org.sonatype.sisu.rdf.query.QueryHistoryHub;
import org.sonatype.sisu.rdf.query.QueryHistoryId;
import org.sonatype.sisu.rdf.query.QueryLog;
import org.sonatype.sisu.rdf.query.QueryResultsProcessor;
import org.sonatype.sisu.rdf.query.QueryRunner;
import org.sonatype.sisu.rdf.query.helper.QueryFile;

@Named
@Singleton
class DefaultQueryHistoryHub
    implements QueryHistoryHub
{

    private final QueryRunner queryRunner;

    private final Repository historyHub;

    @Inject
    DefaultQueryHistoryHub( @Named( "historyhub" ) Repository historyHub,
                             QueryRunner queryRunner )
    {
        this.historyHub = historyHub;
        this.queryRunner = queryRunner;
    }

    @Override
    public QueryHistory historyFor( QueryHistoryId queryId )
    {
        QueryFile queryFile = QueryFile.fromClasspath( "queries/loadHistoryHub.sparql" );
        HistoryMaker historyMaker = new HistoryMaker( queryId.id() );
        queryRunner.execute( historyHub, queryFile.query(), queryFile.queryLanguage(), historyMaker,
            parameter( "queryId", queryId.id() ) );
        return historyMaker.makeHistory();
    }

    private class HistoryMaker
        implements QueryResultsProcessor
    {

        private final String queryId;

        private QueryHistory queryHistory;

        HistoryMaker( String queryId )
        {
            this.queryId = queryId;

        }

        @Override
        public void process( TupleQueryResult result )
        {
            try
            {
                LinkedList<QueryLog> logs = new LinkedList<QueryLog>();
                if ( result != null )
                {
                    while ( result.hasNext() )
                    {
                        BindingSet bindingSet = result.next();
                        Binding timestamp = bindingSet.getBinding( "timestamp" );
                        logs.add( new LazyQueryLog( Long.parseLong( timestamp.getValue().stringValue() ), historyHub,
                            queryRunner ) );
                    }
                }
                queryHistory = new DefaultQueryHistory( historyHub, queryId, logs );
            }
            catch ( QueryEvaluationException e )
            {
                throw new RuntimeException( e );
            }
        }

        public QueryHistory makeHistory()
        {
            return queryHistory;
        }

    }

}
