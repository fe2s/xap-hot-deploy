package org.openspaces.admin.application.hotredeploy.exceptions;

/**
 * @author Anna_Babich
 */
public class HotRedeployException extends RuntimeException{

    public HotRedeployException() {
        super();
    }

    public HotRedeployException(String message) {
        super(message);
    }

    public HotRedeployException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotRedeployException(Throwable cause) {
        super(cause);
    }

    protected HotRedeployException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
