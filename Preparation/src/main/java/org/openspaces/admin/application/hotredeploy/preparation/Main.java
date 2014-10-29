package org.openspaces.admin.application.hotredeploy.preparation;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
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
        FileManager fileManager = new FileManager(configuration);
        try {
            fileManager.prepareFiles();
        } catch (JSchException|SftpException e) {
            e.printStackTrace();
        }
        String[] arg = argsBuilder.build(configuration);
        HotRedeployMain.main(arg);
    }
}
