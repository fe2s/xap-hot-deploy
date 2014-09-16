package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Anna_Babich
 */
public class StatefulPuRestarter extends PuRestarter {

    public static Logger log = LogManager.getLogger(StatefulPuRestarter.class);
    private Config config;

    /**
     * Constructor.
     * @param config command line args.
     */
    public StatefulPuRestarter(Config config){
        this.config = config;
    }

    /**
     * Restart processing unit
     * @param processingUnit current pu (stateful)
     */
    public void restart(ProcessingUnit processingUnit){
        ProcessingUnitInstance[] puInstances = getPuInstances(processingUnit);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        int backupsThreads = checkBackups(puInstances);
        int primariesThreads = puInstances.length - backupsThreads;

        ExecutorService backupService = Executors.newFixedThreadPool(backupsThreads);
        // if return to original state needed, primaries should restart one by one.
        if (config.isDoubleRestart()) {
            primariesThreads = 1;
        }
        ExecutorService primaryService = Executors.newFixedThreadPool(primariesThreads);
        restartAllInstances(processingUnit, backupService, primaryService);
    }

    /**
     * Check with user check with user if he would like to continue when no backups found.
     *
     * @param puInstances discovered processing unit instances.
     */
    private int checkBackups(ProcessingUnitInstance[] puInstances) {
        int findBackups = 0;
        for (ProcessingUnitInstance instance : puInstances) {
            if (instance.getSpaceInstance().getMode() == SpaceMode.BACKUP) {
                findBackups++;
            }
        }
        if (findBackups == 0) {
            System.out.println("No backups find. ALL SPACE DATA WILL BE LOST. Do you want to continue? [y]es, [n]o:");
            Scanner sc = new Scanner(System.in);
            String answer = sc.next();
            while (!(("y".equals(answer)) || ("n".equals(answer)))) {
                System.out.println("Error: invalid response [" + answer + "]. Try again.");
                answer = sc.next();
            }
            if ("n".equals(answer)) {
                log.error(HotRedeployMain.FAILURE);
                throw new HotRedeployException();
            }
        }
        return findBackups;
    }

    /**
     * Restart all processing unit instances.
     *
     * @param backupService  executor service for restart all backups at the same time
     * @param primaryService executor service for restart all primaries at the same time
     */
    private void restartAllInstances(ProcessingUnit processingUnit, ExecutorService backupService, ExecutorService primaryService) {
        log.info("Restarting pu " + processingUnit.getName() + " with type " + processingUnit.getType());
        ProcessingUnitInstance [] puInstances = getPuInstances(processingUnit);
        restartBackups(puInstances, backupService);
        restartPrimaries(puInstances, primaryService);
        if (config.isDoubleRestart()) {
            puInstances = getPuInstances(processingUnit);
            primaryService = Executors.newFixedThreadPool(1);
            restartPrimaries(puInstances, primaryService);
        }
    }

    /**
     * Restart backup instances.
     *
     * @param puInstances   discovered pu instances
     * @param backupService executor service for restart all backups at the same time
     */
    private void restartBackups(ProcessingUnitInstance[] puInstances, ExecutorService backupService) {
        for (ProcessingUnitInstance puInstance : puInstances) {

            if (puInstance.getSpaceInstance().getMode() == SpaceMode.BACKUP) {
                backupService.submit(new PuInstanceRestarter(puInstance));
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
    private void restartPrimaries(ProcessingUnitInstance[] puInstances, ExecutorService primaryService) {
        for (ProcessingUnitInstance puInstance : puInstances) {
            if (puInstance.getSpaceInstance().getMode() == SpaceMode.PRIMARY) {
                primaryService.submit(new PuInstanceRestarter(puInstance));
            }
        }
        shutDownAndWait(primaryService);
    }

    /**
     * Shutdown threads and wait for terminate.
     *
     * @param service current executor service
     */
    private void shutDownAndWait(ExecutorService service) {
        service.shutdown();
        try {
            while (!service.awaitTermination(10, TimeUnit.MINUTES)) {
            }
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    /**
     * Identify pu instances and wait for identify space mode of each instances.
     * @return discovered pu instances
     */
    public ProcessingUnitInstance[] getPuInstances(ProcessingUnit processingUnit){
        ProcessingUnitInstance[] puInstances = identifyPuInstances(processingUnit);
        identSpaceMode(puInstances);
        return puInstances;
    }

    /**
     * Discover space mode for all pu instances.
     *
     * @param puInstances discovered processing unit instances.
     */
    private void identSpaceMode(ProcessingUnitInstance[] puInstances) {
        Long timeout = System.currentTimeMillis() + config.getIdentifySpaceModeTimeout() * 1000;
        boolean keepTrying = true;

        while (keepTrying) {
            if (System.currentTimeMillis() >= timeout) {
                log.error("can't identify space mode");
                log.error(HotRedeployMain.FAILURE);
                throw new HotRedeployException();
            }
            keepTrying = false;
            for (ProcessingUnitInstance instance : puInstances) {
                if ((instance.getSpaceInstance() == null) || (instance.getSpaceInstance().getMode() == SpaceMode.NONE)) {
                    keepTrying = true;
                }
            }
        }
    }
}
