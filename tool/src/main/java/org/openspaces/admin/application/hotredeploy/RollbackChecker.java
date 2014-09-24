package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;
import org.openspaces.admin.application.hotredeploy.files.FileManager;
import org.openspaces.admin.application.hotredeploy.utils.PuUtils;
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
    public static Logger log = LogManager.getLogger(RollbackChecker.class);

    private Config config;
    private PuManager puManager;
    private FileManager sshFileManager;

    public RollbackChecker(Config config, PuManager puManager, FileManager sshFileManager) {
        this.config = config;
        this.puManager = puManager;
        this.sshFileManager = sshFileManager;
    }

    public void checkForErrors() {
        List<ProcessingUnit> processingUnits = puManager.identProcessingUnits();
        for (ProcessingUnit processingUnit : processingUnits) {
            //TODO Add 15 to config
            processingUnit.waitFor(processingUnit.getPlannedNumberOfInstances(), 15, TimeUnit.SECONDS);
            ProcessingUnitInstance[] instances = processingUnit.getInstances();
            if (instances.length != processingUnit.getPlannedNumberOfInstances()) {
                throw new HotRedeployException("Processing unit instances has been lost");
            }
            if (processingUnit.getType() == ProcessingUnitType.STATEFUL) {
                PuUtils.identSpaceMode(instances, config);
            }
        }
    }

    public boolean isRollbackNeed(String message) {
        System.out.println(message + " Do you want to rollback? [y]es/[n]o: ");
        Scanner sc = new Scanner(System.in);
        String answer = sc.next();
        while (!(("y".equals(answer)) || ("n".equals(answer)))) {
            System.out.println("Error: invalid response [" + answer + "]. Try again.");
            answer = sc.next();
        }
        return "y".equals(answer);
    }

    public void doRollback(Config config) {
        sshFileManager.restoreTempFolders();
        log.info("do rollback");
        PuUtils.restartAllPUs(puManager, config, this);
    }
}
