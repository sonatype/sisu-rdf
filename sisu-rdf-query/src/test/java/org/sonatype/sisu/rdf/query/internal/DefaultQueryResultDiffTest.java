package org.sonatype.sisu.rdf.query.internal;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import org.junit.Test;
import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBinding;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;
import org.sonatype.sisu.rdf.query.QueryResultDiff;
import org.sonatype.sisu.rdf.query.internal.DefaultQueryResult;
import org.sonatype.sisu.rdf.query.internal.DefaultQueryResultBinding;
import org.sonatype.sisu.rdf.query.internal.DefaultQueryResultBindingSet;

public class DefaultQueryResultDiffTest
{

    @Test
    public void test1()
    {
        QueryResultBindingSet set2;
        QueryResultBindingSet set3;
        QueryResult result1 = makeResultOf(
            set( b( "n1.1", "v1.1" ), b( "n1.2", "v1.2" ) ),
            set2 = set( b( "n2.1", "v2.1" ) )
            );
        QueryResult result2 = makeResultOf(
            set( b( "n1.2", "v1.2" ), b( "n1.1", "v1.1" ) ),
            set3 = set( b( "n3.1", "v3.1" ) )
            );
        QueryResultDiff diff = result1.diff( result2 );
        assertThat( diff.added(), is( equalTo( iterable( set2 ) ) ) );
        assertThat( diff.removed(), is( equalTo( iterable( set3 ) ) ) );
    }

    private QueryResult makeResultOf( QueryResultBindingSet... sets )
    {
        return new DefaultQueryResult( Arrays.asList( sets ) );
    }

    private Iterable<QueryResultBindingSet> iterable( QueryResultBindingSet... sets )
    {
        return Arrays.asList( sets );
    }

    private QueryResultBindingSet set( QueryResultBinding... bindings )
    {
        return new DefaultQueryResultBindingSet( Arrays.asList( bindings ) );
    }

    private QueryResultBinding b( String name, String value )
    {
        return new DefaultQueryResultBinding( name, value );
    }

}
