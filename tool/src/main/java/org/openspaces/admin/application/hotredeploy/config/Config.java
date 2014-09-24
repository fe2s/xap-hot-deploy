package org.openspaces.admin.application.hotredeploy.config;

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

    @Parameter(names = {"-pun", "-pu_name"}, description = "Name of restarting processing unit")
    private List<String> puToRestart = new ArrayList<String>();

    @Parameter(names = {"-gsl", "-gs_locator"}, description = "Locator")
    private String locator = null;

    @Parameter(names = {"-gsg", "-gs_group"}, description = "Lookup group")
    private String lookupGroup = null;

    @Parameter(names = {"-put", "-pu_timeout"}, description = "Timeout for identify pu")
    private Long identifyPuTimeout;

    @Parameter(names = {"-inst", "-instances_timeout"}, description = "Timeout for identify instances")
    private Long identifyInstancesTimeout;

    @Parameter(names = {"-smt", "-space_made_timeout"}, description = "Timeout for identify space mode")
    private Long identifySpaceModeTimeout;

    @Parameter(names = {"-rt", "-restart_timeout"}, description = "Timeout for restarting pu")
    private long restartTimeout;

    @Parameter(names = {"-s", "-secured"}, description = "Identify security", arity = 1)
    private boolean isSecured = false;

    @Parameter(names = {"-dr", "-double_restart"}, description = "Is double restart required", arity = 1)
    private boolean doubleRestart = false;
    @Parameter(names = {"-gsmh", "-gsm_hosts"}, description = "Hosts on which GSM are located")
    private List<String> gsmHosts;

    @Parameter(names = {"-u", "-user"}, description = "Name of user on remote machine")
    private String sshUser;

    @Parameter(names = {"-lcm","-local_cluster_mode"}, description = "Is local cluster")
    private Boolean localCluster = false;

    private String sshPassword;


    public String getGigaspacesLocation() {
        return gigaspacesLocation;
    }

    public void setGigaspacesLocation(String gigaspacesLocation) {
        this.gigaspacesLocation = gigaspacesLocation;
    }

    public List<String> getPuToRestart() {
        return puToRestart;
    }

    public void setPuToRestart(List<String> puToRestart) {
        this.puToRestart = puToRestart;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public Long getIdentifyPuTimeout() {
        return identifyPuTimeout;
    }

    public void setIdentifyPuTimeout(Long identifyPuTimeout) {
        this.identifyPuTimeout = identifyPuTimeout;
    }

    public String getLookupGroup() {
        return lookupGroup;
    }

    public void setLookupGroup(String lookupGroup) {
        this.lookupGroup = lookupGroup;
    }

    public Long getIdentifyInstancesTimeout() {
        return identifyInstancesTimeout;
    }

    public void setIdentifyInstancesTimeout(Long identifyInstancesTimeout) {
        this.identifyInstancesTimeout = identifyInstancesTimeout;
    }

    public Long getIdentifySpaceModeTimeout() {
        return identifySpaceModeTimeout;
    }

    public void setIdentifySpaceModeTimeout(Long identifySpaceModeTimeout) {
        this.identifySpaceModeTimeout = identifySpaceModeTimeout;
    }

    public long getRestartTimeout() {
        return restartTimeout;
    }

    public void setRestartTimeout(long restartTimeout) {
        this.restartTimeout = restartTimeout;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public void setSecured(boolean isSecured) {
        this.isSecured = isSecured;
    }

    public boolean isDoubleRestart() {
        return doubleRestart;
    }

    public void setDoubleRestart(boolean doubleRestart) {
        this.doubleRestart = doubleRestart;
    }

    public List<String> getGsmHosts() {
        return gsmHosts;
    }

    public void setGsmHosts(List<String> gsmHosts) {
        this.gsmHosts = gsmHosts;
    }

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public Boolean isLocalCluster() {
        return localCluster;
    }

    public void setLocalCluster(Boolean localCluster) {
        this.localCluster = localCluster;
    }
}
