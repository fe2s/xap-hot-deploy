package openspaces.admin.application.hotredeploy;

import org.openspaces.admin.application.hotredeploy.HotRedeployMain;

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

        //TODO change file manager logic to work with jSch (like hot-redeploy.sh script)
        //TODO add scripts to run preparation module
        //TODO change comments in properties.properties
        //FileManager.copy(configuration);

        String[] arg = argsBuilder.build(configuration);
        HotRedeployMain.main(arg);
    }
}
