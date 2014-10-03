package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;
import org.openspaces.admin.application.hotredeploy.utils.PuUtils;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Anna_Babich
 */
public class StatefulPuRestarter implements PuRestarter {

    public static Logger log = LogManager.getLogger(StatefulPuRestarter.class);
    private Config config;
    private RollbackChecker rollbackChecker;

    /**
     * Constructor.
     *
     * @param config command line args.
     */
    public StatefulPuRestarter(Config config, RollbackChecker rollbackChecker) {
        this.config = config;
        this.rollbackChecker = rollbackChecker;
    }

    /**
     * Restart processing unit
     *
     * @param processingUnit current pu (stateful)
     */
    public void restart(ProcessingUnit processingUnit) {
        ProcessingUnitInstance[] puInstances = PuUtils.getStatefulPuInstances(processingUnit, config);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        int backupsThreads = checkBackups(puInstances);
        int primariesThreads = puInstances.length - backupsThreads;

        ExecutorService backupService;

        if (backupsThreads == 0) {
            backupService = Executors.newFixedThreadPool(1);
        } else {
            backupService = Executors.newFixedThreadPool(backupsThreads);
        }

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
                String cause = "Hot redeploy was terminated, because no backups find";
                log.error(cause);
                log.error(HotRedeployMain.FAILURE);
                throw new HotRedeployException(cause);
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
        ProcessingUnitInstance[] puInstances = PuUtils.getStatefulPuInstances(processingUnit, config);
        restartBackups(puInstances, backupService);
        boolean rollback = rollbackChecker.tryRollback("Backup restarting fails. If you don't rollback all data will be lost");
        if (!rollback) {
            restartPrimaries(puInstances, primaryService);
            rollback = rollbackChecker.tryRollback("Primary restarting fails. If you don't rollback all data will be lost");
            if ((!rollback) && (config.isDoubleRestart())){
                    puInstances = PuUtils.getStatefulPuInstances(processingUnit, config);
                    primaryService = Executors.newFixedThreadPool(1);
                    restartPrimaries(puInstances, primaryService);
                    rollbackChecker.tryRollback("Primary restarting fails. If you don't rollback all data will be lost");
            }
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
                backupService.submit(new PuInstanceRestarter(puInstance, config.getRestartTimeout()));
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
                primaryService.submit(new PuInstanceRestarter(puInstance, config.getRestartTimeout()));
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
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            log.error(e);
        }
    }
}
