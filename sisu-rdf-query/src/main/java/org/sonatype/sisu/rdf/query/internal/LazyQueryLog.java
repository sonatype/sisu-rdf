package org.sonatype.sisu.rdf.query.internal;

import static org.sonatype.sisu.rdf.query.Parameter.parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.sonatype.sisu.rdf.query.QueryLog;
import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBinding;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultsProcessor;
import org.sonatype.sisu.rdf.query.QueryRunner;
import org.sonatype.sisu.rdf.query.helper.QueryFile;

public class LazyQueryLog
    implements QueryLog
{
    private final QueryRunner queryRunner;

    private final long timestamp;

    private QueryResult result;

    private final Repository repository;

    @Inject
    public LazyQueryLog( long timestamp,
                         Repository repository,
                         QueryRunner queryRunner )
    {
        this.timestamp = timestamp;
        this.repository = repository;
        this.queryRunner = queryRunner;
    }

    @Override
    public QueryResult result()
    {
        if ( result == null )
        {
            QueryFile queryFile = QueryFile.fromClasspath( "queries/loadBindings.sparql" );
            ResultConverter resultConverter = new ResultConverter();
            queryRunner.execute( repository, queryFile.query(), queryFile.queryLanguage(), resultConverter,
                parameter( "timestamp", String.valueOf( timestamp ) ) );

            result = resultConverter.getResult();
        }

        return result;
    }

    @Override
    public long timestamp()
    {
        return timestamp;
    }

    private class ResultConverter
        implements QueryResultsProcessor
    {

        private DefaultQueryResult results;

        @Override
        public void process( TupleQueryResult result )
        {
            Map<String, List<QueryResultBinding>> sets = new HashMap<String, List<QueryResultBinding>>();
            try
            {
                if ( result != null )
                {
                    while ( result.hasNext() )
                    {
                        BindingSet bindingSet = result.next();
                        String bs = bindingSet.getBinding( "bindingSet" ).getValue().stringValue();
                        List<QueryResultBinding> qrbs = sets.get( bs );
                        if ( qrbs == null )
                        {
                            qrbs = new ArrayList<QueryResultBinding>();
                            sets.put( bs, qrbs );
                        }
                        String name = bindingSet.getBinding( "name" ).getValue().stringValue();
                        String value = bindingSet.getBinding( "value" ).getValue().stringValue();
                        qrbs.add( new DefaultQueryResultBinding( name, value ) );
                    }
                }

                Set<QueryResultBindingSet> bindingSets = new HashSet<QueryResultBindingSet>();
                results = new DefaultQueryResult( bindingSets );

                for ( List<QueryResultBinding> bindings : sets.values() )
                {
                    bindingSets.add( new DefaultQueryResultBindingSet( bindings ) );
                }
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }

        public QueryResult getResult()
        {
            return results;
        }

    }

}
