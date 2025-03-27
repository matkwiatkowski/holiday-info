package com.mkwiatkowski.holidayinfo.service;

import com.mkwiatkowski.holidayinfo.model.HolidayData;
import java.util.List;

/**
 * Interface to be implemented by external services providing holiday data. {@link HolidayData} will be returned by methods in this service.
 */
public interface ExternalHolidayService<T extends HolidayData> {

    List<T> getForYear(String countryCode, Integer year);
}
