package org.openspaces.admin.application.hotredeploy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitType;

import java.io.Console;
import java.util.concurrent.TimeUnit;

/**
 * @author Anna_Babich
 */
public class PuManager {

    public static Logger log = LogManager.getLogger(PuManager.class);
    private Config config;
    private Admin admin;

    public PuManager(Config config){
        this.config = config;
    }

    /**
     * Create admin. Required before identify processing unit instances.
     * @return
     */
    public Admin createAdmin(){
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

    /**
     * Restart all discovered pus.
     */
    public void restartAllPUs() {
        createAdmin();
        PuRestarter puRestarter = null;
        for(String name: config.getPuToRestart()) {
            ProcessingUnit processingUnit = admin.getProcessingUnits().waitFor(name, config.getIdentifyPuTimeout(), TimeUnit.SECONDS);
            if (processingUnit.getType() == ProcessingUnitType.STATEFUL){
                puRestarter = new StatefulPuRestarter(config);
            } else {
                puRestarter = new StatelessPuRestarter();
            }
            puRestarter.restart(processingUnit);
        }
        admin.close();
    }
}
