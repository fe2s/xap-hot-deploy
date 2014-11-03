package org.openspaces.admin.application.hotredeploy.files;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.openspaces.admin.application.hotredeploy.config.Config;
import org.openspaces.admin.application.hotredeploy.exceptions.HotRedeployException;

import java.util.List;

/**
 * @author Mykola_Zalyayev
 */
public class SSHFileManager implements FileManager {

    private JSch jSch;
    private Config config;

    public SSHFileManager(Config config) {
        this.config = config;
        this.jSch = new JSch();
    }

    public void restoreTempFolders() {
        List<String> gsmHosts = config.getGsmHosts();
        List<String> puNames = config.getPuToRestart();
        String gigaspacesLocation = config.getGigaspacesLocation();
        String user = config.getSshUser();
        String password = config.getSshPassword();

        PasswordUserInfo userInfo = new PasswordUserInfo(password);
        try {
            for (String host : gsmHosts) {
                Session session = jSch.getSession(user, host, 22);
                session.setUserInfo(userInfo);
                session.connect();
                for (String puName : puNames) {
                    executeCommand(session, "rm -rf " + gigaspacesLocation + "/deploy/" + puName);
                    executeCommand(session, "cp -r /tmp/pu/" + puName + " " + gigaspacesLocation + "/deploy");
                }
                session.disconnect();
            }
        } catch (JSchException e) {
            throw new HotRedeployException(e);
        }
    }

    public void removeFolder() {
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

    private void executeCommand(Session session, String command) throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);
        channel.connect();
        channel.disconnect();

    }
}
