package com.mkwiatkowski.holidayinfo.exceptions.holidayapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Thrown when any exception or miss-configuration will be detected while calling Holiday API.
 */
public class HolidayApiException extends ResponseStatusException {

    public HolidayApiException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
