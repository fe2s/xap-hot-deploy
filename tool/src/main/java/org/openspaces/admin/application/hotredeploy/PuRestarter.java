package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

/**
 * @author Anna_Babich
 */
public abstract class PuRestarter {

    public static Logger log = LogManager.getLogger(PuRestarter.class);

    /**
     *
     * @param processingUnit current pu.
     * @return pu instances
     */
    protected ProcessingUnitInstance[] identifyPuInstances(ProcessingUnit processingUnit) {
        if (processingUnit == null) {
            String cause = "can't get PU instances";
            log.error(cause);
            log.error(HotRedeployMain.FAILURE);
            throw new HotRedeployException(cause);
        }
        // Wait for all the members to be discovered
        processingUnit.waitFor(processingUnit.getPlannedNumberOfInstances());
        return processingUnit.getInstances();
    }

    /**
     * Restart PU.
     * @param processingUnit current pu
     */
    public abstract void restart(ProcessingUnit processingUnit);
}
