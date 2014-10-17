package org.openspaces.admin.application.hotredeploy;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminException;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;
import org.openspaces.admin.application.hotredeploy.utils.FileUtils;
import org.openspaces.admin.gsm.GridServiceManagers;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;
import org.openspaces.remoting.ExecutorRemotingProxyConfigurer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Hot redeploy integration test.
 * First deploys v0 stateful PU, calls remote service, redeploys v1 and calls remote service again to check that
 * service implementation changed.
 * <p/>
 * PREREQUISITES:
 * - run gs-agent.sh/bat
 * - lookup group and locator should be set to default values
 * - properties should be set in config.properties file
 * - make sure that there is no pu with name "space" deployed already
 *
 * @author Anna_Babich
 */
public class IntegrationTest {

    private String rootPath = new File("").getAbsoluteFile().getParent();
    private ProcessingUnit space;
    private String gsLocation;

    public static Logger log = LogManager.getLogger(IntegrationTest.class);

    @BeforeClass
    public static void beforeClass(){
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < 3; i ++) {
            list.add("y");
            list.add(System.lineSeparator());
        }
        MockedConsole console = new MockedConsole(list);
        System.setIn(console);
    }

    @Before
    public void before() throws InterruptedException {
        space = deployV0();
    }

    @Test
    public void testCorrectRedeploy() throws IOException, InterruptedException {
        testActions("v1", "v1");
    }

    @Test
    public void testRollback() throws IOException, InterruptedException {
        testActions("v2", "v0");
    }


    @After
    public void after() {
        space.undeploy();
    }


    public void testActions(String moduleName, String resultString) throws InterruptedException {
        GigaSpace gigaSpace = new GigaSpaceConfigurer(new UrlSpaceConfigurer("jini://*/*/space")).gigaSpace();

        // check start deployed version
        TestAction testAction = new ExecutorRemotingProxyConfigurer<TestAction>(gigaSpace, TestAction.class).proxy();
        assertEquals("v0", testAction.doAction());
        gigaSpace.write(new Person("1"));

        // unzip new jar version
        String deployPath = getAndClearDeployDirectory();
        unzipNewSpaceJar(deployPath, moduleName);
        Thread.sleep(1000);

        // run redeploy
        String args[] = {"-pun", "space", "-put", "20", "-smt", "20", "-s", "false", "-dr", "false", "-lcm", "true", "-gsloc", gsLocation, "-rt", "30", "-inst", "20"};
        HotRedeployMain.main(args);

        // check deployed version after redeploy
        assertEquals(resultString, testAction.doAction());
        Person person = gigaSpace.readById(Person.class, "1");
        assertNotNull(person);
    }

    public ProcessingUnit deployV0() {
        Admin admin = new AdminFactory().create();
        String[] pathToJar = {"test-pu", "v0", "target", "space.jar"};
        File puArchive = new File(rootPath + File.separator + StringUtils.join(pathToJar, File.separator));
        log.info("puArchive " + puArchive);
        ProcessingUnit space;
        try {
            GridServiceManagers managers = admin.getGridServiceManagers();
            managers.waitFor(1);
            space = managers.deploy(new ProcessingUnitDeployment(puArchive));
            space.waitFor(2);
        } catch (AdminException e) {
            throw new HotRedeployException("Unable to identify GSM or Space. Make sure you have default lookup group and locator. Make sure that there is no pu with name \"space\" deployed already" + e.getMessage());
        }
        return space;
    }

    public String getGsLocation() {
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

    private void unzipNewSpaceJar(String deployPath, String moduleName) {
        String[] pathToJar = {"test-pu", moduleName, "target", "space.jar"};
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
        gsLocation = getGsLocation();
        if (gsLocation == null) {
            throw new HotRedeployException("You should specified path to gigaspaces folder in config.properties file");
        }
        String[] pathToDeployFolder = {"deploy", "space"};
        File oldFiles = new File(gsLocation + File.separator + StringUtils.join(pathToDeployFolder, File.separator));
        String deployPath = oldFiles.getPath();
        FileUtils.deleteDirectory(oldFiles);
        return deployPath;
    }


}
