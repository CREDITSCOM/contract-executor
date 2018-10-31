package com.credits.exception;

public class CompilationException extends Exception {
    public CompilationException(String message, Throwable e) {
        super(message, e);
    }

    public CompilationException(String message) {
        super(message);
    }
}
