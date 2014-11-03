package org.openspaces.admin.application.hotredeploy.preparation;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author Anna_Babich
 */
public class FileManager {

    public static final int SLEEP_TIME = 100;

    private JSch jSch;
    private Configuration config;
    private String rootPath;

    public FileManager(Configuration config) {
        jSch = new JSch();
        this.config = config;
        Console console = System.console();
        String password = String.valueOf(console.readPassword("%s", "Insert password of remote machine: "));
        config.setSshPassword(password);
        rootPath = new File("").getAbsoluteFile().getParentFile().getParent();
    }


    public void prepareFiles() throws JSchException, FileNotFoundException, SftpException {
        String user = config.getSshUser();
        String password = config.getSshPassword();
        List<String> gsmHosts = config.getGsmHosts();
        PasswordUserInfo userInfo = new PasswordUserInfo(password);
        Map<String, String> pus = config.getPus();
        String gsLocation = config.getGsLocation();
            for (String host : gsmHosts) {
                Session session = jSch.getSession(user, host, 22);
                session.setUserInfo(userInfo);
                session.connect();
                executeCommand(session, "mkdir /tmp/pu");
                for (String puName : pus.keySet()) {
                    executeCommand(session, "cp -r " + gsLocation + "/deploy/" + puName + "  /tmp/pu/");
                    executeCommand(session, "rm -rf " + gsLocation + "/deploy/" + puName);
                    executeCopy(session, pus.get(puName), gsLocation + "/deploy/");
                    executeCommand(session, "unzip " + gsLocation + "/deploy/" + pus.get(puName) + " -d " +  gsLocation+ "/deploy/" + puName);
                }
                session.disconnect();
            }


    }

    private void executeCommand(Session session, String command) throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");

        channel.setInputStream(null);
        channel.setErrStream(System.err);
        channel.setCommand(command);
        channel.connect();
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.disconnect();
    }

    private void executeCopy(Session session, String src, String dest) throws JSchException, SftpException, FileNotFoundException {
        ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
        channel.connect();
        File localFile = new File(rootPath + File.separator + src);
            channel.cd(dest);
            channel.put(new FileInputStream(localFile), localFile.getName());
        channel.disconnect();
    }
}

