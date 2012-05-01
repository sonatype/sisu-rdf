package org.sonatype.sisu.rdf.query;

public interface Names
{

    static final String PRINTING_QRP = "printing";

    static final String HISTORY_HUB_STORAGE = "historyHub.storage";

    static final String HISTORY_HUB_STORAGE_DIR = "historyHub.storageDir";

    static final String HISTORY_HUB_STORAGE_DEFAULT = "target/rdf/storage";

    static final String HISTORY_HUB_STORAGE_DIR_CONF = "${" + HISTORY_HUB_STORAGE_DIR + ":-"
        + HISTORY_HUB_STORAGE_DEFAULT + "}";

}
