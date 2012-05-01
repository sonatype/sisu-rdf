package org.sonatype.sisu.rdf.sesame.jena.internal;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryEvaluationException;

import com.hp.hpl.jena.query.Dataset;

class JenaBooleanQuery
    extends JenaQueryBase
    implements BooleanQuery
{

    JenaBooleanQuery( String query, ValueFactory valueFactory, Dataset jenaDataset, String baseURI )
    {
        super( query, valueFactory, jenaDataset, baseURI );
    }

    @Override
    public boolean evaluate()
        throws QueryEvaluationException
    {
        JenaBooleanQueryResult result = new JenaBooleanQueryResult( getQuery(), getJenaDataset() );
        return result.getValue();
    }

}
