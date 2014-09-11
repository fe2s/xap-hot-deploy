package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.Admin;
import org.openspaces.admin.pu.ProcessingUnitInstance;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anna_Babich on 05.09.2014.
 */
public class RestartAll {

    public static Logger log = LogManager.getLogger(RestartAll.class);
    private ArgsStorage argsStorage;
    private PuManager puManager;

    /**
     * Constructor.
     * @param argsStorage command line args.
     */
    public RestartAll(ArgsStorage argsStorage){
        this.argsStorage = argsStorage;
        puManager = new PuManager(argsStorage);
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
                log.info(HotRedeployMain.FAILURE);
                System.exit(1);
            }
        }
        return findBackups;
    }

    /**
     * Restart all processing unit instances.
     *
     * @param puInstances    discovered pu instances.
     * @param backupService  executor service for restart all backups at the same time
     * @param primaryService executor service for restart all primaries at the same time
     */
    private void restartAllInstances(ProcessingUnitInstance[] puInstances, ExecutorService backupService, ExecutorService primaryService) {
        restartBackups(puInstances, backupService);
        restartPrimaries(puInstances, primaryService);
        if (argsStorage.isDoubleRestart()) {
            puInstances = puManager.getPuInstances();
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
    private void restartPrimaries(ProcessingUnitInstance[] puInstances, ExecutorService primaryService) {
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
    private void shutDownAndWait(ExecutorService service) {
        service.shutdown();
        try {
            while (!service.awaitTermination(10, TimeUnit.MINUTES)) {
            }
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    public void restart(){
        Admin admin = puManager.createAdmin();
        ProcessingUnitInstance[] puInstances = puManager.getPuInstances();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        int backupsThreads = checkBackups(puInstances);
        int primariesThreads = puInstances.length - backupsThreads;

        ExecutorService backupService = Executors.newFixedThreadPool(backupsThreads);
        // if return to original state needed, primaries should restart one by one.
        if (argsStorage.isDoubleRestart()) {
            primariesThreads = 1;
        }
        ExecutorService primaryService = Executors.newFixedThreadPool(primariesThreads);
        restartAllInstances(puInstances, backupService, primaryService);
        admin.close();

    }
}
