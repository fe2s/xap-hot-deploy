package org.openspaces.admin.application.hotredeploy.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anna_Babich
 */
public class Config {
    public static Logger log = LogManager.getLogger(Config.class);

    /**
     * Path to gigaspaces folder.
     */
    private String gigaspacesLocation;

    /**
     * Name and fileNames of restarting pus.
     */
    private Map<String, String> pusToRestart = new HashMap<String, String>();

    /**
     * Pu to restart names.
     */
    private List<String> puNames = new ArrayList<>();

    /**
     * Locator.
     */
    private String locator = null;

    /**
     * Lookup group.
     */
    private String lookupGroup = null;
    /**
     * Timeout for identify pu.
     */
    private Long identifyPuTimeout = null;

    /**
     * Timeout for identify instances.
     */
    private Long identifyInstancesTimeout = null;

    /**
     * Timeout for identify space mode.
     */
    private Long identifySpaceModeTimeout = null;

    /**
     * Timeout for restarting pu.
     */
    private Long restartTimeout = null;

    /**
     * Identify security.
     */
    private boolean isSecured = false;

    /**
     * Is double restart required.
     */
    private boolean doubleRestart = false;

    /**
     * Hosts on which GSM are located.
     */
    private List<String> gsmHosts = new ArrayList<String>();

    /**
     * Name of user on remote machine.
     */
    private String sshUser;

    /**
     * Is local cluster.
     */
    private boolean localCluster = false;

    /**
     * Password of remote machine.
     */
    private String sshPassword;


    public String getGigaspacesLocation() {
        return gigaspacesLocation;
    }

    public void setGigaspacesLocation(String gigaspacesLocation) {
        this.gigaspacesLocation = gigaspacesLocation;
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

    public Long getRestartTimeout() {
        return restartTimeout;
    }

    public void setRestartTimeout(Long restartTimeout) {
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

    public Map<String, String> getPusToRestart() {
        return pusToRestart;
    }

    public void setPusToRestart(Map<String, String> pusToRestart) {
        this.pusToRestart = pusToRestart;
    }

    public void setLocalCluster(boolean localCluster) {
        this.localCluster = localCluster;
    }

    public boolean isLocalCluster() {
        return localCluster;
    }

    public List<String> getPuNames() {
        return puNames;
    }

    public void setPuNames(List<String> puNames) {
        this.puNames = puNames;
    }
}
