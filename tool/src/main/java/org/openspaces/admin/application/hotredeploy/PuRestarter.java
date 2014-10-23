package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

/**
 * @author Anna_Babich
 */
public interface PuRestarter {

    /**
     * Restart PU.
     * @param processingUnit current pu
     */
    boolean restart(ProcessingUnit processingUnit);
}
