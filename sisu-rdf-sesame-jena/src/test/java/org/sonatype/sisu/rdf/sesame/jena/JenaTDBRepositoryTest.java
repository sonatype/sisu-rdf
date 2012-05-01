package org.sonatype.sisu.rdf.sesame.jena;

import static org.codehaus.plexus.util.FileUtils.deleteDirectory;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;

public class JenaTDBRepositoryTest
{

    private JenaTDBRepository repo;

    private RepositoryConnection connection;

    @Before
    public void setUp()
        throws Exception
    {

        File storageDir = new File( "target/tdb" );
        deleteDirectory( storageDir );
        repo = new JenaTDBRepository( storageDir );
        repo.initialize();
        connection = repo.getConnection();
    }

    @After
    public void tearDown()
        throws Exception
    {
        connection.close();
        repo.shutDown();
    }

    @Test
    public void addString()
        throws Exception
    {
        add( o1() );
    }

    @Test
    public void addInteger()
        throws Exception
    {
        add( o2() );
    }

    @Test
    public void addDouble()
        throws Exception
    {
        add( o3() );
    }

    @Test
    public void addBoolean()
        throws Exception
    {
        add( o4() );
    }

    @Test
    public void addUri()
        throws Exception
    {
        add( s2() );
    }

    @Test
    public void statements()
        throws Exception
    {
        connection.add( s1(), p1(), o1() );
        connection.add( s2(), p1(), o2(), c1() );
        connection.add( s2(), p1(), o3(), c2() );

        RepositoryResult<Statement> result = connection.getStatements( null, null, null, false );
        List<Statement> statements = result.asList();
        assertThat( "Number of statements", statements.size(), is( equalTo( 3 ) ) );
        for ( Statement statement : statements )
        {
            System.out.println( statement );
        }
    }

    @Test
    public void statementsByResource()
        throws Exception
    {
        connection.add( s1(), p1(), o1() );
        connection.add( s2(), p1(), o2(), c1() );
        connection.add( s3(), p1(), o3(), c2() );

        RepositoryResult<Statement> result = connection.getStatements( s2(), null, null, false );
        List<Statement> statements = result.asList();
        assertThat( "Number of statements", statements.size(), is( equalTo( 1 ) ) );
        for ( Statement statement : statements )
        {
            System.out.println( statement );
            assertThat( "Subject", statement.getSubject(), is( equalTo( s2() ) ) );
            assertThat( "Predicate", statement.getPredicate(), is( equalTo( p1() ) ) );
            assertThat( "Object", statement.getObject(), is( equalTo( o2() ) ) );
        }
    }

    @Test
    public void statementsByPredicate()
        throws Exception
    {
        connection.add( s1(), p1(), o1() );
        connection.add( s2(), p1(), o2(), c1() );
        connection.add( s3(), p2(), o3(), c2() );

        RepositoryResult<Statement> result = connection.getStatements( null, p2(), null, false );
        List<Statement> statements = result.asList();
        assertThat( "Number of statements", statements.size(), is( equalTo( 1 ) ) );
        for ( Statement statement : statements )
        {
            System.out.println( statement );
            assertThat( "Subject", statement.getSubject(), is( equalTo( s3() ) ) );
            assertThat( "Predicate", statement.getPredicate(), is( equalTo( p2() ) ) );
            assertThat( "Object", statement.getObject(), is( equalTo( o3() ) ) );
        }
    }

    @Test
    public void statementsByValue()
        throws Exception
    {
        connection.add( s1(), p1(), o1() );
        connection.add( s2(), p1(), o2(), c1() );
        connection.add( s3(), p2(), o3(), c2() );

        RepositoryResult<Statement> result = connection.getStatements( null, null, o2(), false );
        List<Statement> statements = result.asList();
        assertThat( "Number of statements", statements.size(), is( equalTo( 1 ) ) );
        for ( Statement statement : statements )
        {
            System.out.println( statement );
            assertThat( "Subject", statement.getSubject(), is( equalTo( s2() ) ) );
            assertThat( "Predicate", statement.getPredicate(), is( equalTo( p1() ) ) );
            assertThat( "Object", statement.getObject(), is( equalTo( o2() ) ) );
        }
    }

    @Test
    public void statementsByContext()
        throws Exception
    {
        connection.add( s1(), p1(), o1() );
        connection.add( s2(), p1(), o2(), c1() );
        connection.add( s3(), p1(), o3(), c2() );

        RepositoryResult<Statement> result = connection.getStatements( null, null, null, false, c1() );
        List<Statement> statements = result.asList();
        assertThat( "Number of statements", statements.size(), is( equalTo( 1 ) ) );
        for ( Statement statement : statements )
        {
            System.out.println( statement );
            assertThat( "Subject", statement.getSubject(), is( equalTo( s2() ) ) );
            assertThat( "Predicate", statement.getPredicate(), is( equalTo( p1() ) ) );
            assertThat( "Object", statement.getObject(), is( equalTo( o2() ) ) );
        }
    }

    @Test
    public void select()
        throws Exception
    {
        connection.add( s1(), p1(), o1() );
        connection.add( s2(), p1(), o2(), c1() );
        connection.add( s3(), p2(), o3(), c2() );

        TupleQuery tupleQuery = connection.prepareTupleQuery( QueryLanguage.SPARQL, "SELECT ?s WHERE { ?s ?p ?o }" );
        TupleQueryResult result = tupleQuery.evaluate();
        while ( result.hasNext() )
        {
            BindingSet bindingSet = result.next();
            System.out.println( bindingSet );
        }
    }

    @Test
    public void selectFromNamed()
        throws Exception
    {
        connection.add( s1(), p1(), o1() );
        connection.add( s2(), p1(), o2(), c1() );
        connection.add( s3(), p2(), o3(), c2() );

        TupleQuery tupleQuery =
            connection.prepareTupleQuery( QueryLanguage.SPARQL,
                String.format( "SELECT ?s FROM <%s> WHERE { ?s ?p ?o }", c2().stringValue() ) );
        TupleQueryResult result = tupleQuery.evaluate();
        while ( result.hasNext() )
        {
            BindingSet bindingSet = result.next();
            assertThat( "Subject", bindingSet.getBinding( "s" ).getValue(), is( equalTo( (Value) s3() ) ) );
        }
    }

    private void add( Value object )
        throws Exception
    {
        connection.add( s1(), p1(), object );

        RepositoryResult<Statement> statements = connection.getStatements( null, null, null, false );
        assertThat( "Number of statements", statements.asList().size(), is( equalTo( 1 ) ) );
        while ( statements.hasNext() )
        {
            Statement statement = statements.next();
            System.out.println( statement );
            assertThat( "Subject", statement.getSubject(), is( equalTo( s1() ) ) );
            assertThat( "Predicate", statement.getPredicate(), is( equalTo( p1() ) ) );
            assertThat( "Object", statement.getObject(), is( equalTo( object ) ) );
        }
    }

    private Value o1()
    {
        return repo.getValueFactory().createLiteral( "o1" );
    }

    private Value o2()
    {
        return repo.getValueFactory().createLiteral( 2 );
    }

    private Value o3()
    {
        return repo.getValueFactory().createLiteral( 3.0 );
    }

    private Value o4()
    {
        return repo.getValueFactory().createLiteral( true );
    }

    private URI p1()
    {
        return repo.getValueFactory().createURI( "http://test.org/p1" );
    }

    private URI p2()
    {
        return repo.getValueFactory().createURI( "http://test.org/p2" );
    }

    private Resource s1()
    {
        return repo.getValueFactory().createURI( "http://test.org/s1" );
    }

    private Resource s2()
    {
        return repo.getValueFactory().createURI( "http://test.org/s2" );
    }

    private Resource s3()
    {
        return repo.getValueFactory().createURI( "http://test.org/s3" );
    }

    private Resource c1()
    {
        return repo.getValueFactory().createURI( "http://test.org/c1" );
    }

    private Resource c2()
    {
        return repo.getValueFactory().createURI( "http://test.org/c2" );
    }
}
