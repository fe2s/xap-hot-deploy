package org.openspaces.admin.application.hotredeploy;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManagers;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.remoting.ExecutorProxy;
import org.openspaces.remoting.ExecutorRemotingProxyConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @autor Anna_Babich
 */
//todo comments here, in property
public class IntegrationTest {

    String s;
    GridServiceManagers managers;
    ProcessingUnit space;

    public static Logger log = LogManager.getLogger(IntegrationTest.class);

    @Before
    public void before() throws InterruptedException {
        Admin admin = new AdminFactory().create();
        File file = new File("");
        s = file.getAbsoluteFile().getParent();
        File puArchive = new File(s + File.separator + "test-pu\\v0\\target\\space.jar");
        managers = admin.getGridServiceManagers();
        Thread.sleep(1000);
        managers.deploy(new ProcessingUnitDeployment(puArchive));
        Thread.sleep(20000);
        space = admin.getProcessingUnits().waitFor("space", 30, TimeUnit.SECONDS);
    }

    @Test
    public void test() throws IOException, InterruptedException {


        GigaSpace gigaSpace = new GigaSpaceConfigurer(new UrlSpaceConfigurer("jini://*/*/space")).gigaSpace();
        TestAction testAction = new ExecutorRemotingProxyConfigurer<TestAction>(gigaSpace, TestAction.class)
                .proxy();

        assertEquals(testAction.doAction(), "v0");
        String gsLocation = getGsLocation();
        log.info("gs-location " + gsLocation);
        File oldFiles = new File(gsLocation + "\\deploy\\space");
        log.info("old path ---> " + oldFiles.getPath());
        deleteDirectory(oldFiles);

        String source = s + File.separator + "test-pu\\v1\\target\\space.jar";
        //TODO property (message if null)
        String destination = gsLocation + "\\deploy\\space";

        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            log.error(e.getMessage());
        }
        Thread.sleep(1000);
        // TODO try catch
        String args[] = {"-pun", "space", "-put", "1000", "-smt", "1000", "-s", "false", "-dr", "true"};
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);
        HotRedeployMain.main(args);
        assertEquals(testAction.doAction(), "v1");

    }

    @After
    public void after() {
        space.undeploy();
    }

    /**
     * Deletes directory with subdirs and subfolders
     *
     * @param dir Directory to delete
     * @author Cloud
     */
    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }

    public static String getGsLocation() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            ClassLoader classLoader = IntegrationTest.class.getClassLoader();
            File file = new File(classLoader.getResource("config.properties").getFile());
            log.info(file.getAbsolutePath());
            input = new FileInputStream(file.getAbsolutePath());
            prop.load(input);
            return prop.getProperty("gs_location");
        } catch (IOException ex) {

            throw new HotRedeployException(ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
