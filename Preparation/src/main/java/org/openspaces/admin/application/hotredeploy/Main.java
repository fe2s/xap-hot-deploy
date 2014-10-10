package org.openspaces.admin.application.hotredeploy;

import java.io.*;

/**
 * @author Anna_Babich
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        PropertiesParser propertiesParser = new PropertiesParser();
        Configuration configuration = propertiesParser.parse();
        ArgsBuilder argsBuilder = new ArgsBuilder();
        argsBuilder.validateConfig(configuration);
        FileManager.copy(configuration);
        String[] arg = argsBuilder.build(configuration);
        HotRedeployMain.main(arg);
    }
}
