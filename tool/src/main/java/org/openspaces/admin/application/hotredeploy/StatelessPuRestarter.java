package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.utils.PuUtils;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

/**
 * @author Anna_Babich
 */
public class StatelessPuRestarter implements PuRestarter {

    public static Logger log = LogManager.getLogger(StatelessPuRestarter.class);

    public void restart(ProcessingUnit processingUnit){
        ProcessingUnitInstance[] puInstances = PuUtils.getPuInstances(processingUnit);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        for (ProcessingUnitInstance puInstance : puInstances) {
            log.info("Restarting pu " + processingUnit.getName() + " with type " + processingUnit.getType());
            puInstance.restartAndWait();
            log.info("done");
        }
    }
}
