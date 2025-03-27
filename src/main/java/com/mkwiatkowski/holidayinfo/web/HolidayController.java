package com.mkwiatkowski.holidayinfo.web;

import com.mkwiatkowski.holidayinfo.model.HolidayData;
import com.mkwiatkowski.holidayinfo.service.HolidayService;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handling API calls for getting holidays in selected countries.
 */
@RestController
@RequestMapping("api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    /**
     * Handles GET request for upcoming holidays.
     *
     * @param countryCode list of ISO country codes, required
     * @param from date from which application will start searching for holidays, required
     * @return map of all requested countries with corresponding holiday names and date.
     */
    @GetMapping("upcoming")
    public Map<String, String> getUpcomingForCountries(@RequestParam Set<String> countryCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from) {
        Map<String, HolidayData> upcoming = holidayService.getUpcoming(countryCode, from);
        Map<String, String> response = upcoming.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getName()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        response.put("date", upcoming.values().stream()
                .map(HolidayData::getDate)
                .map(LocalDate::toString)
                .findFirst()
                .orElse(""));

        return response;
    }
}
