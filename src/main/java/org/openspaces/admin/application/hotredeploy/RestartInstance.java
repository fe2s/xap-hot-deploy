package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import java.util.concurrent.TimeUnit;

/**
 * Restart pu instance in new thread.
 */
public class RestartInstance implements Runnable {
    public static Logger log = LogManager.getLogger(RestartInstance.class);

    private ProcessingUnitInstance instance;
    private long timeout;

    public RestartInstance(ProcessingUnitInstance instance, long timeout){
        this.instance = instance;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        restartPUInstance(instance, timeout);
    }

    /**
     * Restart certain processing unit instance.
     *
     * @param pi                     processing unit instance.
     * @param restartAndWaitTimeout  time out for restarting instance.
     */
    private void restartPUInstance(
            ProcessingUnitInstance pi, long restartAndWaitTimeout) {
        final String instStr = pi.getSpaceInstance().getMode() != SpaceMode.PRIMARY ? "backup" : "primary";
        log.info("restarting instance " + pi.getInstanceId()
                + " on " + pi.getMachine().getHostName() + "["
                + pi.getMachine().getHostAddress() + "] GSC PID:"
                + pi.getVirtualMachine().getDetails().getPid() + " mode:"
                + instStr + "...");

        pi = pi.restartAndWait(restartAndWaitTimeout, TimeUnit.SECONDS);
        log.info("done");
    }
}
