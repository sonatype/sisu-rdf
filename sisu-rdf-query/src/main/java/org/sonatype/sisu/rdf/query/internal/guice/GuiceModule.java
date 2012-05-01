package org.sonatype.sisu.rdf.query.internal.guice;

import javax.inject.Named;

import org.openrdf.repository.Repository;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

@Named
public class GuiceModule
    implements Module
{
    public static final String NS_QUERY = "http://sonatype.com/query#";

    @Override
    public void configure( Binder binder )
    {
        binder.bind( Repository.class ).annotatedWith( Names.named( "historyhub" ) ).toProvider(
            HistoryHubRepositoryProvider.class );
    }

}
