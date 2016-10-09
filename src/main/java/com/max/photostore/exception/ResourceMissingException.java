package com.max.photostore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResourceMissingException extends PhotostoreException {
    public ResourceMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceMissingException(String message) {
        super(message);
    }

    @Override
    public ResponseEntity<?> buildResponse() {
        return new ResponseEntity<>(getMessage(), HttpStatus.NOT_FOUND);
    }
}
