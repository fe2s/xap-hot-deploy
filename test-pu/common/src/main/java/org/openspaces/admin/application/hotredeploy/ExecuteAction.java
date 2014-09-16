package org.openspaces.admin.application.hotredeploy;

import org.openspaces.remoting.ExecutorProxy;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @autor Anna_Babich
 */
public class ExecuteAction {

    @ExecutorProxy
    private TestAction testAction;

    public String execute(){
        return testAction.doAction();
    }
}
