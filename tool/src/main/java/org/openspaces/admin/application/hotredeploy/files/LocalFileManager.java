package org.openspaces.admin.application.hotredeploy.files;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Mykola_Zalyayev
 */
public class LocalFileManager implements FileManager {

    public static Logger log = LogManager.getLogger(LocalFileManager.class);

    private String tempDir;
    private String gsLocation;
    private Map<String, String> pus;
    private String rootPath;

    public LocalFileManager(Config config) {
        this.tempDir = System.getProperty("java.io.tmpdir");
        this.gsLocation = config.getGigaspacesLocation();
        this.pus = config.getPusToRestart();
        this.rootPath = new File("").getAbsoluteFile().getParentFile().getParent();
    }


    @Override
    public void restoreTempFolders() {
        for (String puName : pus.keySet()) {
            String folderPath = StringUtils.join(new String[]{gsLocation,"deploy",puName}, File.separator);
            File puFolder = new File(folderPath);
            try {
                FileUtils.deleteDirectory(puFolder);
            } catch (IOException e) {
                throw new HotRedeployException(e);
            }
            String tempFolderPath = StringUtils.join(new String[]{tempDir,"pu",puName}, File.separator);
            File tempPuFolder = new File(tempFolderPath);

            String destinationFolderPath = StringUtils.join(new String[]{gsLocation,"deploy",puName}, File.separator);
            File destinationFolder = new File(destinationFolderPath);
            try {
                FileUtils.copyDirectory(tempPuFolder, destinationFolder);
            } catch (IOException e) {
                throw new HotRedeployException(e);
            }
        }
    }

    @Override
    public void removeTempFolder() {
        File tempDirPu = new File(tempDir + File.separator + "pu");
        try {
            FileUtils.deleteDirectory(tempDirPu);
        } catch (IOException e) {
            throw new HotRedeployException(e);
        }
    }

    @Override
    public void prepareFiles() {
        createTempFolder();
        for (String puName : pus.keySet()) {
            try {
                File src = new File(gsLocation + File.separator + "deploy" + File.separator + puName);
                log.info("Old files " + src.getAbsolutePath());
                String deployPath = src.getParent();
                log.info("Deploy path " + deployPath);
                File temp = new File(tempDir + "pu");
                log.info("Temp dir " + temp.getAbsolutePath());
                File newFile = new File(rootPath + File.separator + pus.get(puName));
                log.info("New file " + newFile.getAbsolutePath());
                FileUtils.copyDirectoryToDirectory(src, temp);
                FileUtils.deleteDirectory(src);
                ZipFile zipFile = new ZipFile(newFile);
                zipFile.extractAll(src.getAbsolutePath());
            } catch (IOException | ZipException e) {
                throw new HotRedeployException(e);
            }
        }
    }

    public void createTempFolder() {
        File dest = new File(tempDir + File.separator + "pu");
        dest.mkdir();
        for (String puName : pus.keySet()) {
            String sourcePath = StringUtils.join(new String[]{gsLocation, "deploy", puName}, File.separator);
            File srcFile = new File(sourcePath);
            File destFile = new File(dest.getAbsolutePath() + File.separator + puName);
            try {
                FileUtils.copyDirectory(srcFile, destFile);
            } catch (IOException e) {
                throw new HotRedeployException(e);
            }
        }
    }

}
