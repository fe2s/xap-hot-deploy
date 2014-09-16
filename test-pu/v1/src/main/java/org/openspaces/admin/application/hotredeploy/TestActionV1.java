package org.openspaces.admin.application.hotredeploy;

import org.openspaces.remoting.RemotingService;

/**
 * @author Anna_Babich
 */
@RemotingService
public class TestActionV1 implements TestAction{

    @Override
    public String doAction() {
        return "v1";
    }
}
