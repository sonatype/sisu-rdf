package org.sonatype.sisu.rdf.query.internal;

import static org.sonatype.sisu.rdf.query.internal.guice.GuiceModule.NS_QUERY;

import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.sonatype.sisu.rdf.query.QueryHistory;
import org.sonatype.sisu.rdf.query.QueryLog;
import org.sonatype.sisu.rdf.query.QueryResult;
import org.sonatype.sisu.rdf.query.QueryResultBinding;
import org.sonatype.sisu.rdf.query.QueryResultBindingSet;

class DefaultQueryHistory
    implements QueryHistory
{

    private final List<QueryLog> logs;

    private final String queryId;

    private final Repository repository;

    DefaultQueryHistory( Repository repository, String queryId, List<QueryLog> logs )
    {
        this.repository = repository;
        this.queryId = queryId;
        this.logs = logs;
    }

    @Override
    public QueryLog commit( QueryResult result )
    {
        DefaultQueryLog log = new DefaultQueryLog( result, System.currentTimeMillis() );
        saveLog( log );
        logs.add( log );
        return log;
    }

    @Override
    public QueryLog prev( QueryLog log )
    {
        int index = logs.indexOf( log );
        if ( index > 0 )
        {
            return logs.get( index - 1 );
        }
        return null;
    }

    private void saveLog( DefaultQueryLog log )
    {
        try
        {
            RepositoryConnection conn = null;
            try
            {
                conn = repository.getConnection();
                ValueFactory vf = conn.getValueFactory();
                URI queryURI = vf.createURI( NS_QUERY + queryId );
                URI logURI = vf.createURI( NS_QUERY + log.timestamp() );
                conn.add( queryURI, RDF.TYPE, vf.createURI( NS_QUERY + "Query" ) );
                conn.add( queryURI, vf.createURI( NS_QUERY + "log" ), logURI );
                conn.add( logURI, RDF.TYPE, vf.createURI( NS_QUERY + "QueryLog" ) );
                conn.add( logURI, vf.createURI( NS_QUERY + "timestamp" ), vf.createLiteral( log.timestamp() ) );
                for ( QueryResultBindingSet set : log.result() )
                {
                    BNode setURI = vf.createBNode();
                    conn.add( logURI, vf.createURI( NS_QUERY + "bindingSet" ), setURI );
                    for ( QueryResultBinding binding : set )
                    {
                        BNode bindingURI = vf.createBNode();
                        conn.add( setURI, vf.createURI( NS_QUERY + "binding" ), bindingURI );
                        conn.add( bindingURI, vf.createURI( NS_QUERY + "name" ), vf.createLiteral( binding.name() ) );
                        conn.add( bindingURI, vf.createURI( NS_QUERY + "value" ), vf.createLiteral( binding.value() ) );
                    }
                }
            }
            finally
            {
                if ( conn != null )
                {
                    conn.close();
                }
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

}
