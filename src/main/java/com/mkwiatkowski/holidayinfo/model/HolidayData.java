package com.mkwiatkowski.holidayinfo.model;

import com.mkwiatkowski.holidayinfo.service.ExternalHolidayService;
import java.time.LocalDate;

/**
 * Shared interface for doing operations on holiday data retrieved from external source. Must be implemented by classes that are returned by
 * services implementing {@link ExternalHolidayService}.
 */
public interface HolidayData {

    String getName();

    LocalDate getDate();

    String getCountryCode();
}
