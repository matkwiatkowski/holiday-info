package com.mkwiatkowski.holidayinfo.config;

import com.mkwiatkowski.holidayinfo.config.holidayapi.HolidayApiProperties;
import com.mkwiatkowski.holidayinfo.service.ExternalHolidayService;
import com.mkwiatkowski.holidayinfo.service.holidayapi.HolidayApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Based on configuration enables different implementations of {@link ExternalHolidayService}. Currently available:
 * {@link HolidayApiService}.
 */
@Configuration
public class ExternalHolidayServiceProvider {

    // TODO: add logic to change implementation of holiday data source
    @Bean
    public ExternalHolidayService externalHolidayService(RestTemplate restTemplate, HolidayApiProperties properties) {
        properties.validate();
        return new HolidayApiService(restTemplate, properties);
    }
}
