package org.openspaces.admin.application.hotredeploy.preparation;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * @author Anna_Babich
 */
public class PropertiesParser {
    private Configuration configuration;

    public Configuration parse() throws IOException {
        String rootPath = new File("").getAbsoluteFile().getParentFile().getParent();
        System.out.println("PARSER ROOT PATH " + rootPath);
        String[] pathToResources = {rootPath, "config.properties"};
        String propPath = StringUtils.join(pathToResources, File.separator);
        System.out.println("PROP PATH " + propPath);

        File file = new File(propPath);
        configuration = new Configuration();
        InputStream input = new FileInputStream(file.getAbsolutePath());
        setProperties(input);
        System.out.println("CONFIGURATION " + configuration);
        return configuration;
    }

    public void setProperties(InputStream input) throws IOException {
        Properties prop = new Properties();
        prop.load(input);
        configuration.setGsLocation(prop.getProperty("GIGASPACES_LOCATION"));
        configuration.setGsLocators(prop.getProperty("GIGASPACES_LOCATORS"));
        configuration.setGroup(prop.getProperty("LOOKUP_GROUP"));
        configuration.setIdentPuTimeout(prop.getProperty("IDENT_PU_TIMEOUT"));
        configuration.setIdentSpaceModeTimeout(prop.getProperty("IDENT_SPACE_MODE_TIMEOUT"));
        configuration.setIdentInstancesTimeout(prop.getProperty("IDENT_INSTANCES_TIMEOUT"));
        configuration.setRestartTimeout(prop.getProperty("RESTART_TIMEOUT"));
        configuration.setSshUser(prop.getProperty("SSH_USER"));
        configuration.setLocalCluster(prop.getProperty("LOCAL_CLUSTER_MODE"));
        configuration.setDoubleRestart(prop.getProperty("DOUBLE_RESTART"));
        configuration.setIsSecured(prop.getProperty("IS_SECURED"));

        String hostsString = prop.getProperty("GSM_HOSTS");
        List<String> hosts = Arrays.asList(hostsString.split("\\s*,\\s*"));
        configuration.setGsmHosts(hosts);

        String pusString = prop.getProperty("PU");
        List<String> puList = Arrays.asList(pusString.split("\\s*,\\s*"));
        Map<String, String> puMap = new HashMap<String, String>();
        for(String pu :puList){
            int index = pu.indexOf('=');
            String key = pu.substring(0,index).trim();
            String value = pu.substring(index+1).trim();
            System.out.println(key + " " + value);
            puMap.put(key,value);
        }
        configuration.setPus(puMap);
        input.close();

    }
}
