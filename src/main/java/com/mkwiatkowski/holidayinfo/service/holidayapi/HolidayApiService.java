package com.mkwiatkowski.holidayinfo.service.holidayapi;

import com.mkwiatkowski.holidayinfo.config.holidayapi.HolidayApiProperties;
import com.mkwiatkowski.holidayinfo.exceptions.holidayapi.HolidayApiException;
import com.mkwiatkowski.holidayinfo.model.holidayapi.HolidayApiHoliday;
import com.mkwiatkowski.holidayinfo.model.holidayapi.HolidayApiResponse;
import com.mkwiatkowski.holidayinfo.service.ExternalHolidayService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.InvalidUrlException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handles requests to Holiday API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayApiService implements ExternalHolidayService<HolidayApiHoliday> {

    private final RestTemplate restTemplate;
    private final HolidayApiProperties properties;

    /**
     * Gets list of holidays on given year in given country.
     *
     * @param countryCode 2 letter country code that is used
     * @param year        calendar year
     * @return list of {@link HolidayApiHoliday} returned by Holiday API
     */
    public List<HolidayApiHoliday> getForYear(String countryCode, Integer year) {
        properties.validate();
        if (StringUtils.isBlank(countryCode) || Objects.isNull(year)) {
            log.warn("Parameters missing while calling Holiday API, country={}, year={}", countryCode, year);
            return Collections.emptyList();
        }

        try {
            String url = buildUrl(countryCode, year);
            ResponseEntity<HolidayApiResponse> response = restTemplate.getForEntity(url, HolidayApiResponse.class);
            log.debug("Called Holiday API {} and got response with code: {} and body: {}",
                    url, response.getStatusCode(), response.getBody());
            checkForErrors(response);
            return Optional.ofNullable(response.getBody())
                    .map(HolidayApiResponse::holidays)
                    .orElse(Collections.emptyList());
        } catch (RestClientException | InvalidUrlException e) {
            log.error("Exception occurred while calling Holiday API.", e);
            throw new HolidayApiException(
                    "Holiday API call failed, please contact support for more details. Message: " + e.getMessage());
        }
    }

    private String buildUrl(String countryCode, int year) {
        return UriComponentsBuilder
                .fromUriString(properties.url())
                .queryParam("key", properties.apiKey())
                .queryParam("country", countryCode)
                .queryParam("year", year)
                .build()
                .toUri()
                .toString();
    }

    private void checkForErrors(ResponseEntity<HolidayApiResponse> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            String error = Optional.ofNullable(response.getBody())
                    .map(HolidayApiResponse::error)
                    .orElse(null);
            String errorMessage = String.format("Holiday API returned HTTP status code %s with error message: %s",
                    response.getStatusCode(), error);
            log.error(errorMessage);
            throw new HolidayApiException(errorMessage);
        }
    }
}
