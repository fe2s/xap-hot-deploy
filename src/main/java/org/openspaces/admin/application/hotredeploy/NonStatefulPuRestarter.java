package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

/**
 * @autor Anna_Babich
 */
public class NonStatefulPuRestarter extends PuRestarter {

    public static Logger log = LogManager.getLogger(NonStatefulPuRestarter.class);

    public void restart(ProcessingUnit processingUnit){
        ProcessingUnitInstance[] puInstances = identifyPuInstances(processingUnit);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        for (ProcessingUnitInstance puInstance : puInstances) {
            log.info("Restarting pu " + processingUnit.getName() + " with type " + processingUnit.getType());
            puInstance = puInstance.restartAndWait();
            log.info("done");
        }
    }
}
