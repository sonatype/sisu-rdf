package org.sonatype.sisu.sparql.endpoint.internal.guice;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.openrdf.rio.RDFWriterRegistry;

@Named
@Singleton
class GraphQueryResultWriterRegistryProvider
    extends RDFWriterRegistry
    implements Provider<RDFWriterRegistry>
{

    @Override
    public RDFWriterRegistry get()
    {
        return getInstance();
    }

}
