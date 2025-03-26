package com.mkwiatkowski.holiday_info.service.holiday_api;

import com.mkwiatkowski.holiday_info.config.holiday_api.HolidayApiProperties;
import com.mkwiatkowski.holiday_info.exceptions.holiday_api.HolidayApiException;
import com.mkwiatkowski.holiday_info.model.holiday_api.HolidayApiHoliday;
import com.mkwiatkowski.holiday_info.model.holiday_api.HolidayApiHolidayDataBuilder;
import com.mkwiatkowski.holiday_info.model.holiday_api.HolidayApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayApiServiceTest {

    private static final Integer YEAR = 2024;
    private static final String COUNTRY_CODE = "US";
    private static final String API_KEY = "some-api-key";
    private static final String BASE_URL = "http://holiday-api:8080";
    private static final String EXAMPLE_URL = buildHolidayApiUrl();
    private static final HolidayApiHoliday HOLIDAY = new HolidayApiHolidayDataBuilder()
            .withName("Some holiday")
            .build();
    private static final HolidayApiHoliday HOLIDAY_2 = new HolidayApiHolidayDataBuilder()
            .withName("Another holiday")
            .build();
    private static final ResponseEntity<HolidayApiResponse> SINGLE_HOLIDAY_RESPONSE =
            new ResponseEntity<>(new HolidayApiResponse(List.of(HOLIDAY)), HttpStatus.OK);
    private static final ResponseEntity<HolidayApiResponse> MULTIPLE_HOLIDAY_RESPONSE =
            new ResponseEntity<>(new HolidayApiResponse(List.of(HOLIDAY, HOLIDAY_2)), HttpStatus.OK);
    private static final ResponseEntity<HolidayApiResponse> EMPTY_RESPONSE
            = new ResponseEntity<>(new HolidayApiResponse(Collections.emptyList()), HttpStatus.OK);
    private static final ResponseEntity<HolidayApiResponse> ERROR_RESPONSE =
            new ResponseEntity<>(new HolidayApiResponse(null, "holiday api failed"), HttpStatus.BAD_REQUEST);

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HolidayApiProperties holidayApiProperties;

    @InjectMocks
    private HolidayApiService holidayApiService;

    @BeforeEach
    void setUp() {
        lenient().when(holidayApiProperties.apiKey()).thenReturn(API_KEY);
        lenient().when(holidayApiProperties.url()).thenReturn(BASE_URL);
        lenient().when(restTemplate.getForEntity(EXAMPLE_URL, HolidayApiResponse.class)).thenReturn(EMPTY_RESPONSE);
    }

    @Test
    void shouldUseAllParametersInUrl() {
        assertThat(holidayApiService.getForYear(COUNTRY_CODE, YEAR)).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenHolidayApiIsNotReachable() {
        when(restTemplate.getForEntity(anyString(), eq(HolidayApiResponse.class)))
                .thenThrow(new RestClientException("Resource not reachable"));

        assertThrows(HolidayApiException.class,
                () -> holidayApiService.getForYear(COUNTRY_CODE, YEAR),
                "Holiday API call failed, please contact support for more details.");
    }

    @Test
    void shouldThrowExceptionIfHolidayApiReturnedHttpErrorCode() {
        when(restTemplate.getForEntity(EXAMPLE_URL, HolidayApiResponse.class)).thenReturn(ERROR_RESPONSE);

        assertThrows(HolidayApiException.class,
                () -> holidayApiService.getForYear(COUNTRY_CODE, YEAR),
                String.format("Holiday API returned HTTP status code %s with error message: %s",
                        ERROR_RESPONSE.getStatusCode(), ERROR_RESPONSE.getBody().error()));
    }

    @Test
    void shouldThrowExceptionIfHolidayApiUrlOrApiKeyAreNotConfigured() {
        String errorMessage = "validation error message";
        doThrow(new HolidayApiException(errorMessage)).when(holidayApiProperties).validate();

        assertThrows(HolidayApiException.class, () -> holidayApiService.getForYear(COUNTRY_CODE, YEAR), errorMessage);
    }

    @Test
    void shouldParseResponseWithSingleHoliday() {
        when(restTemplate.getForEntity(EXAMPLE_URL, HolidayApiResponse.class)).thenReturn(SINGLE_HOLIDAY_RESPONSE);

        assertThat(holidayApiService.getForYear(COUNTRY_CODE, YEAR)).containsExactly(HOLIDAY);
    }

    @Test
    void shouldParseResponseWithMultipleHolidays() {
        when(restTemplate.getForEntity(EXAMPLE_URL, HolidayApiResponse.class)).thenReturn(MULTIPLE_HOLIDAY_RESPONSE);

        assertThat(holidayApiService.getForYear(COUNTRY_CODE, YEAR)).containsExactlyInAnyOrder(HOLIDAY, HOLIDAY_2);
    }

    @Test
    void shouldReturnEmptyListIfCountryCodeIsMissing() {
        assertThat(holidayApiService.getForYear(null, YEAR)).isEmpty();
    }

    @Test
    void shouldReturnEmptyListIfDateIsMissing() {
        assertThat(holidayApiService.getForYear(COUNTRY_CODE, null)).isEmpty();
    }

    @Test
    void shouldReturnEmptyListIfBodyIsEmpty() {
        when(restTemplate.getForEntity(EXAMPLE_URL, HolidayApiResponse.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThat(holidayApiService.getForYear(COUNTRY_CODE, YEAR)).isEmpty();
    }

    private static String buildHolidayApiUrl() {
        return UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("key", API_KEY)
                .queryParam("country", COUNTRY_CODE)
                .queryParam("year", YEAR)
                .build()
                .toUri()
                .toString();
    }
}