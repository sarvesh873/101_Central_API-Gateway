package com.central.api_gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when an invalid JWT token is encountered.
 */
public class InvalidJWTTokenException extends ResponseStatusException {
    public InvalidJWTTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
