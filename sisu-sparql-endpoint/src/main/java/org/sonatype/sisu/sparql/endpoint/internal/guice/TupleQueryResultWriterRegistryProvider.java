package org.sonatype.sisu.sparql.endpoint.internal.guice;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;

@Named
@Singleton
class TupleQueryResultWriterRegistryProvider
    extends TupleQueryResultWriterRegistry
    implements Provider<TupleQueryResultWriterRegistry>
{

    @Override
    public TupleQueryResultWriterRegistry get()
    {
        return getInstance();
    }

}
