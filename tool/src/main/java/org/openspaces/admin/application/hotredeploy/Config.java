package org.openspaces.admin.application.hotredeploy;

import com.beust.jcommander.Parameter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anna_Babich
 */
public class Config {
    public static Logger log = LogManager.getLogger(Config.class);


    @Parameter(names = {"-gsloc", "-gs_location"}, description = "Path to gigaspaces folder")
    private String gigaspacesLocation;

    @Parameter
    private List<String> parameters = new ArrayList<String>();

    @Parameter(names = { "-pun", "-pu_name" }, description = "Name of restarting processing unit")
    private List<String> puToRestart = new ArrayList<String>();

    @Parameter(names = { "-gsl", "-gs_locator" }, description = "Locator")
    private String locator = null;

    @Parameter(names = { "-gsg", "-gs_group" }, description = "Lookup group")
    private String lookupGroup = null;

    @Parameter(names = { "-put", "-pu_timeout" }, description = "Timeout for identify pu")
    private Long identifyPuTimeout;

    @Parameter(names = { "-inst", "-instances_timeout" }, description = "Timeout for identify instances")
    private Long identifyInstancesTimeout;

    @Parameter(names = { "-smt", "-space_made_timeout" }, description = "Timeout for identify space mode")
    private Long identifySpaceModeTimeout;

    @Parameter(names = {"-rt", "-restart_timeout"}, description = "Timeout for restarting pu")
    private long restartTimeout;

    @Parameter(names = { "-s", "-secured" }, description = "Identify security", arity = 1)
    private boolean isSecured = false;

    @Parameter(names = { "-dr", "-double_restart" }, description = "Is double restart required", arity = 1)
    private boolean doubleRestart = false;

    public List<String> getPuToRestart() {
        return puToRestart;
    }

    public String getLocator() {
        return locator;
    }

    public String getLookupGroup() {
        return lookupGroup;
    }

    public Long getIdentifyPuTimeout() {
        return identifyPuTimeout;
    }

    public Long getIdentifySpaceModeTimeout() {
        return identifySpaceModeTimeout;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public boolean isDoubleRestart() {
        return doubleRestart;
    }

    public long getRestartTimeout() {
        return restartTimeout;
    }

    public String getGigaspacesLocation() {
        return gigaspacesLocation;
    }

}
