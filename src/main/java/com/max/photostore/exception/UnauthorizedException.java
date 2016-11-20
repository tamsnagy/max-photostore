package com.max.photostore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UnauthorizedException extends PhotostoreException {
    public ResponseEntity<?> buildResponse() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
