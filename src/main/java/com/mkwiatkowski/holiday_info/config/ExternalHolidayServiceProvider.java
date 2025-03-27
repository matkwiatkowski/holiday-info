package com.mkwiatkowski.holiday_info.config;

import com.mkwiatkowski.holiday_info.config.holiday_api.HolidayApiProperties;
import com.mkwiatkowski.holiday_info.service.ExternalHolidayService;
import com.mkwiatkowski.holiday_info.service.holiday_api.HolidayApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Based on configuration enables different implementations of {@link ExternalHolidayService}.
 * Currently available: {@link HolidayApiService}.
 */
@Configuration
public class ExternalHolidayServiceProvider {

    // TODO: add logic to change implementation of holiday data source
    @Bean
    public ExternalHolidayService externalHolidayService(
            RestTemplate restTemplate, HolidayApiProperties properties) {
        return new HolidayApiService(restTemplate, properties);
    }
}
