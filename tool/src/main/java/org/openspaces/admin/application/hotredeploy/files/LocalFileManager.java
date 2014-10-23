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

    private String tempDir;

    public LocalFileManager(Config config) {
        this.config = config;
        this.tempDir = System.getProperty("java.io.tmpdir");
    }

    @Override
    public void restoreTempFolders() {
        List<String> puNames = config.getPuToRestart();
        String gigaspacesLocation = config.getGigaspacesLocation();
        for (String puName : puNames) {
            String folderPath = StringUtils.join(new String[]{gigaspacesLocation,"deploy",puName}, File.separator);
            File puFolder = new File(folderPath);
            FileUtils.deleteDirectory(puFolder);
            String tempFolderPath = StringUtils.join(new String[]{tempDir,"pu",puName}, File.separator);
            File tempPuFolder = new File(tempFolderPath);

            String destinationFolderPath = StringUtils.join(new String[]{gigaspacesLocation,"deploy",puName}, File.separator);
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
        File tempDirPu = new File(tempDir + File.separator + "pu");
        FileUtils.deleteDirectory(tempDirPu);
    }

    public void createTempFolder(){
        File dest= new File(tempDir + File.separator + "pu");
        dest.mkdir();
        for (String puName: config.getPuToRestart()){
            String sourcePath = StringUtils.join(new String[]{config.getGigaspacesLocation(),"deploy",puName}, File.separator);
            File srcFile = new File(sourcePath);
            File destFile = new File(dest.getAbsolutePath() + File.separator + puName);
            try {
                FileUtils.copyFolder(srcFile, destFile);
            } catch (IOException e) {
                throw new HotRedeployException(e);
            }
        }
    }
}
