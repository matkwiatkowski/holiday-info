package com.mkwiatkowski.holidayinfo.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mkwiatkowski.holidayinfo.config.holidayapi.HolidayApiProperties;
import com.mkwiatkowski.holidayinfo.exceptions.holidayapi.HolidayApiException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HolidayApiPropertiesTest {

    private static final String URL = "https://holidayapi.com/v1/holidays";
    private static final String API_KEY = UUID.randomUUID().toString();

    @Test
    void shouldAcceptProperConfiguration() {
        assertDoesNotThrow(() -> new HolidayApiProperties(URL, API_KEY).validate());
    }

    @Test
    void shouldThrowExceptionWhenApiKeyIsMissing() {
        HolidayApiProperties properties = new HolidayApiProperties(URL, null);

        assertThrows(HolidayApiException.class, properties::validate, "Missing api key for Holiday API.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void shouldThrowExceptionWhenApiKeyIsEmpty(String apiKey) {
        HolidayApiProperties properties = new HolidayApiProperties(URL, apiKey);

        assertThrows(HolidayApiException.class, properties::validate, "Missing api key for Holiday API.");
    }

    @Test
    void shouldThrowExceptionWhenUrlIsMissing() {
        HolidayApiProperties properties = new HolidayApiProperties(null, API_KEY);

        assertThrows(HolidayApiException.class, properties::validate, "Missing url for Holiday API.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void shouldThrowExceptionWhenUrlIsEmpty(String url) {
        HolidayApiProperties properties = new HolidayApiProperties(url, API_KEY);

        assertThrows(HolidayApiException.class, properties::validate, "Missing url for Holiday API.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-valid-url", "htt://url-but-not-quite", "http://test:not-port/path"})
    void shouldThrowExceptionWhenUrlHasInvalidFormat(String url) {
        HolidayApiProperties properties = new HolidayApiProperties(url, API_KEY);

        assertThrows(HolidayApiException.class, properties::validate, "Holiday API url has invalid format: " + url);
    }
}