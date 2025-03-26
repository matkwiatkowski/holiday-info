package com.mkwiatkowski.holiday_info.exceptions.holiday_api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when any exception or miss-configuration will be detected while calling Holiday API.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Error occurred while calling Holiday API")
public class HolidayApiException extends RuntimeException {

    public HolidayApiException(String message) {
        super(message);
    }
}
