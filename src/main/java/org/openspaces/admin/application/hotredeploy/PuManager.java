package org.openspaces.admin.application.hotredeploy;

import com.gigaspaces.cluster.activeelection.SpaceMode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitInstance;

import java.io.Console;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anna_Babich on 05.09.2014.
 */
public class PuManager {

    public static Logger log = LogManager.getLogger(PuManager.class);
    private ArgsStorage argsStorage;
    private Admin admin;

    public PuManager(ArgsStorage argsStorage){
        this.argsStorage = argsStorage;
    }

    /**
     * Create admin. Required before identify processing unit instances.
     * @return
     */
    public Admin createAdmin(){
        AdminFactory adminFactory = new AdminFactory();
        if (argsStorage.getLocator() != null) {
            adminFactory.addLocator(argsStorage.getLocator());
        }
        if (argsStorage.getLookupGroup() != null) {
            adminFactory.addGroup(argsStorage.getLookupGroup());
        }
        authorize(adminFactory);
        admin = adminFactory.createAdmin();
        return admin;
    }

    /**
     * Identify pu instances and wait for identify space mode of each instances.
     * @return discovered pu instances
     */
    public ProcessingUnitInstance[] getPuInstances(){
        ProcessingUnitInstance[] puInstances = identifyPuInstances();
        identSpaceMode(puInstances);
        return puInstances;
    }

    /**
     * Authorize user, if secure is turn on.
     *
     * @param adminFactory current admin factory.
     */
    private void authorize(AdminFactory adminFactory) {
        if (argsStorage.isSecured()) {
            Console console = System.console();
            String user = console.readLine("%s", "User: ");
            String password = String.valueOf(console.readPassword("%s", "Password: "));
            adminFactory.credentials(user, password);
        }
    }

    /**
     * Discover all processing unit instances.
     *
     * @return all discovering pu instances.
     */
    private ProcessingUnitInstance[] identifyPuInstances() {
        ProcessingUnit processingUnit = admin.getProcessingUnits().waitFor(
                argsStorage.getPuToRestart(), argsStorage.getIdentifyPuTimeout(), TimeUnit.SECONDS);
        if (processingUnit == null) {
            log.error("can't get PU instances for " + argsStorage.getPuToRestart());
            log.info(HotRedeployMain.FAILURE);
            admin.close();
            System.exit(1);
        }
        // Wait for all the members to be discovered
        processingUnit.waitFor(processingUnit.getPlannedNumberOfInstances());
        return processingUnit.getInstances();
    }

    /**
     * Discover space mode for all pu instances.
     *
     * @param puInstances discovered processing unit instances.
     */
    private void identSpaceMode(ProcessingUnitInstance[] puInstances) {
        Long timeout = System.currentTimeMillis() + argsStorage.getIdentifySpaceModeTimeout() * 1000;
        boolean keepTrying = true;

        while (keepTrying) {
            if (System.currentTimeMillis() >= timeout) {
                log.error("can't identify space mode");
                log.info(HotRedeployMain.FAILURE);
                System.exit(1);
            }
            keepTrying = false;
            for (ProcessingUnitInstance instance : puInstances) {
                if (instance.getSpaceInstance().getMode() == SpaceMode.NONE) {
                    keepTrying = true;
                }
            }
        }
    }
}
