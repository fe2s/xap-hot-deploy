package org.openspaces.admin.application.hotredeploy.files;

import com.jcraft.jsch.UserInfo;

/**
 * @author Mykola_Zalyayev
 */
public class PasswordUserInfo implements UserInfo {
    private String password;

    public PasswordUserInfo(String password) {
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassword(String message) {
        return true;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return true;
    }

    @Override
    public boolean promptYesNo(String message) {
        return true;
    }

    @Override
    public void showMessage(String message) {

    }
}
