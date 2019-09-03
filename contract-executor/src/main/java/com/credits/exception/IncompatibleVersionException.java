package com.credits.exception;

import exception.ContractExecutorException;

public class IncompatibleVersionException extends ContractExecutorException {
    public IncompatibleVersionException(String message, Throwable e) {
        super(message, e);
    }

    public IncompatibleVersionException(String message) {
        super(message);
    }
}
