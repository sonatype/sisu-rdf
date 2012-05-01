package org.sonatype.sisu.rdf.query.helper;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.sonatype.sisu.rdf.query.Names;

@Singleton
public class StaticConfigurationHistoryHubStorageDirProvider
    implements Provider<File>
{

    private final File storageDir;

    @Inject
    public StaticConfigurationHistoryHubStorageDirProvider( @Named( Names.HISTORY_HUB_STORAGE_DIR_CONF ) File storageDir )
    {
        this.storageDir = storageDir;
    }

    @Override
    public File get()
    {
        return storageDir;
    }

}
