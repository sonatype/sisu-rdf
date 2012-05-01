package org.sonatype.sisu.rdf.query.helper;

import static org.sonatype.sisu.rdf.query.Names.PRINTING_QRP;

import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.sonatype.sisu.rdf.query.QueryResultsProcessor;

@Named( PRINTING_QRP )
@Singleton
public class PrintingQueryResultsProcessor
    implements QueryResultsProcessor
{

    @Override
    public void process( TupleQueryResult result )
    {
        try
        {
            if ( result == null || !result.hasNext() )
            {
                System.out.println( "No results." );
                System.out.println();
                return;
            }
            long i = 0;
            while ( result.hasNext() )
            {
                i++;
                BindingSet bindingSet = result.next();
                for ( Binding binding : bindingSet )
                {
                    System.out.print( String.format( "%s=%s ", binding.getName(), binding.getValue().stringValue() ) );
                }
                System.out.println();
            }
            System.out.println( i + " rows" );
        }
        catch ( QueryEvaluationException e )
        {
            throw new RuntimeException( e );
        }
    }

}
