package com.max.photostore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AccessDeniedException extends PhotostoreException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessDeniedException() {
    }

    @Override
    public ResponseEntity<?> buildResponse() {
        return new ResponseEntity<>(getMessage(), HttpStatus.FORBIDDEN);
    }
}
