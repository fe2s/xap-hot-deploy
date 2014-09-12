package org.openspaces.admin.application.hotredeploy;

import com.beust.jcommander.JCommander;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 * @author Anna_Babich
 */
public class ConfigInitializer {

    public static Logger log = LogManager.getLogger(ConfigInitializer.class);

    /**
     * Init configs.
     * @param args command line arguments
     * @return Config object with parsed arguments
     */
    public static Config init(String[] args) {
        checkFiles();
        Config config = new Config();
        new JCommander(config, args);

        log.info("Pu to restart: " + config.getPuToRestart());
        log.info("Locator: " + config.getLocator());
        log.info("Lookup group: " + config.getLookupGroup());
        log.info("Timeout for identify pu: " + config.getIdentifyPuTimeout());
        log.info("Timeout for identify space mode: " + config.getIdentifySpaceModeTimeout());
        log.info("Secured: " + config.isSecured());
        log.info("Double restart: " + config.isDoubleRestart());
        return config;
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
        if ("n".equals(answer)) {
            log.error("Please place new files on all GSM machines and try again.");
            log.error(HotRedeployMain.FAILURE);
            System.exit(1);
        }
    }
}
