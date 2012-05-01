package org.sonatype.sisu.rdf.query.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBinding;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultFactory;
import org.sonatype.sisu.rdf.query.QueryResultsProcessor;

public class ExtractingQueryResultsProcessor
    implements QueryResultsProcessor
{

    private final QueryResultFactory queryResultFactory;

    private QueryResult queryResult;

    public ExtractingQueryResultsProcessor( QueryResultFactory queryResultFactory )
    {
        this.queryResultFactory = queryResultFactory;
    }

    @Override
    public void process( TupleQueryResult result )
    {
        queryResult = adapt( result );
    }

    public QueryResult queryResult()
    {
        return queryResult;
    }

    private QueryResult adapt( TupleQueryResult result )
    {
        Set<QueryResultBindingSet> bindingSets = new HashSet<QueryResultBindingSet>();
        QueryResult adapted = queryResultFactory.createQueryResult( bindingSets );
        try
        {
            if ( result != null )
            {
                while ( result.hasNext() )
                {
                    List<QueryResultBinding> bindings = new ArrayList<QueryResultBinding>();
                    bindingSets.add( queryResultFactory.createQueryResultBindingSet( bindings ) );
                    BindingSet bindingSet = result.next();
                    for ( Binding binding : bindingSet )
                    {
                        QueryResultBinding adaptedBinding =
                            queryResultFactory.createQueryResultBinding( binding.getName(),
                                binding.getValue().stringValue() );
                        bindings.add( adaptedBinding );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        return adapted;
    }

}
