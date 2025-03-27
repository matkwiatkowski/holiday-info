package com.mkwiatkowski.holiday_info.service.holiday_api;

import com.mkwiatkowski.holiday_info.BaseIntegrationTest;
import com.mkwiatkowski.holiday_info.model.HolidayData;
import com.mkwiatkowski.holiday_info.service.ExternalHolidayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class HolidayApiServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ExternalHolidayService externalHolidayService;

    @Test
    void shouldParseResponseFromExternalService() {
        mockServer.expect(ExpectedCount.once(), requestTo(buildHolidayApiUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(SERVER_RESPONSE));

        List<HolidayData> result = externalHolidayService.getForYear(COUNTRY_CODE, YEAR);

        assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(HOLIDAY);
    }
}