// Copyright (c) 2013 Michael Snell - see https://github.com/snellm/cutlet

package com.snell.michael.cutlet;

public class CutletRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 2160305518990818995L;

    public CutletRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CutletRuntimeException(String message) {
        super(message);
    }
}