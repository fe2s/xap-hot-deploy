package org.openspaces.admin.application.hotredeploy;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * @autor Anna_Babich
 */
public class ConfigInitializerTest {
    public static final String TIMEOUT = "100";
    public static final String LOCATOR = "127.0.0.1";
    public static final String GROUP = "group";
    public static final String SECURE = "true";
    public static final String RESTART = "true";
    public static final String PU_NAME1 = "space";
    public static final String PU_NAME2 = "cinema";
    public static final String PU_NAME3 = "mirror";

    @Test
    public void testForAllParameters(){
        String args[] = {"-pun", PU_NAME1, "-put", TIMEOUT, "-smt", TIMEOUT, "-s", SECURE, "-dr", RESTART, "-pun", PU_NAME2, "-pun", PU_NAME3, "-gsl", LOCATOR, "-gsg", GROUP};
        Config config = ConfigInitializer.init(args);
        assertEquals(config.getIdentifyPuTimeout(), (Long) Long.parseLong(TIMEOUT));
        assertEquals(config.getIdentifySpaceModeTimeout(),(Long) Long.parseLong(TIMEOUT));
        assertEquals(config.getLocator(), LOCATOR);
        assertEquals(config.getLocator(), LOCATOR);
        assertEquals(config.getLookupGroup(), GROUP);
        assertEquals(config.isDoubleRestart(), Boolean.parseBoolean(RESTART));
        assertEquals(config.isSecured(), Boolean.parseBoolean(SECURE));
        assertTrue(config.getPuToRestart().contains(PU_NAME1));
        assertTrue(config.getPuToRestart().contains(PU_NAME2));
        assertTrue(config.getPuToRestart().contains(PU_NAME3));
        assertEquals(config.getPuToRestart().size(), 3);
    }

    @Test
    public void testForDefaultParameters(){
        String args[] = {"-pun", PU_NAME1, "-put", TIMEOUT, "-smt", TIMEOUT, "-pun", PU_NAME2, "-pun", PU_NAME3};
        Config config = ConfigInitializer.init(args);
        assertNull(config.getLocator());
        assertNull(config.getLookupGroup());
        assertFalse(config.isSecured());
        assertFalse(config.isDoubleRestart());
    }
}
