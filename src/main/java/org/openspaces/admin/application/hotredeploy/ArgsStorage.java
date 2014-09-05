package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.Console;

/**
 * Created by Anna_Babich on 05.09.2014.
 */
public class ArgsStorage {
    public static Logger log = LogManager.getLogger(ArgsStorage.class);
    private String puToRestart;
    private String locator = null;
    private String lookupGroup = null;
    private Long identifyPuTimeout;
    private Long identifySpaceModeTimeout;
    private boolean isSecured = false;
    private boolean doubleRestart = false;

    public String getPuToRestart() {
        return puToRestart;
    }

    public String getLocator() {
        return locator;
    }

    public String getLookupGroup() {
        return lookupGroup;
    }

    public Long getIdentifyPuTimeout() {
        return identifyPuTimeout;
    }

    public Long getIdentifySpaceModeTimeout() {
        return identifySpaceModeTimeout;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public boolean isDoubleRestart() {
        return doubleRestart;
    }

    /**
     * Parse and check command line arguments.
     * Check with the user if new files placed on all GSM machines.
     * @param args
     */
    public void validate(String[] args) {
        checkFiles();
        try {
            checkArgs(args);
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());
            log.info(HotRedeployMain.FAILURE);
            System.exit(1);
        }
    }

    /**
     * Check if all command-line arguments were entered right.
     * Parse arguments.
     * @param args command-line arguments
     */
    private void checkArgs(String[] args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Incorrect number of parameters");
        }
        int length = args.length;

        for (int i = 0; i < args.length; i++) {
            String key = args[i];
            if ("-pun".equals(key) || ("-pu_name".equals(key))) {
                puToRestart = parseValue(key, args[i + 1], length, i);
            } else if ("-gsl".equals(key) || ("-gs_locator".equals(key))) {
                locator = parseValue(key, args[i + 1], length, i);
            } else if ("-gsg".equals(key) || ("-gs_group".equals(key))) {
                lookupGroup = parseValue(key, args[i + 1], length, i);
            } else if ("-put".equals(key) || ("-pu_timeout".equals(key))) {
                identifyPuTimeout = Long.parseLong(parseValue(key, args[i + 1], length, i));
            } else if ("-smt".equals(key) || ("-space_mode_timeout".equals(key))) {
                identifySpaceModeTimeout = Long.parseLong(parseValue(key, args[i + 1], length, i));
            } else if ("-s".equals(key) || ("-secured".equals(key))) {
                isSecured = Boolean.parseBoolean(parseValue(key, args[i + 1], length, i));
            } else if ("-dr".equals(key) || ("-double_restart".equals(key))) {
                doubleRestart = Boolean.parseBoolean(parseValue(key, args[i + 1], length, i));
            }
        }
        log.info("Pu to restart: " + puToRestart);
        log.info("Locator: " + locator);
        log.info("Lookup group: " + lookupGroup);
        log.info("Timeout for identify pu: " + identifyPuTimeout);
        log.info("Timeout for identify space mode: " + identifySpaceModeTimeout);
        log.info("Secured: " + isSecured);
        log.info("Double restart: " + doubleRestart);
    }

    /**
     * Check value after specified key
     * @param key - command-line key
     * @param value - command-line argument
     * @param length - length of args array
     * @param i - current number of arg
     * @return value, if it valid
     */
    private String parseValue(String key, String value, int length, int i) {
        if (i + 1 >= length) {
            throw new IllegalArgumentException("After parameter " + key + " should be a value");
        }
        if (!value.startsWith("-")) {
            return value;
        } else {
            throw new IllegalArgumentException("Incorrect value of key: " + key);
        }
    }

    /**
     * Check with the user if new files placed on all GSM machines.
     */
    private void checkFiles() {
        System.out.println("Are you sure that new files placed on all GSM machines? [y]es, [n]o: ");
        Console console = System.console();
        String answer = console.readLine();
        while (!(("y".equals(answer)) || ("n".equals(answer)))) {
            System.out.println("Error: invalid response [" + answer + "]. Try again.");
            answer = console.readLine();
        }
        if ("n".equals(answer)) {
            log.info("Please place new files on all GSM machines and try again.");
            log.info(HotRedeployMain.FAILURE);
            System.exit(1);
        }
    }
}
