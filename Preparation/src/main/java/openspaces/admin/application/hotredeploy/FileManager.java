package openspaces.admin.application.hotredeploy;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;

import java.io.File;
import java.io.IOException;
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
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                throw new HotRedeployException(e);
            }
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
