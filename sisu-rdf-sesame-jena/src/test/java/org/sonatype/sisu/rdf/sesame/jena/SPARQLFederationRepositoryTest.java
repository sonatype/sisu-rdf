package org.sonatype.sisu.rdf.sesame.jena;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

public class SPARQLFederationRepositoryTest
{

    @Test
    public void tupleQuery()
        throws Exception
    {
        SPARQLFederationRepository repository = new SPARQLFederationRepository();
        repository.initialize();
        RepositoryConnection connection = null;
        try
        {
            connection = repository.getConnection();
            TupleQuery tupleQuery = connection.prepareTupleQuery( QueryLanguage.SPARQL,
                FileUtils.readFileContentFromClasspath( "queries/tupleQuery01.sparql" ) );
            TupleQueryResult result = tupleQuery.evaluate();
            assertThat( "Result", result, is( notNullValue() ) );
            List<String> bindingNames = result.getBindingNames();
            assertThat( "Binding contains 'birthDate'", bindingNames.contains( "birthDate" ), is( true ) );
            assertThat( "Binding contains 'spouseName'", bindingNames.contains( "spouseName" ), is( true ) );
            assertThat( "Binding contains 'movieTitle'", bindingNames.contains( "movieTitle" ), is( true ) );
            assertThat( "Binding contains 'movieDate'", bindingNames.contains( "movieDate" ), is( true ) );
            connection.close();
        }
        finally
        {
            if ( connection != null )
            {
                connection.close();
            }
        }

    }

    @Ignore
    @Test
    public void graphQuery()
        throws Exception
    {
        SPARQLFederationRepository repository = new SPARQLFederationRepository();
        repository.initialize();
        RepositoryConnection connection = null;
        try
        {
            connection = repository.getConnection();
            GraphQuery graphQuery = connection.prepareGraphQuery( QueryLanguage.SPARQL,
                FileUtils.readFileContentFromClasspath( "queries/graphQuery01.sparql" ) );
            GraphQueryResult result = graphQuery.evaluate();
            assertThat( "Result", result, is( notNullValue() ) );
            Map<String, String> namespaces = result.getNamespaces();
            assertThat( "Namespace contains 'wikipedia'", namespaces.containsKey( "wikipedia" ), is( true ) );
            assertThat( "Namespace contains 'imdb'", namespaces.containsKey( "imdb" ), is( true ) );
            assertThat( "Namespace contains 'dcterms'", namespaces.containsKey( "dcterms" ), is( true ) );
            connection.close();
        }
        finally
        {
            if ( connection != null )
            {
                connection.close();
            }
        }

    }

    @Ignore
    @Test
    public void askQuery()
        throws Exception
    {
        SPARQLFederationRepository repository = new SPARQLFederationRepository();
        repository.initialize();
        RepositoryConnection connection = null;
        try
        {
            connection = repository.getConnection();
            BooleanQuery booleanQuery = connection.prepareBooleanQuery( QueryLanguage.SPARQL,
                FileUtils.readFileContentFromClasspath( "queries/askQuery01.sparql" ) );
            boolean result = booleanQuery.evaluate();
            assertThat( "Result", result, is( true ) );
            connection.close();
        }
        finally
        {
            if ( connection != null )
            {
                connection.close();
            }
        }

    }

}
