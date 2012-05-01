package org.sonatype.sisu.rdf.query.internal;

import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.sonatype.sisu.rdf.query.Parameter;
import org.sonatype.sisu.rdf.query.QueryResultsProcessor;
import org.sonatype.sisu.rdf.query.QueryRunner;

@Named
@Singleton
class DefaultQueryRunner
    implements QueryRunner
{

    @Override
    public void execute( Repository repository, String query, QueryLanguage queryLanguage,
                         QueryResultsProcessor processor, Parameter... bindings )
    {
        String actualQuery = replaceBindings( query, bindings );

        RepositoryConnection connection = null;
        try
        {
            try
            {
                connection = repository.getConnection();
                TupleQuery preparedQuery = connection.prepareTupleQuery( queryLanguage, actualQuery );
                TupleQueryResult result = preparedQuery.evaluate();
                try
                {
                    if ( processor != null )
                    {
                        processor.process( result );
                    }
                }
                finally
                {
                    result.close();
                }
            }
            finally
            {
                if ( connection != null )
                {
                    connection.close();
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private String replaceBindings( String query, Parameter[] bindings )
    {
        String replaced = query;
        for ( Parameter binding : bindings )
        {
            replaced = replaced.replace( String.format( "${%s}", binding.name() ), binding.value() );
        }
        return replaced;
    }

}
