package com.max.photostore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PhotostoreException extends Exception{
    public PhotostoreException() {
    }

    public PhotostoreException(String message) {
        super(message);
    }

    public PhotostoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhotostoreException(Throwable cause) {
        super(cause);
    }

    public PhotostoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ResponseEntity<?> buildResponse() {
        return new ResponseEntity<>(getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
