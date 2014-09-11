package org.openspaces.admin.application.hotredeploy;

import com.beust.jcommander.JCommander;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * Created by Anna_Babich on 11.09.2014.
 */
public class StorageInitializer {

    public static Logger log = LogManager.getLogger(StorageInitializer.class);

    public static void init(ArgsStorage argsStorage, String[] args) {
        checkFiles();

        new JCommander(argsStorage, args);

        log.info("Pu to restart: " + argsStorage.getPuToRestart());
        log.info("Locator: " + argsStorage.getLocator());
        log.info("Lookup group: " + argsStorage.getLookupGroup());
        log.info("Timeout for identify pu: " + argsStorage.getIdentifyPuTimeout());
        log.info("Timeout for identify space mode: " + argsStorage.getIdentifySpaceModeTimeout());
        log.info("Secured: " + argsStorage.isSecured());
        log.info("Double restart: " + argsStorage.isDoubleRestart());

    }

    /**
     * Check with the user if new files placed on all GSM machines.
     */
    private static void checkFiles() {
        System.out.println("Are you sure that new files placed on all GSM machines? [y]es, [n]o: ");
        Scanner sc = new Scanner(System.in);
        String answer = sc.next();
        while (!(("y".equals(answer)) || ("n".equals(answer)))) {
            System.out.println("Error: invalid response [" + answer + "]. Try again.");
            answer = sc.next();
        }
        sc.close();
        if ("n".equals(answer)) {
            log.info("Please place new files on all GSM machines and try again.");
            log.info(HotRedeployMain.FAILURE);
            System.exit(1);
        }
    }
}
