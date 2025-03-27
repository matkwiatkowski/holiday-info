package com.mkwiatkowski.holidayinfo.config.holidayapi;

import com.mkwiatkowski.holidayinfo.exceptions.holidayapi.HolidayApiException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds all properties that used for configuring Holiday API calls.
 *
 * @param url    full URL to Holiday API service, should contain path to resource but not query params
 * @param apiKey key generated on Holiday API site used for authentication
 */
@Slf4j
@ConfigurationProperties(prefix = "holiday-info.holiday-api")
public record HolidayApiProperties(String url, String apiKey) {

    /**
     * Validates if all configuration properties for Holiday API are properly set. NOTE: this should be called only if Holiday API is
     * enabled in the system. Checks:
     * <ul>
     *     <li>apiKey - if present, does not check it's format,</li>
     *     <li>url - if present and if it is a valid URL.</li>
     * </ul>
     */
    public void validate() {
        if (StringUtils.isBlank(apiKey)) {
            log.error("Missing api key for Holiday API. Please check your configuration.");
            throw new HolidayApiException("Missing api key for Holiday API.");
        }
        if (StringUtils.isBlank(url)) {
            log.error("Missing url for Holiday API. Please check your configuration.");
            throw new HolidayApiException("Missing url for Holiday API.");
        }
        try {
            new URI(url).toURL();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            log.error("Invalid url for Holiday API: {}. Please check your configuration.", url);
            throw new HolidayApiException("Holiday API url has invalid format: " + url);
        }
    }
}
