package org.openspaces.admin.application.hotredeploy;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminException;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.gsm.GridServiceManagers;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.remoting.ExecutorRemotingProxyConfigurer;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Hot redeploy integration test.
 * First deploys v0 stateful PU, calls remote service, redeploys v1 and calls remote service again to check that
 * service implementation changed.
 *
 * PREREQUISITES:
 * - run gs-agent.sh/bat
 * - lookup group and locator should be set to default values
 * - properties should be set in config.properties file
 * - make sure that there is no pu with name "space" deployed already
 *
 * @author Anna_Babich
 */
public class IntegrationTest {

    private String rootPath;
    private ProcessingUnit space;

    public static Logger log = LogManager.getLogger(IntegrationTest.class);

    @Before
    public void before() throws InterruptedException {
        Admin admin = new AdminFactory().create();
        rootPath = new File("").getAbsoluteFile().getParent();
        String[] pathToJar = {"test-pu", "v0", "target", "space.jar"};
        File puArchive = new File(rootPath + File.separator + StringUtils.join(pathToJar, File.separator));
        log.info("puArchive " + puArchive);
        try {
            GridServiceManagers managers = admin.getGridServiceManagers();
            managers.waitFor(1);
            space = managers.deploy(new ProcessingUnitDeployment(puArchive));
            space.waitFor(4);
        } catch (AdminException e) {
            throw new HotRedeployException("Unable to identify GSM or Space. Make sure you have default lookup group and locator. Make sure that there is no pu with name \"space\" deployed already", e);
        }
    }

    @Test
    public void test() throws IOException, InterruptedException {
        GigaSpace gigaSpace = new GigaSpaceConfigurer(new UrlSpaceConfigurer("jini://*/*/space")).gigaSpace();

        // check v0 deployed
        TestAction testAction = new ExecutorRemotingProxyConfigurer<TestAction>(gigaSpace, TestAction.class).proxy();
        assertEquals("v0", testAction.doAction());
        gigaSpace.write(new Person("1"));

        // unzip new jar version
        String deployPath = getAndClearDeployDirectory();
        unzipNewSpaceJar(deployPath);
        Thread.sleep(1000);

        // run redeploy
        String args[] = {"-pun", "space", "-put", "1000", "-smt", "1000", "-s", "false", "-dr", "true"};
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);
        HotRedeployMain.main(args);

        // check v1 deployed
        assertEquals( "v1", testAction.doAction());
        Person person = gigaSpace.readById(Person.class, "1");
        assertNotNull(person);
    }

    private void unzipNewSpaceJar(String deployPath) {
        String[] pathToJar = {"test-pu", "v1", "target", "space.jar"};
        String source = rootPath + File.separator + StringUtils.join(pathToJar, File.separator);
        log.info(source);

        log.info(deployPath);
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(deployPath);
        } catch (ZipException e) {
            log.error(e.getMessage());
        }
    }

    private String getAndClearDeployDirectory() {
        String gsLocation = getGsLocation();
        if (gsLocation == null) {
            throw new HotRedeployException("You should specified path to gigaspace folder in config.properties file");
        }
        String[] pathToDeployFolder = {"deploy", "space"};
        File oldFiles = new File(gsLocation + File.separator + StringUtils.join(pathToDeployFolder, File.separator));
        String deployPath = oldFiles.getPath();
        deleteDirectory(oldFiles);
        return deployPath;
    }

    @After
    public void after() {
        space.undeploy();
    }

    /**
     * Deletes directory with subdirs and subfolders
     */
    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                File f = new File(dir, aChildren);
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
