package org.openspaces.admin.application.hotredeploy;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.Map;

/**
 * @author Anna_Babich
 */
public class FileManager {
    public static void copy(Configuration configuration) {
        String rootPath = new File("").getAbsoluteFile().getParentFile().getParent();
        Map<String, String> pus = configuration.getPus();
        String deployPath = configuration.getGsLocation() + File.separator + "deploy" + File.separator;

        System.out.println("DEPLOY_PATH" + deployPath);

        for (String puName : pus.keySet()) {
            File file = new File(deployPath + puName);
            FileUtils.deleteDirectory(file);
            try {
                ZipFile zipFile = new ZipFile(rootPath + File.separator + pus.get(puName));
                System.out.println("ZIPFILE="+zipFile.getFile().getAbsolutePath());
                zipFile.extractAll(deployPath + puName);
                System.out.println("EXTRACTING TO " + deployPath + puName);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }
    }
}
