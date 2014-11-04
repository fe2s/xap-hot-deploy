package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.config.ConfigInitializer;
import org.openspaces.admin.application.hotredeploy.files.FileManager;
import org.openspaces.admin.application.hotredeploy.files.FileManagerFactory;
import org.openspaces.admin.application.hotredeploy.files.LocalFileManager;
import org.openspaces.admin.application.hotredeploy.files.SSHFileManager;
import org.openspaces.admin.application.hotredeploy.utils.PuUtils;

import java.io.Console;

public class HotRedeployMain {

    public static final String FAILURE = "Hot redeploy failed";
    public static final String SUCCESS = "Hot redeploy completed successfully";
    public static final String ROLLBACK = "Hot redeploy failed. Rollback successfully completed";
    public static Logger log = LogManager.getLogger(HotRedeployMain.class);

    public static void main(String[] args) {
        ConfigInitializer.checkFiles();
        Config config = ConfigInitializer.init(args);
        log.info(config.isLocalCluster());
        PuManager puManager = new PuManager(config);
        FileManager fileManager = FileManagerFactory.getFileManager(config);
        puManager.createAdmin();
        try {
            redeploy(puManager, config, fileManager);
        } finally {
            fileManager.removeFolder();
            puManager.closeAdmin();
        }
    }

    private static void redeploy(PuManager puManager, Config config, FileManager fileManager) {
        RollbackChecker rollbackChecker = new RollbackChecker(config, puManager, fileManager);
        boolean rollback = PuUtils.restartAllPUs(puManager, config, rollbackChecker);
        if (!rollback){
            log.info(SUCCESS);
        } else {
            log.warn(ROLLBACK);
        }

    }



}