package com.mkwiatkowski.holidayinfo.service;

import com.mkwiatkowski.holidayinfo.exceptions.BadRequestException;
import com.mkwiatkowski.holidayinfo.model.HolidayData;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Uses external services to get holiday data and filter them with given criteria.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

    @Value("${holiday-info.years-search-limit}")
    private Integer yearsLimit = 10;

    private final ExternalHolidayService externalHolidayService;


    /**
     * Using configured implementation of {@link ExternalHolidayService} gets holidays calendar year by year and finds closest upcoming
     * holiday that will happen on the same day for all requested countries.
     *
     * @param countryCodes set of unique ISO country codes
     * @param from         date used as a start of search, will not be included if holidays are available on that date
     * @return map where key is country code and value is given country holiday that meets criteria
     * @throws BadRequestException when parameter are invalid or missing
     */
    public Map<String, HolidayData> getUpcoming(Set<String> countryCodes, LocalDate from) {
        log.debug("Searching upcoming holidays for {} after {}", countryCodes, from);

        validateParams(countryCodes, from);

        int year = from.getYear();
        do {
            Map<String, List<HolidayData>> holidaysForCountries = getHolidaysForCountries(countryCodes, year);
            Set<LocalDate> intersectingDates = getIntersectingDates(holidaysForCountries, from);

            if (!intersectingDates.isEmpty()) {
                return getHolidaysOnClosestDate(holidaysForCountries, intersectingDates);
            }

            year++;
        } while (from.getYear() + yearsLimit > year);

        log.debug("Reached the year search limit");
        return new HashMap<>();
    }

    /**
     * Has to use for loop to allow exceptions from {@link ExternalHolidayService} to go through.
     */
    private Map<String, List<HolidayData>> getHolidaysForCountries(Set<String> countryCodes, int year) {
        Map<String, List<HolidayData>> holidays = new HashMap<>();
        for (String code : countryCodes) {
            holidays.put(code, externalHolidayService.getForYear(code, year));
        }
        return holidays;
    }

    private Set<LocalDate> getIntersectingDates(Map<String, List<HolidayData>> holidaysForCountries, LocalDate from) {
        Set<LocalDate> intersecting = new HashSet<>();
        for (List<HolidayData> countryHolidays : holidaysForCountries.values()) {
            Set<LocalDate> dates = countryHolidays.stream()
                    .map(HolidayData::getDate)
                    .filter(date -> date.isAfter(from))
                    .collect(Collectors.toSet());

            if (intersecting.isEmpty()) {
                intersecting.addAll(dates);
                continue;
            }
            intersecting.retainAll(dates);

            if (intersecting.isEmpty()) {
                return intersecting;
            }
        }
        return intersecting;
    }

    private Map<String, HolidayData> getHolidaysOnClosestDate(Map<String, List<HolidayData>> holidaysForCountries,
            Set<LocalDate> intersectingDates) {
        Optional<LocalDate> closestDate = intersectingDates.stream().sorted().findFirst();
        return closestDate.map(localDate -> holidaysForCountries.entrySet().stream()
                        .map(entry -> Map.entry(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .filter(holiday -> holiday.getDate().equals(localDate))
                                        .findFirst()
                                        .orElse(null)))
                        .filter(entry -> Objects.nonNull(entry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .orElseGet(HashMap::new);

    }

    private void validateParams(Set<String> countryCodes, LocalDate from) {
        if (Objects.isNull(from)) {
            throw new BadRequestException("Date have to be specified");
        }
        if (CollectionUtils.isEmpty(countryCodes)) {
            throw new BadRequestException("Country codes have to be specified.");
        }

        List<String> isoCountries = List.of(Locale.getISOCountries());
        List<String> invalidCountryCodes = countryCodes.stream()
                .filter(code -> !isoCountries.contains(code))
                .toList();
        if (!CollectionUtils.isEmpty(invalidCountryCodes)) {
            throw new BadRequestException("Some country codes are invalid: " + invalidCountryCodes);
        }
    }
}
