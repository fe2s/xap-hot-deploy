package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

import java.io.Console;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HotRedeployMain {

    public static final String FAILURE = "Hot redeploy failed";
    public static final String SUCCESS = "Hot redeploy completed successfully";
    public static Logger log = LogManager.getLogger(HotRedeployMain.class);
    public static final Console console = System.console();
    private static String puToRestart;
    private static String locator = null;
    private static String lookupGroup = null;
    private static Long identifyPuTimeout;
    private static Long identifySpaceModeTimeout;
    private static boolean isSecure = false;


    public static void main(String[] args) throws InterruptedException {
        checkFiles();
        try {
            checkArgs(args);
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage());
            log.info(FAILURE);
            System.exit(1);
        }

        AdminFactory adminFactory = new AdminFactory();
        if (locator != null) {
            adminFactory.addLocator(locator);
        }
        if (lookupGroup != null) {
            adminFactory.addGroup(lookupGroup);
        }
        authorize(args, adminFactory);
        Admin admin = adminFactory.createAdmin();
        ProcessingUnitInstance[] puInstances = identifyPuInstances(admin, puToRestart, identifyPuTimeout);
        identSpaceMode(puInstances, identifySpaceModeTimeout);
        Thread.sleep(1000);

        int backupsAmount = checkBackups(puInstances);
        int primariesAmount = puInstances.length - backupsAmount;

        ExecutorService backupService = Executors.newFixedThreadPool(backupsAmount);
        ExecutorService primaryService = Executors.newFixedThreadPool(primariesAmount);

        restartAllInstances(puInstances, backupService, primaryService);

        admin.close();
        log.info(SUCCESS);
        System.exit(0);
    }


    /**
     * Check with the user if new files placed on all GSM machines.
     */
    private static void checkFiles() {
        System.out.println("Are you sure that new files placed on all GSM machines? [y]es, [n]o: ");
        String answer = console.readLine();
        while (!(("y".equals(answer)) || ("n".equals(answer)))) {
            System.out.println("Error: invalid response [" + answer + "]. Try again.");
            answer = console.readLine();
        }
        if ("n".equals(answer)) {
            log.info("Please place new files on all GSM machines and try again.");
            log.info(FAILURE);
            System.exit(1);
        }
    }

    /**
     * Check with user check with user if he would like to continue when no backups found.
     *
     * @param puInstances discovered processing unit instances.
     */
    private static int checkBackups(ProcessingUnitInstance[] puInstances) {
        int findBackups = 0;
        for (ProcessingUnitInstance instance : puInstances) {
            if (instance.getSpaceInstance().getMode() == SpaceMode.BACKUP) {
                findBackups++;
            }
        }
        if (findBackups == 0) {
            System.out.println("No backups find. ALL SPACE DATA WILL BE LOST. Do you want to continue? [y]es, [n]o:");
            String answer = console.readLine();
            while (!(("y".equals(answer)) || ("n".equals(answer)))) {
                System.out.println("Error: invalid response [" + answer + "]. Try again.");
                answer = console.readLine();
            }
            if ("n".equals(answer)) {
                log.info(FAILURE);
                System.exit(1);
            }
        }
        return findBackups;
    }

    /**
     * Discover space mode for all pu instances.
     *
     * @param puInstances              discovered processing unit instances.
     * @param identifySpaceModeTimeout if timeout expire earlier than pu modes will be discover - deploy will failed.
     */
    private static void identSpaceMode(ProcessingUnitInstance[] puInstances, Long identifySpaceModeTimeout) {
        Long timeout = System.currentTimeMillis() + identifySpaceModeTimeout * 1000;
        boolean keepTrying = true;

        while (keepTrying) {
            if (System.currentTimeMillis() >= timeout) {
                log.error("can't identify space mode");
                log.info(FAILURE);
                System.exit(1);
            }
            keepTrying = false;
            for (ProcessingUnitInstance instance : puInstances) {
                if (instance.getSpaceInstance().getMode() == SpaceMode.NONE) {
                    keepTrying = true;
                }
            }
        }
    }

    /**
     * Authorize user, if secure is turn on.
     *
     * @param args         command-line arguments.
     * @param adminFactory current admin factory.
     */
    private static void authorize(String[] args, AdminFactory adminFactory) {
        boolean isSecured = args.length >= 7 && Boolean.parseBoolean(args[6]);
        if (isSecured) {
            String user = console.readLine("%s", "User: ");
            String password = String.valueOf(console.readPassword("%s", "Password: "));
            adminFactory.credentials(user, password);
        }
    }

    /**
     * Check if all command-line arguments were entered.
     *
     * @param args command-line arguments
     */
    private static void checkArgs(String[] args) {
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
                isSecure = Boolean.getBoolean(parseValue(key, args[i + 1], length, i));
            }
        }
    }

    private static String parseValue(String key, String value, int length, int i) {
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
     * Discover all processing unit instances.
     *
     * @param admin             current admin
     * @param puToRestart       name of restarting processing unit
     * @param identifyPuTimeout timeout for pu discovering
     * @return
     */
    private static ProcessingUnitInstance[] identifyPuInstances(Admin admin, String puToRestart, Long identifyPuTimeout) {
        ProcessingUnit processingUnit = admin.getProcessingUnits().waitFor(
                puToRestart, identifyPuTimeout, TimeUnit.SECONDS);
        if (processingUnit == null) {
            log.error("can't get PU instances for " + puToRestart);
            log.info(FAILURE);
            admin.close();
            System.exit(1);
        }
        // Wait for all the members to be discovered
        processingUnit.waitFor(processingUnit.getTotalNumberOfInstances());
        return processingUnit.getInstances();
    }

    /**
     * Restart all processing unit instances.
     *
     * @param puInstances    discovered pu instances.
     * @param backupService  executor service for restart all backups at the same time
     * @param primaryService executor service for restart all primaries at the same time
     */
    private static void restartAllInstances(ProcessingUnitInstance[] puInstances, ExecutorService backupService, ExecutorService primaryService) {
        restartBackups(puInstances, backupService);
        restartPrimaries(puInstances, primaryService);
    }

    /**
     * Restart backup instances.
     *
     * @param puInstances   discovered pu instances
     * @param backupService executor service for restart all backups at the same time
     */
    private static void restartBackups(ProcessingUnitInstance[] puInstances, ExecutorService backupService) {
        for (ProcessingUnitInstance puInstance : puInstances) {

            if (puInstance.getSpaceInstance().getMode() == SpaceMode.BACKUP) {
                backupService.submit(new RestartInstance(puInstance));
            }
        }
        shutDownAndWait(backupService);
    }

    /**
     * Restart primary instances.
     *
     * @param puInstances    discovered pu instances
     * @param primaryService executor service for restart all primaries at the same time
     */
    private static void restartPrimaries(ProcessingUnitInstance[] puInstances, ExecutorService primaryService) {
        for (ProcessingUnitInstance puInstance : puInstances) {
            if (puInstance.getSpaceInstance().getMode() == SpaceMode.PRIMARY) {
                primaryService.submit(new RestartInstance(puInstance));
            }
        }
        shutDownAndWait(primaryService);
    }

    /**
     * Shutdown threads and wait for terminate.
     *
     * @param service current executor service
     */
    private static void shutDownAndWait(ExecutorService service) {
        service.shutdown();
        try {
            while (!service.awaitTermination(10, TimeUnit.MINUTES)) {
            }
        } catch (InterruptedException e) {
            log.error(e);
        }
    }
}