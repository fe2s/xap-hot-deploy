package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import java.io.Console;
import java.util.concurrent.TimeUnit;

public class HotRedeployMain {

    public static final String FAILURE = "Hot redeploy is FAILURE";
    public static final String SUCCESS = "Hot redeploy is SUCCESS";
    public static Logger log = LogManager.getLogger(HotRedeployMain.class);


    public static void main(String[] args) throws InterruptedException {
        if (args.length < 6) {
            log.error("Must be specified processing unit name, locator, lookup group, timeouts");
            log.info(FAILURE);
            System.exit(1);
        }

        Console console = System.console();
        checkFiles(console);

        String puToRestart = args[0];
        String locator = args[1];
        String lookupGroup = args[2];
        Long identifyPuTimeout = Long.parseLong(args[3]);
        Long identifySpaceModeTimeout = Long.parseLong(args[4]);
        Long restartAndWaitTimeout = Long.parseLong(args[5]);

        AdminFactory adminFactory = new AdminFactory().addLocator(locator).addGroup(lookupGroup);

        boolean isSecured = args.length >= 7 && Boolean.parseBoolean(args[6]);
        if (isSecured) {
            String user = console.readLine("%s", "User: ");
            String password = String.valueOf(console.readPassword("%s", "Password: "));
            adminFactory.credentials(user, password);
        }

        Admin admin = adminFactory.createAdmin();

        ProcessingUnit processingUnit = admin.getProcessingUnits().waitFor(
                puToRestart, identifyPuTimeout, TimeUnit.SECONDS);

        if (processingUnit == null) {
            log.error("can't get PU instances for " + puToRestart);
            log.info(FAILURE);
            admin.close();
            System.exit(0);
        }

        // Wait for all the members to be discovered
        processingUnit.waitFor(processingUnit.getTotalNumberOfInstances());

        ProcessingUnitInstance[] puInstances = processingUnit.getInstances();

        identSpaceMode(puInstances, identifySpaceModeTimeout);

        Thread.sleep(1000);

        checkBackups(puInstances, console);

        // restart all backups
        for (int i = 0; i < puInstances.length; i++) {

            if (puInstances[i].getSpaceInstance().getMode() == SpaceMode.BACKUP) {
                restartPUInstance(puInstances[i], restartAndWaitTimeout);
            }
        }

        // restart all primaries
        for (int i = 0; i < puInstances.length; i++) {
            if (puInstances[i].getSpaceInstance().getMode() == SpaceMode.PRIMARY) {
                restartPUInstance(puInstances[i], restartAndWaitTimeout);
            }
        }
        admin.close();
        log.info(SUCCESS);
        System.exit(0);
    }

    private static void restartPUInstance(
            ProcessingUnitInstance pi, long restartAndWaitTimeout) {
        final String instStr = pi.getSpaceInstance().getMode() != SpaceMode.PRIMARY ? "backup" : "primary";
        log.info("restarting instance " + pi.getInstanceId()
                + " on " + pi.getMachine().getHostName() + "["
                + pi.getMachine().getHostAddress() + "] GSC PID:"
                + pi.getVirtualMachine().getDetails().getPid() + " mode:"
                + instStr + "...");

        pi = pi.restartAndWait(restartAndWaitTimeout, TimeUnit.SECONDS);
        log.info("done");
    }

    private static void checkFiles(Console console){
        System.out.println("Are you sure that new files placed on all GSM machines? [y]es, [n]o: ");
        String answer = console.readLine();
        while(!(("y".equals(answer))||("n".equals(answer)))){
            System.out.println("Error: invalid response [" + answer + "]. Try again.");
            answer = console.readLine();
        }
        if ("n".equals(answer)){
            log.info("Please place new files on all GSM machines and try again.");
            log.info(FAILURE);
            System.exit(0);
        }
    }

    private static void checkBackups(ProcessingUnitInstance[] puInstances, Console console){
        Boolean findBackup = false;
        for (ProcessingUnitInstance instance : puInstances) {
            if (instance.getSpaceInstance().getMode() == SpaceMode.BACKUP) {
                findBackup = true;
            }
        }
        if (!findBackup){
            System.out.println("No backups find. Do you want to continue? [y]es, [n]o:");
            String answer = console.readLine();
            while(!(("y".equals(answer))||("n".equals(answer)))){
                System.out.println("Error: invalid response [" + answer + "]. Try again.");
                answer = console.readLine();
            }
            if ("n".equals(answer)){
                log.info(FAILURE);
                System.exit(0);
            }
        }
    }

    private static void identSpaceMode(ProcessingUnitInstance[] puInstances, Long identifySpaceModeTimeout){
        Long timeout = System.currentTimeMillis() + identifySpaceModeTimeout * 1000;
        boolean keepTrying = true;

        while (keepTrying) {
            if (System.currentTimeMillis() >= timeout ) {
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
}