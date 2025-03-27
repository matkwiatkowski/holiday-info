package com.mkwiatkowski.holiday_info.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when request data didn't pass validation or other exception occurred during processing.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid request data")
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
