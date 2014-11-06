package org.openspaces.admin.application.hotredeploy.files;

/**
 * @author Mykola_Zalyayev
 */
public interface FileManager {

    void restoreTempFolders();

    void removeTempFolder();

    public void prepareFiles();
}
