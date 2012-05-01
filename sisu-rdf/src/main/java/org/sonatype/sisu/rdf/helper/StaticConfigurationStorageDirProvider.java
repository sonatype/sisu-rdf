package org.sonatype.sisu.rdf.helper;

import static org.sonatype.sisu.rdf.Names.LOCAL_STORAGE_DIR_CONF;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class StaticConfigurationStorageDirProvider
    implements Provider<File>
{

    private final File storageDir;

    @Inject
    public StaticConfigurationStorageDirProvider( @Named( LOCAL_STORAGE_DIR_CONF ) File storageDir )
    {
        this.storageDir = storageDir;
    }

    @Override
    public File get()
    {
        return storageDir;
    }

}
