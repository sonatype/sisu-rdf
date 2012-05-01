package org.sonatype.sisu.sparql.endpoint.internal.guice;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;

@Named
@Singleton
class BooleanQueryResultWriterRegistryProvider
    extends BooleanQueryResultWriterRegistry
    implements Provider<BooleanQueryResultWriterRegistry>
{

    @Override
    public BooleanQueryResultWriterRegistry get()
    {
        return getInstance();
    }

}
