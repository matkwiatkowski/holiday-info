package com.mkwiatkowski.holiday_info.service.holiday_api;

import com.mkwiatkowski.holiday_info.HolidayInformationApplication;
import com.mkwiatkowski.holiday_info.model.holiday_api.HolidayApiHoliday;
import com.mkwiatkowski.holiday_info.model.holiday_api.HolidayApiHolidayDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = HolidayInformationApplication.class)
@TestPropertySource("classpath:application.properties")
class HolidayApiServiceIntegrationTest {

    private static final Integer YEAR = 2024;
    private static final String COUNTRY_CODE = "DE";
    private static final String API_KEY = "some-api-key";
    private static final String BASE_URL = "https://holidayapi.com/v1/holidays";
    private static final HolidayApiHoliday HOLIDAY =  new HolidayApiHolidayDataBuilder()
            .withName("First Day of Christmas")
            .withCountry(COUNTRY_CODE)
            .withDate(LocalDate.of(2024, 12, 25))
            .withPublic(true)
            .build();
    private static final String SERVER_RESPONSE = """
            {
                "status": 200,
                "warning": "These results do not include state and province holidays. For more information, please visit https://holidayapi.com/docs",
                "requests": {
                    "used": 25,
                    "available": 9975,
                    "resets": "2025-04-01 00:00:00"
                },
                "holidays": [
                    {
                        "name": "First Day of Christmas",
                        "date": "2024-12-25",
                        "observed": "2024-12-25",
                        "public": true,
                        "country": "DE",
                        "uuid": "1e0d04a4-f9e3-4cf2-9285-d0621b2ef29b",
                        "weekday": {
                            "date": {
                                "name": "Wednesday",
                                "numeric": "3"
                            },
                            "observed": {
                                "name": "Wednesday",
                                "numeric": "3"
                            }
                        }
                    }
                ]
            }
            """;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HolidayApiService holidayApiService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldParseResponseFromExternalService() {
        mockServer.expect(ExpectedCount.once(), requestTo(buildHolidayApiUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(SERVER_RESPONSE));


        List<HolidayApiHoliday> result = holidayApiService.getForYear(COUNTRY_CODE, YEAR);

        assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(HOLIDAY);
    }

    private URI buildHolidayApiUri() {
        return UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("key", API_KEY)
                .queryParam("country", COUNTRY_CODE)
                .queryParam("year", YEAR)
                .build()
                .toUri();
    }
}