package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;
import org.openspaces.admin.gsa.GridServiceAgent;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.gsm.GridServiceManagers;
import org.openspaces.admin.pu.ProcessingUnit;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Anna_Babich
 */
public class PuManager {

    public static Logger log = LogManager.getLogger(PuManager.class);
    private Config config;
    private Admin admin;

    public PuManager(Config config) {
        this.config = config;
    }

    /**
     * Create admin. Required before identify processing unit instances.
     *
     * @return
     */
    public Admin createAdmin() {
        AdminFactory adminFactory = new AdminFactory();
        if (config.getLocator() != null) {
            adminFactory.addLocator(config.getLocator());
        }
        if (config.getLookupGroup() != null) {
            adminFactory.addGroup(config.getLookupGroup());
        }
        authorize(adminFactory);
        admin = adminFactory.createAdmin();
        return admin;
    }

    /**
     * Authorize user, if secure is turn on.
     *
     * @param adminFactory current admin factory.
     */
    private void authorize(AdminFactory adminFactory) {
        if (config.isSecured()) {
            Console console = System.console();
            String user = console.readLine("%s", "User: ");
            String password = String.valueOf(console.readPassword("%s", "Password: "));
            adminFactory.credentials(user, password);
        }
    }

    public List<ProcessingUnit> identProcessingUnits() {
        List<ProcessingUnit> processingUnits = new ArrayList<ProcessingUnit>();
        for (String name : config.getPuToRestart()) {
            ProcessingUnit processingUnit = admin.getProcessingUnits().waitFor(name, config.getIdentifyPuTimeout(), TimeUnit.SECONDS);
            if (processingUnit == null) {
                String cause = "can't get PU instances for " + name;
                log.error(cause);
                log.error(HotRedeployMain.FAILURE);
                throw new HotRedeployException(cause);
            }
            processingUnits.add(processingUnit);
        }
        return processingUnits;
    }

    public GridServiceManager[] getMangers(){
        return admin.getGridServiceManagers().getManagers();
    }


    public void closeAdmin(){
        admin.close();
    }
}
