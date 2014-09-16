package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.pu.ProcessingUnitInstance;

/**
 * @author Anna_Babich
 */
public class PuInstanceRestarter implements Runnable {
    public static Logger log = LogManager.getLogger(PuInstanceRestarter.class);

    private ProcessingUnitInstance instance;

    public PuInstanceRestarter(ProcessingUnitInstance instance){
        this.instance = instance;
    }

    @Override
    public void run() {
        restartPUInstance(instance);
    }

    /**
     * Restart certain processing unit instance.
     *
     * @param pi processing unit instance.
     */
    private void restartPUInstance(
            ProcessingUnitInstance pi) {
        final String instStr = pi.getSpaceInstance().getMode() != SpaceMode.PRIMARY ? "backup" : "primary";
        log.info("restarting instance " + pi.getInstanceId()
                + " on " + pi.getMachine().getHostName() + "["
                + pi.getMachine().getHostAddress() + "] GSC PID:"
                + pi.getVirtualMachine().getDetails().getPid() + " mode:"
                + instStr + "...");

        pi = pi.restartAndWait();
        log.info("done");
    }
}
