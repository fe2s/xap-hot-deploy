package org.openspaces.admin.application.hotredeploy.files;

import org.openspaces.admin.application.hotredeploy.config.Config;

/**
 * @author Anna_Babich
 */
public class FileManagerFactory {

    public static FileManager getFileManager(Config config) {
        FileManager fileManager;
        if(!config.isLocalCluster()) {
            fileManager = new SSHFileManager(config);
        } else {
            fileManager = new LocalFileManager(config);
        }
        return fileManager;
    }
}
