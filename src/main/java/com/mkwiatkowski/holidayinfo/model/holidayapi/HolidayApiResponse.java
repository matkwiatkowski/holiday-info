package com.mkwiatkowski.holidayinfo.model.holidayapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Holds list of holidays returned by Holiday API. In case of error it can also hold error message. We are only
 * extracting fields that are meaningful for the application.
 *
 * <p>Example of full successful response:
 * {@code
 * {
 *     "status": 200,
 *     "warning": "These results do not include state and province holidays. For more information, please visit https://holidayapi.com/docs",
 *     "requests": {
 *         "used": 19,
 *         "available": 9981,
 *         "resets": "2025-04-01 00:00:00"
 *     },
 *     "holidays": [
 *         {
 *             "name": "All Saints' Day",
 *             "date": "2024-11-01",
 *             "observed": "2024-11-01",
 *             "public": true,
 *             "country": "PL",
 *             "uuid": "12e216ee-93b2-4a8c-a957-b7c39686c3bd",
 *             "weekday": {
 *                 "date": {
 *                     "name": "Friday",
 *                     "numeric": "5"
 *                 },
 *                 "observed": {
 *                     "name": "Friday",
 *                     "numeric": "5"
 *                 }
 *             }
 *         }
 *     ]
 * }
 * }
 *
 * <p/>Example of full error response:
 * {@code
 * {
 *     "status": 400,
 *     "requests": {
 *         "used": 20,
 *         "available": 9980,
 *         "resets": "2025-04-01 00:00:00"
 *     },
 *     "error": "The requested date (2024-asdf-11) is invalid. For more information, please visit https://holidayapi.com/docs"
 * }
 * }
 *
 * @param holidays list of all holidays that are matching the query params
 * @param error    error message in case request fails
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record HolidayApiResponse(@JsonProperty List<HolidayApiHoliday> holidays, @JsonProperty String error) {

    public HolidayApiResponse(List<HolidayApiHoliday> holidays) {
        this(holidays, null);
    }
}
