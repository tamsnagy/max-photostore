package com.max.photostore.exception;

public class SignupException extends PhotostoreException {
    public SignupException(String message) {
        super(message);
    }

    public SignupException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignupException(Throwable cause) {
        super(cause);
    }
}
