package org.openspaces.admin.application.hotredeploy.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.HotRedeployMain;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;
import org.openspaces.admin.application.hotredeploy.utils.ScannerHolder;

import java.util.Scanner;

/**
 * @author Anna_Babich
 */
public class Validation {
    public static Logger log = LogManager.getLogger(Validation.class);

    public static void validate(Config config){
        validateConfig(config);
        checkFiles();
    }

    /**
     * Check with the user if new files placed on all GSM machines.
     */
    private static void checkFiles() {
        System.out.println("Are you sure that new files placed on all GSM machines? [y]es, [n]o: ");
        Scanner sc = ScannerHolder.getScanner();
        String answer = sc.nextLine();
        while (!(("y".equals(answer)) || ("n".equals(answer)))) {
            System.out.println("Error: invalid response [" + answer + "]. Try again.");
            answer = sc.nextLine();
        }
        if ("n".equals(answer)) {
            String cause = "Please place new files on all GSM machines and try again.";
            log.error(cause);
            log.error(HotRedeployMain.FAILURE);
            throw new HotRedeployException(cause);
        }
    }

    private static void validateConfig(Config configuration){
        if (configuration.getGigaspacesLocation().equals("")) {
            throw new HotRedeployException("Gigaspaces location must be set");
        }
        if (configuration.getPusToRestart().size() == 0) {
            throw new HotRedeployException("PU name and file must be set");
        }
    }
}
