package com.mkwiatkowski.holiday_info.web;

import com.mkwiatkowski.holiday_info.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HolidayControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void shouldReturnHolidays() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(buildHolidayApiUri()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(SERVER_RESPONSE));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/holidays/upcoming")
                        .param("from", DATE_FROM.toString())
                        .param("countryCode", COUNTRY_CODE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(HOLIDAY.getDate().toString()))
                .andExpect(jsonPath("$.DE").value(HOLIDAY.getName()));
    }

    @Test
    void shouldRespondWithBadRequestOnInvalidFromDateFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/holidays/upcoming")
                        .param("from", "2022a10b10")
                        .param("countryCode", COUNTRY_CODE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestOnMissingFromDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/holidays/upcoming")
                        .param("countryCode", COUNTRY_CODE))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestOnMissingCountryCode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/holidays/upcoming")
                        .param("from", DATE_FROM.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}