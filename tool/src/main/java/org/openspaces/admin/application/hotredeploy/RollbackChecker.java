package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;
import org.openspaces.admin.application.hotredeploy.files.FileManager;
import org.openspaces.admin.application.hotredeploy.utils.PuUtils;
import org.openspaces.admin.application.hotredeploy.utils.ScannerHolder;
import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.pu.ProcessingUnitType;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author Anna_Babich
 */
public class RollbackChecker {
    public static final int PAUSE = 100;
    public static final int WAIT_FOR_GSM_INIT = 5000;
    public static Logger log = LogManager.getLogger(RollbackChecker.class);

    private Config config;
    private PuManager puManager;
    private FileManager sshFileManager;

    public RollbackChecker(Config config, PuManager puManager, FileManager sshFileManager) {
        this.config = config;
        this.puManager = puManager;
        this.sshFileManager = sshFileManager;
    }

    private void checkForErrors() {
        List<ProcessingUnit> processingUnits = puManager.identProcessingUnits(config.getPuToRestart());
        for (ProcessingUnit processingUnit : processingUnits) {
            processingUnit.waitFor(processingUnit.getPlannedNumberOfInstances(), config.getIdentifyInstancesTimeout(), TimeUnit.SECONDS);
            ProcessingUnitInstance[] instances = processingUnit.getInstances();
            if (instances.length != processingUnit.getPlannedNumberOfInstances()) {
                throw new HotRedeployException("Processing unit instances has been lost");
            }
            if (processingUnit.getType() == ProcessingUnitType.STATEFUL) {
                PuUtils.identSpaceMode(instances, config);
            }
        }
    }

    private boolean checkWithUser(String message) {
        System.out.println(message + " Do you want to rollback? [y]es/[n]o: ");
        Scanner sc = ScannerHolder.getScanner();
        String answer = sc.nextLine();
        while (!(("y".equals(answer)) || ("n".equals(answer)))) {
            System.out.println("Error: invalid response [" + answer + "]. Try again.");
            answer = sc.nextLine();
        }
        return "y".equals(answer);
    }

    //Add javadoc with explanation of GSM/empty GSC restarting logic
    private void doRollback(Config config) {
        sshFileManager.restoreTempFolders();
        log.info("Do rollback..");
        GridServiceManager[] managers = puManager.getMangers();
        if (managers.length > 1) {
            log.info("Find backup GSM in system Restarting GSMs..");
            restartGSMs(managers);
        } else {
            log.info("There is one GSM in system. Try to find empty GSC");
            GridServiceContainer gsc = findEmptyContainer();
            if (gsc != null) {
                log.info("Restarting GSC with id " + gsc.getAgentId());
               gsc.restart();
            } else {
                throw new HotRedeployException("There is no empty GSC in system. If you want to rollback please start new GSC manually");
            }
        }
        List<ProcessingUnit> processingUnits = puManager.identProcessingUnits(config.getPuToRestart());
        for (ProcessingUnit processingUnit : processingUnits) {
            processingUnit.waitFor(processingUnit.getPlannedNumberOfInstances(), config.getRestartTimeout(), TimeUnit.SECONDS);
        }
        log.info("Rollback completed successfully");

    }

    public boolean checkForRollback(String message){
        boolean isRollbackDone = false;
        try{
            checkForErrors();
        } catch (HotRedeployException e) {
            isRollbackDone = true;
            if(checkWithUser(message)){
                doRollback(config);
            } else {
                throw new HotRedeployException("Hot redeploy failed. Rollback canceled by user.");
            }
        }
        return isRollbackDone;
    }


    private void restartGSMs(GridServiceManager[] managers){
        int numberOfManagers = managers.length;
        for (GridServiceManager gsm:managers){
            log.info("Restarting gsm with id " + gsm.getAgentId());
            gsm.restart();
            try {
                // can it be replaced by admin.getGridServiceManagers().wait(numberOfManagers) ?
                while (puManager.getMangers().length < numberOfManagers) {
                    Thread.sleep(PAUSE);
                }
                Thread.sleep(WAIT_FOR_GSM_INIT);
            } catch (InterruptedException e) {
                log.error(e);
            }
            log.info("done");
        }
    }

    private GridServiceContainer findEmptyContainer(){
        GridServiceContainer[] containers = puManager.getContainers();
        for (GridServiceContainer gsc : containers){
            if (gsc.getProcessingUnitInstances().length == 0) {
                return gsc;
            }
        }
        return null;
    }
}
