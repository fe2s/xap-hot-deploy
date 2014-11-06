package org.openspaces.admin.application.hotredeploy.files;

import com.jcraft.jsch.*;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * @author Anna_Babich
 */
public class SSHFileManager implements FileManager {

    public static final int SLEEP_TIME = 100;

    private JSch jSch;
    private Config config;
    private String rootPath;
    private Map<String, String> pus;
    private String gsLocation;

    public SSHFileManager(Config config) {
        jSch = new JSch();
        this.config = config;
        Console console = System.console();
        String password = String.valueOf(console.readPassword("%s", "Insert password of remote machine: "));
        config.setSshPassword(password);
        rootPath = new File("").getAbsoluteFile().getParentFile().getParent();
        pus = config.getPusToRestart();
        gsLocation = config.getGigaspacesLocation();

    }

    public void restoreTempFolders() {
        List<String> gsmHosts = config.getGsmHosts();
        String user = config.getSshUser();
        String password = config.getSshPassword();

        PasswordUserInfo userInfo = new PasswordUserInfo(password);
        try {
            for (String host : gsmHosts) {
                Session session = jSch.getSession(user, host, 22);
                session.setUserInfo(userInfo);
                session.connect();
                for (String puName : pus.keySet()) {
                    executeCommand(session, "rm -rf " + gsLocation + "/deploy/" + puName);
                    executeCommand(session, "cp -r /tmp/pu/" + puName + " " + gsLocation + "/deploy");
                }
                session.disconnect();
            }
        } catch (JSchException e) {
            throw new HotRedeployException(e);
        }
    }

    public void removeTempFolder() {
        PasswordUserInfo userInfo = new PasswordUserInfo(config.getSshPassword());
        List<String> gsmHosts = config.getGsmHosts();
        String user = config.getSshUser();
        try {
            for (String host : gsmHosts) {
                Session session = jSch.getSession(user, host, 22);
                session.setUserInfo(userInfo);
                session.connect();
                executeCommand(session, "rm -rf /tmp/pu/");
                session.disconnect();
            }
        } catch (JSchException e) {
            throw new HotRedeployException(e);
        }
    }

    public void prepareFiles() {
        String user = config.getSshUser();
        String password = config.getSshPassword();
        List<String> gsmHosts = config.getGsmHosts();
        PasswordUserInfo userInfo = new PasswordUserInfo(password);
        try {
            for (String host : gsmHosts) {
                Session session = jSch.getSession(user, host, 22);
                session.setUserInfo(userInfo);
                session.connect();
                executeCommand(session, "mkdir /tmp/pu");
                for (String puName : pus.keySet()) {
                    executeCommand(session, "cp -r " + gsLocation + "/deploy/" + puName + "  /tmp/pu/");
                    executeCommand(session, "rm -rf " + gsLocation + "/deploy/" + puName);
                    executeCopy(session, pus.get(puName), gsLocation + "/deploy/");
                    executeCommand(session, "unzip " + gsLocation + "/deploy/" + pus.get(puName) + " -d " + gsLocation + "/deploy/" + puName);
                }
                session.disconnect();
            }
        }catch (JSchException | FileNotFoundException | SftpException e) {
            throw new HotRedeployException(e);
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
