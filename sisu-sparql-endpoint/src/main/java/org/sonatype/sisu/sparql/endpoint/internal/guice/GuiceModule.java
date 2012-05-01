package org.sonatype.sisu.sparql.endpoint.internal.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import org.openrdf.query.resultio.BooleanQueryResultWriterRegistry;
import org.openrdf.query.resultio.TupleQueryResultWriterRegistry;
import org.openrdf.rio.RDFWriterRegistry;

import com.google.inject.Binder;
import com.google.inject.Module;

@Named
@Singleton
public class GuiceModule
    implements Module
{

    @Override
    public void configure( Binder binder )
    {
        binder.bind( TupleQueryResultWriterRegistry.class ).toProvider( TupleQueryResultWriterRegistryProvider.class );
        binder.bind( RDFWriterRegistry.class ).toProvider( GraphQueryResultWriterRegistryProvider.class );
        binder.bind( BooleanQueryResultWriterRegistry.class ).toProvider( BooleanQueryResultWriterRegistryProvider.class );
    }

}
