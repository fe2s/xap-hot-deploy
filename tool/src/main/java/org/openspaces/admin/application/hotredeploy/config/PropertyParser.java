package org.openspaces.admin.application.hotredeploy.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Anna_Babich
 */
public class PropertyParser {

    public static Logger log = LogManager.getLogger(PropertyParser.class);

    private static Config configuration;

    public static Config parse() {
        String rootPath = new File("").getAbsoluteFile().getParentFile().getParent();
        log.info("ROOT PATH " + rootPath);
        String[] pathToResources = {rootPath, "config.properties"};
        String propPath = StringUtils.join(pathToResources, File.separator);
        log.info("PROP PATH " + propPath);
        File file = new File(propPath);
        configuration = new Config();
        try {
            InputStream input = new FileInputStream(file.getAbsolutePath());
            setProperties(input);
        } catch (IOException e) {
            throw new HotRedeployException(e);
        }
        logProperties(configuration);
        return configuration;
    }

    private static void setProperties(InputStream input) throws IOException {
        Properties prop = new Properties();
        prop.load(input);
        configuration.setGigaspacesLocation(prop.getProperty("GIGASPACES_LOCATION"));
        String locator = prop.getProperty("GIGASPACES_LOCATORS");
        if (!"".equals(locator)) {
            configuration.setLocator(prop.getProperty(locator));
        }
        String group = prop.getProperty("LOOKUP_GROUP");
        if (!"".equals(group)) {
            configuration.setLookupGroup(group);
        }

        try {
            configuration.setIdentifyPuTimeout(Long.parseLong(prop.getProperty("IDENT_PU_TIMEOUT")));
            configuration.setIdentifySpaceModeTimeout(Long.parseLong(prop.getProperty("IDENT_SPACE_MODE_TIMEOUT")));
            configuration.setIdentifyInstancesTimeout(Long.parseLong(prop.getProperty("IDENT_INSTANCES_TIMEOUT")));
            configuration.setRestartTimeout(Long.parseLong(prop.getProperty("RESTART_TIMEOUT")));
        } catch (NumberFormatException e) {
            throw new HotRedeployException("Please set correct timeouts and try again", e);
        }

        configuration.setSshUser(prop.getProperty("SSH_USER"));

        configuration.setLocalCluster(parseBoolean(prop.getProperty("LOCAL_CLUSTER_MODE")));
        configuration.setDoubleRestart(parseBoolean(prop.getProperty("DOUBLE_RESTART")));
        configuration.setSecured(parseBoolean(prop.getProperty("IS_SECURED")));

        String hostsString = prop.getProperty("GSM_HOSTS");
        List<String> hosts = Arrays.asList(hostsString.split("\\s*,\\s*"));
        configuration.setGsmHosts(hosts);

        String pusString = prop.getProperty("PU");
        List<String> puList = Arrays.asList(pusString.split("\\s*,\\s*"));
        List<String> puNames = new ArrayList<String>();
        Map<String, String> puMap = new HashMap<String, String>();
        for (String pu : puList) {
            int index = pu.indexOf('=');
            String key = pu.substring(0, index).trim();
            String value = pu.substring(index + 1).trim();
            puMap.put(key, value);
            puNames.add(key);
        }
        configuration.setPusToRestart(puMap);
        configuration.setPuNames(puNames);
        input.close();

    }


    private static void logProperties(Config config) {
        log.info("Gigaspaces location: " + config.getGigaspacesLocation());
        log.info("Pu to restart: " + config.getPusToRestart());
        log.info("Pu names: " + config.getPuNames());
        log.info("Locator: " + config.getLocator());
        log.info("Lookup group: " + config.getLookupGroup());
        log.info("Timeout for identify pu: " + config.getIdentifyPuTimeout());
        log.info("Timeout for identify instances: " + config.getIdentifyInstancesTimeout());
        log.info("Timeout for identify space mode: " + config.getIdentifySpaceModeTimeout());
        log.info("Timeout for restart " + config.getRestartTimeout());
        log.info("Secured: " + config.isSecured());
        log.info("Double restart: " + config.isDoubleRestart());
        log.info("GSM Hosts: " + config.getGsmHosts());
        log.info("User: " + config.getSshUser());
        log.info("Is local cluster: " + config.isLocalCluster());
    }

    public static boolean parseBoolean(String value){
        if (value.equalsIgnoreCase("true")){
            return true;
        } else if (value.equalsIgnoreCase("false")){
            return false;
        } else {
            throw new HotRedeployException("Please set correct boolean values");
        }
    }


}
