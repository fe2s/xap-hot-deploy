package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.config.PropertyParser;
import org.openspaces.admin.application.hotredeploy.config.Validation;
import org.openspaces.admin.application.hotredeploy.files.FileManager;
import org.openspaces.admin.application.hotredeploy.files.FileManagerFactory;
import org.openspaces.admin.application.hotredeploy.utils.PuUtils;

public class HotRedeployMain {

    public static final String FAILURE = "Hot redeploy failed";
    public static final String SUCCESS = "Hot redeploy completed successfully";
    public static final String ROLLBACK = "Hot redeploy failed. Rollback successfully completed";
    public static Logger log = LogManager.getLogger(HotRedeployMain.class);

    public static void main(String[] args) {
        Config config = PropertyParser.parse();
        Validation.validate(config);
        FileManager fileManager = FileManagerFactory.getFileManager(config);
        fileManager.prepareFiles();
        PuManager puManager = new PuManager(config);
        puManager.createAdmin();
        try {
            redeploy(puManager, config, fileManager);
        } finally {
            fileManager.removeTempFolder();
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