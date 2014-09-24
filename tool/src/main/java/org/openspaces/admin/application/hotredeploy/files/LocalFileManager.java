package org.openspaces.admin.application.hotredeploy.files;

import org.apache.commons.lang3.StringUtils;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.utils.FileUtils;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Mykola_Zalyayev
 */
public class LocalFileManager implements FileManager {

    private Config config;

    public LocalFileManager(Config config) {
        this.config = config;
    }

    @Override
    public void restoreTempFolders() {
        List<String> puNames = config.getPuToRestart();
        String gigaspacesLocation = config.getGigaspacesLocation();
        for (String puName : puNames) {
            String folderPath = StringUtils.join(new String[]{gigaspacesLocation,"deploy",puName}, File.pathSeparator);
            File puFolder = new File(folderPath);
            FileUtils.deleteDirectory(puFolder);

            String tempDir = System.getProperty("java.io.tmpdir");
            String tempFolderPath = StringUtils.join(new String[]{tempDir,"pu",puName}, File.pathSeparator);
            File tempPuFolder = new File(tempFolderPath);

            String destinationFolderPath = StringUtils.join(new String[]{gigaspacesLocation,"deploy",puName}, File.pathSeparator);
            File destinationFolder = new File(destinationFolderPath);
            try {
                FileUtils.copyFolder(tempPuFolder, destinationFolder);
            } catch (IOException e) {
                throw new HotRedeployException(e);
            }
        }
    }

    @Override
    public void removeFolder() {
        String tempDir = System.getProperty("java.io.tmpdir");
        File tempDirPu = new File(tempDir + File.separator + "pu");
        FileUtils.deleteDirectory(tempDirPu);
    }
}
