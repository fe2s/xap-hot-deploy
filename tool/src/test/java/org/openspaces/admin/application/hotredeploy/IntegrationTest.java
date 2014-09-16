package org.openspaces.admin.application.hotredeploy;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManagers;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @autor Anna_Babich
 */
public class IntegrationTest {
    public static Logger log = LogManager.getLogger(IntegrationTest.class);
    @Test
    public void test() throws IOException, InterruptedException {
        // TODO Processing unit already deploy
        Admin admin = new AdminFactory().create();
        File file = new File("");
        String s = file.getAbsoluteFile().getParent();
        File puArchive = new File(s + File.separator + "test-pu\\v0\\target\\space.jar");
        GridServiceManagers managers = admin.getGridServiceManagers();
        Thread.sleep(1000);
        managers.deploy(new ProcessingUnitDeployment(puArchive));
        Thread.sleep(1000);
        //TODO check
        File oldFiles = new File("D:\\xap\\gigaspaces-xap-premium-10.0.0-ga\\deploy\\space");
        log.info("old path ---> " + oldFiles.getAbsolutePath());
        deleteDirectory(oldFiles);

        String source = s + File.separator + "test-pu\\v1\\target\\space.jar";
        //TODO property
        String destination = "D:\\xap\\gigaspaces-xap-premium-10.0.0-ga\\deploy\\space";

        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
           log.error(e.getMessage());
        }
        Thread.sleep(1000);
        // TODO args?
        String args[] = {"-pun", "space", "-put", "200", "-smt", "200", "-s", "false", "-dr", "true"};
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);
        HotRedeployMain.main(args);


    }

    /**
     * Deletes directory with subdirs and subfolders
     * @author Cloud
     * @param dir Directory to delete
     */
    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }

}
