package org.openspaces.admin.application.hotredeploy.config;

import com.beust.jcommander.JCommander;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;
import org.openspaces.admin.application.hotredeploy.HotRedeployMain;

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
        Config config = new Config();
        new JCommander(config, args);

        log.info("Gigaspaces location: " + config.getGigaspacesLocation());
        log.info("Pu to restart: " + config.getPuToRestart());
        log.info("Locator: " + config.getLocator());
        log.info("Lookup group: " + config.getLookupGroup());
        log.info("Timeout for identify pu: " + config.getIdentifyPuTimeout());
        log.info("Timeout for identify space mode: " + config.getIdentifySpaceModeTimeout());
        log.info("Timeout for restart " + config.getRestartTimeout());
        log.info("Secured: " + config.isSecured());
        log.info("Double restart: " + config.isDoubleRestart());
        log.info("GSM Hosts: " + config.getGsmHosts());
        log.info("User: " + config.getSshUser());
        log.info("Is local cluster: " + config.isLocalCluster());
        return config;
    }

    /**
     * Check with the user if new files placed on all GSM machines.
     */
    public static void checkFiles() {
        System.out.println("Are you sure that new files placed on all GSM machines? [y]es, [n]o: ");
        Scanner sc = new Scanner(System.in);
        String answer = sc.next();
        while (!(("y".equals(answer)) || ("n".equals(answer)))) {
            System.out.println("Error: invalid response [" + answer + "]. Try again.");
            answer = sc.next();
        }
        if ("n".equals(answer)) {
            String cause = "Please place new files on all GSM machines and try again.";
            log.error(cause);
            log.error(HotRedeployMain.FAILURE);
            throw new HotRedeployException(cause);
        }
    }
}
