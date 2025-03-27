package com.mkwiatkowski.holiday_info.web;

import com.mkwiatkowski.holiday_info.model.HolidayData;
import com.mkwiatkowski.holiday_info.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

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
