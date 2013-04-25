package org.snellm.cutlet;

public class XPathObjectRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 2160305518990818995L;

    public XPathObjectRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public XPathObjectRuntimeException(String message) {
        super(message);
    }
}