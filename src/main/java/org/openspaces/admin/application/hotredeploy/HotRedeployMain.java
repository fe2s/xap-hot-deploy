package org.openspaces.admin.application.hotredeploy;

import com.beust.jcommander.JCommander;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class HotRedeployMain {

    public static final String FAILURE = "Hot redeploy failed";
    public static final String SUCCESS = "Hot redeploy completed successfully";
    public static Logger log = LogManager.getLogger(HotRedeployMain.class);

    public static void main(String[] args) throws InterruptedException {
        ArgsStorage argsStorage = new ArgsStorage();
        StorageInitializer.init(argsStorage, args);
        RestartAll restartAll = new RestartAll(argsStorage);
        restartAll.restart();
        log.info(SUCCESS);
        System.exit(0);
    }
}