package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Scanner;

public class HotRedeployMain {

    public static final String FAILURE = "Hot redeploy failed";
    public static final String SUCCESS = "Hot redeploy completed successfully";
    public static Logger log = LogManager.getLogger(HotRedeployMain.class);

    public static void main(String[] args) throws InterruptedException {
        ConfigInitializer.checkFiles();
        Config config = ConfigInitializer.init(args);
        PuManager puManager = new PuManager(config);
        puManager.createAdmin();
        RollbackChecker rollbackChecker = new RollbackChecker(config, puManager);
        PuUtils.restartAllPUs(puManager, config, rollbackChecker);


        try{
            rollbackChecker.checkForErrors();
        } catch (HotRedeployException e) {
            if(rollbackChecker.isRollbackNeed("Hot redeploy fails. If you don't rollback all data will be lost")){
                rollbackChecker.doRollback();
            }
        }

        FileUtils.cleanTempDirectory();
        puManager.closeAdmin();
        log.info(SUCCESS);
    }


}