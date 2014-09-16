package org.openspaces.admin.application.hotredeploy;

/**
 * @autor Anna_Babich
 */
public class HotRedeployException extends RuntimeException{

    public HotRedeployException(String message){
        super(message);
    }

    public HotRedeployException(){
        super();
    }


}
