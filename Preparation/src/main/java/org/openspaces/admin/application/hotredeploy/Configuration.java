package org.openspaces.admin.application.hotredeploy;

import java.util.List;
import java.util.Map;

/**
 * @author Anna_Babich
 */
public class Configuration {

    private String gsLocation;
    private String gsLocators;
    private String group;
    private String identPuTimeout;
    private String identSpaceModeTimeout;
    private String isSecured;
    private String doubleRestart;
    private Map<String, String> pus;

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

    @Override
    public String toString() {
        return "org.openspaces.admin.application.hotredeploy.Config{" +
                "gsLocation='" + gsLocation + '\'' +
                ", gsLocators='" + gsLocators + '\'' +
                ", group='" + group + '\'' +
                ", identPuTimeout='" + identPuTimeout + '\'' +
                ", identSpaceModeTimeout='" + identSpaceModeTimeout + '\'' +
                ", isSecured='" + isSecured + '\'' +
                ", doubleRestart='" + doubleRestart + '\'' +
                ", pus=" + pus +
                '}';
    }
}
