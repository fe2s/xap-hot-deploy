package org.openspaces.admin.application.hotredeploy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Anna_Babich on 05.09.2014.
 */
public class ArgsStorage {
    public static Logger log = LogManager.getLogger(ArgsStorage.class);

    @Parameter
    private List<String> parameters = new ArrayList<String>();

    @Parameter(names = { "-pun", "-pu_name" }, description = "Name of restarting processing unit")
    private String puToRestart;

    @Parameter(names = { "-gsl", "-gs_locator" }, description = "Locator")
    private String locator = null;

    @Parameter(names = { "-gsg", "-gs_group" }, description = "Lookup group")
    private String lookupGroup = null;

    @Parameter(names = { "-put", "-pu_timeout" }, description = "Timeout for identify pu")
    private Long identifyPuTimeout;

    @Parameter(names = { "-smt", "-space_made_timeout" }, description = "Timeout for identify space mode")
    private Long identifySpaceModeTimeout;

    @Parameter(names = { "-s", "-secured" }, description = "Identify security", arity = 1)
    private boolean isSecured = false;

    @Parameter(names = { "-dr", "-double_restart" }, description = "Is double restart required", arity = 1)
    private boolean doubleRestart = false;

    public String getPuToRestart() {
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
}
