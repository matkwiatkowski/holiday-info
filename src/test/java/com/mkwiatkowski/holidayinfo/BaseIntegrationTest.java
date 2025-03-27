package com.mkwiatkowski.holidayinfo;

import com.mkwiatkowski.holidayinfo.model.holidayapi.HolidayApiHoliday;
import com.mkwiatkowski.holidayinfo.model.holidayapi.HolidayApiHolidayDataBuilder;
import java.net.URI;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Base class for Spring integration testing.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = HolidayInformationApplication.class)
@TestPropertySource("classpath:application.properties")
public abstract class BaseIntegrationTest {

    protected static final Integer YEAR = 2024;
    protected static final String COUNTRY_CODE = "DE";
    protected static final LocalDate DATE_FROM = LocalDate.of(2024, 10, 10);
    protected static final String API_KEY = "some-api-key";
    protected static final String BASE_URL = "https://holidayapi.com/v1/holidays";
    protected static final HolidayApiHoliday HOLIDAY = new HolidayApiHolidayDataBuilder()
            .withName("First Day of Christmas")
            .withCountry(COUNTRY_CODE)
            .withDate(LocalDate.of(2024, 12, 25))
            .withPublic(true)
            .build();
    protected static final String SERVER_RESPONSE = """
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
    protected RestTemplate restTemplate;

    protected MockRestServiceServer mockServer;

    @BeforeEach
    protected void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    protected URI buildHolidayApiUri() {
        return UriComponentsBuilder
                .fromUriString(BASE_URL)
                .queryParam("key", API_KEY)
                .queryParam("country", COUNTRY_CODE)
                .queryParam("year", YEAR)
                .build()
                .toUri();
    }
}
