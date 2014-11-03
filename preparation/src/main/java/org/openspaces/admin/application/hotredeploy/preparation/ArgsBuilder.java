package org.openspaces.admin.application.hotredeploy.preparation;

import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anna_Babich
 */
public class ArgsBuilder {
    public void validateConfig(Configuration configuration){
        if (configuration.getGsLocation().equals("")) {
            throw new HotRedeployException("Gigaspaces location must be set");
        }
        if (configuration.getIdentPuTimeout().equals("")) {
            throw new HotRedeployException("IDENTI_PU_TIMEOUT must be set");
        }
        if (configuration.getIdentInstancesTimeout().equals("")) {
            throw new HotRedeployException("IDENT_INSTANCES_TIMEOUT must be set");
        }
        if (configuration.getRestartTimeout().equals("")) {
            throw new HotRedeployException("RESTART_TIMEOUT must be set");
        }
        if (configuration.getIdentSpaceModeTimeout().equals("")) {
            throw new HotRedeployException("IDENTIFY_SPACE_MODE_TIMEOUT must be set");
        }
        if (configuration.getPus().size() == 0) {
            throw new HotRedeployException("PU name and file must be set");
        }
    }

    public String[] build(Configuration configuration){
        List<String> args = new ArrayList<String>();
        args.add("-u");
        args.add(configuration.getSshUser());
        args.add("-p");
        args.add(configuration.getSshPassword());
        args.add("-put");
        args.add(configuration.getIdentPuTimeout());
        args.add("-smt");
        args.add(configuration.getIdentSpaceModeTimeout());
        args.add("-inst");
        args.add(configuration.getIdentInstancesTimeout());
        args.add("-rt");
        args.add(configuration.getRestartTimeout());
        args.add("-gsloc");
        args.add(configuration.getGsLocation());
        for (String puName: configuration.getPus().keySet()) {
            args.add("-pun");
            args.add(puName);
        }
        for (String host: configuration.getGsmHosts()) {
            args.add("-gsmh");
            args.add(host);
        }
        if (!configuration.getGroup().equals("")) {
            args.add("-gsg");
            args.add(configuration.getGroup());
        }
        if (!configuration.getGsLocators().equals("")){
            args.add("-gsl");
            args.add(configuration.getGsLocators());
        }
        if (!configuration.getIsSecured().equals("")){
            args.add("-s");
            args.add(configuration.getIsSecured());
        }
        if (!configuration.getDoubleRestart().equals("")){
            args.add("-dr");
            args.add(configuration.getDoubleRestart());
        }
        if (!configuration.getLocalCluster().equals("")){
            args.add("-lcm");
            args.add(configuration.getLocalCluster());
        }
        String[] s = new String[args.size()];
        return args.toArray(s);
    }
}
