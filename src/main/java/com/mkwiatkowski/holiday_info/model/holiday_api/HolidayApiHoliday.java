package com.mkwiatkowski.holiday_info.model.holiday_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Represents a single holiday object returned by Holiday API. We are only extracting fields that are meaningful for
 * the application.
 * <p>
 * Example of full object:
 * {@code
 * {
 *     "name": "All Saints' Day",
 *     "date": "2024-11-01",
 *     "observed": "2024-11-01",
 *     "public": true,
 *     "country": "PL",
 *     "uuid": "12e216ee-93b2-4a8c-a957-b7c39686c3bd",
 *     "weekday": {
 *         "date": {
 *             "name": "Friday",
 *             "numeric": "5"
 *         },
 *         "observed": {
 *             "name": "Friday",
 *             "numeric": "5"
 *         }
 *     }
 * }
 * }
 *
 * @param name     name of the holiday
 * @param date     date on which holiday occurs
 * @param country  country where holiday occurs
 * @param isPublic is it a public holiday or not
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record HolidayApiHoliday(@JsonProperty String name, @JsonProperty LocalDate date, @JsonProperty String country,
                                @JsonProperty("public") Boolean isPublic) {
}
