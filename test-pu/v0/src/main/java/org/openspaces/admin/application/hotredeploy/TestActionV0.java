package org.openspaces.admin.application.hotredeploy;

import org.openspaces.remoting.RemotingService;

/**
 * @autor Anna_Babich
 */
@RemotingService
public class TestActionV0 implements TestAction{

    @Override
    public String doAction() {
        return "v0";
    }
}
