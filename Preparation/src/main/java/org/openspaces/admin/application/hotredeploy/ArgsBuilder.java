package org.openspaces.admin.application.hotredeploy;

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
            throw new HotRedeployException("IDENTIFY_PU_TIMEOUT must be set");
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
        args.add("-put");
        args.add(configuration.getIdentPuTimeout());
        args.add("-smt");
        args.add(configuration.getIdentSpaceModeTimeout());
        for (String puName: configuration.getPus().keySet()) {
            args.add("-pun");
            args.add(puName);
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
        String[] s = new String[args.size()];
        return args.toArray(s);
    }
}
