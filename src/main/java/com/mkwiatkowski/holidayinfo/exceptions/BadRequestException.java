package com.mkwiatkowski.holidayinfo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Thrown when request data didn't pass validation or other exception occurred during processing.
 */
public class BadRequestException extends ResponseStatusException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
