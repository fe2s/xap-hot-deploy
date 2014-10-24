package org.openspaces.admin.application.hotredeploy;

import org.openspaces.admin.pu.ProcessingUnit;

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
