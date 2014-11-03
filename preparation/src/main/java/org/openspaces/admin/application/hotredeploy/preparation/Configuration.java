package org.openspaces.admin.application.hotredeploy.preparation;

import java.util.List;
import java.util.Map;

/**
 * @author Anna_Babich
 */
public class Configuration {

    private List<String> gsmHosts;
    private String gsLocation;
    private String gsLocators;
    private String group;
    private String identPuTimeout;
    private String identSpaceModeTimeout;
    private String identInstancesTimeout;
    private String restartTimeout;
    private String sshUser;
    private String localCluster;
    private String isSecured;
    private String doubleRestart;
    private Map<String, String> pus;
    private String sshPassword;

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public String getGsLocation() {
        return gsLocation;
    }

    public void setGsLocation(String gsLocation) {
        this.gsLocation = gsLocation;
    }

    public String getGsLocators() {
        return gsLocators;
    }

    public void setGsLocators(String gsLocators) {
        this.gsLocators = gsLocators;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getIdentPuTimeout() {
        return identPuTimeout;
    }

    public void setIdentPuTimeout(String identPuTimeout) {
        this.identPuTimeout = identPuTimeout;
    }

    public String getIdentSpaceModeTimeout() {
        return identSpaceModeTimeout;
    }

    public void setIdentSpaceModeTimeout(String identSpaceModeTimeout) {
        this.identSpaceModeTimeout = identSpaceModeTimeout;
    }

    public String getIsSecured() {
        return isSecured;
    }

    public void setIsSecured(String isSecured) {
        this.isSecured = isSecured;
    }

    public String getDoubleRestart() {
        return doubleRestart;
    }

    public void setDoubleRestart(String doubleRestart) {
        this.doubleRestart = doubleRestart;
    }

    public Map<String, String> getPus() {
        return pus;
    }

    public void setPus(Map<String, String> pus) {
        this.pus = pus;
    }

    public List<String> getGsmHosts() {
        return gsmHosts;
    }

    public void setGsmHosts(List<String> gsmHosts) {
        this.gsmHosts = gsmHosts;
    }

    public String getIdentInstancesTimeout() {
        return identInstancesTimeout;
    }

    public void setIdentInstancesTimeout(String identInstancesTimeout) {
        this.identInstancesTimeout = identInstancesTimeout;
    }

    public String getRestartTimeout() {
        return restartTimeout;
    }

    public void setRestartTimeout(String restartTimeout) {
        this.restartTimeout = restartTimeout;
    }

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public String getLocalCluster() {
        return localCluster;
    }

    public void setLocalCluster(String localCluster) {
        this.localCluster = localCluster;
    }


    @Override
    public String toString() {
        return "Configuration{" +
                "gsmHosts=" + gsmHosts +
                ", gsLocation='" + gsLocation + '\'' +
                ", gsLocators='" + gsLocators + '\'' +
                ", group='" + group + '\'' +
                ", identPuTimeout='" + identPuTimeout + '\'' +
                ", identSpaceModeTimeout='" + identSpaceModeTimeout + '\'' +
                ", identInstancesTimeout='" + identInstancesTimeout + '\'' +
                ", restartTimeout='" + restartTimeout + '\'' +
                ", sshUser='" + sshUser + '\'' +
                ", localCluster='" + localCluster + '\'' +
                ", isSecured='" + isSecured + '\'' +
                ", doubleRestart='" + doubleRestart + '\'' +
                ", pus=" + pus +
                '}';
    }
}
