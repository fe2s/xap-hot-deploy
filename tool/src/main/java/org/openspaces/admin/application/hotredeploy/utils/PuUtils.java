package org.openspaces.admin.application.hotredeploy.utils;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.*;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.pu.ProcessingUnitType;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Anna_Babich
 */
public class PuUtils {

    public static Logger log = LogManager.getLogger(PuUtils.class);

    /**
     * Discover space mode for all pu instances.
     *
     * @param puInstances discovered processing unit instances.
     */
    public static void identSpaceMode(ProcessingUnitInstance[] puInstances, Config config) {
        Long timeout = System.currentTimeMillis() + config.getIdentifySpaceModeTimeout() * 1000;
        boolean keepTrying = true;

        while (keepTrying) {
            if (System.currentTimeMillis() >= timeout) {
                String cause = "can't identify space mode";
                log.error(cause);
                log.error(HotRedeployMain.FAILURE);
                throw new HotRedeployException(cause);
            }
            keepTrying = false;
            for (ProcessingUnitInstance instance : puInstances) {
                if ((instance.getSpaceInstance() == null) || (instance.getSpaceInstance().getMode() == SpaceMode.NONE)) {
                    keepTrying = true;
                }
            }
        }
    }
    /**
     * Identify pu instances and wait for identify space mode of each instances.
     * @return discovered pu instances
     */
    public static ProcessingUnitInstance[] getStatefulPuInstances(ProcessingUnit processingUnit, Config config){
        ProcessingUnitInstance[] puInstances = getPuInstances(processingUnit);
        identSpaceMode(puInstances, config);
        return puInstances;
    }

    /**
     *
     * @param processingUnit current pu.
     * @return pu instances
     */
    public static ProcessingUnitInstance[] getPuInstances(ProcessingUnit processingUnit) {
        int i = processingUnit.getPlannedNumberOfInstances();
        // Wait for all the members to be discovered\
        // TODO 15 to config
        processingUnit.waitFor(i, 15, TimeUnit.SECONDS);
        return processingUnit.getInstances();
    }

    /**
     * Restart all discovered pus.
     */
    public static void restartAllPUs(PuManager puManager, Config config, RollbackChecker rollbackChecker) {
        PuRestarter puRestarter;
        List<ProcessingUnit> processingUnits = puManager.identProcessingUnits();
        for (ProcessingUnit processingUnit : processingUnits) {
            if (processingUnit.getType() == ProcessingUnitType.STATEFUL) {
                puRestarter = new StatefulPuRestarter(config, rollbackChecker);
            } else {
                puRestarter = new StatelessPuRestarter();
            }
            puRestarter.restart(processingUnit);
        }
    }

}
