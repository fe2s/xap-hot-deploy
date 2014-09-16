package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class HotRedeployMain {

    public static final String FAILURE = "Hot redeploy failed";
    public static final String SUCCESS = "Hot redeploy completed successfully";
    public static Logger log = LogManager.getLogger(HotRedeployMain.class);

    public static void main(String[] args) throws InterruptedException {
        ConfigInitializer.checkFiles();
        Config config = ConfigInitializer.init(args);
        PuManager puManager = new PuManager(config);
        puManager.restartAllPUs();
        log.info(SUCCESS);
    }
}