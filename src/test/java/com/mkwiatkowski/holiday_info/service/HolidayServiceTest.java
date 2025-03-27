package com.mkwiatkowski.holiday_info.service;

import com.mkwiatkowski.holiday_info.exceptions.BadRequestException;
import com.mkwiatkowski.holiday_info.model.HolidayData;
import com.mkwiatkowski.holiday_info.model.holiday_api.HolidayApiHolidayDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    private static final LocalDate FROM_DATE = LocalDate.of(2024, 1, 1);

    private static final String US_CODE = "US";
    private static final String DE_CODE = "DE";
    private static final String PL_CODE = "PL";

    private static final HolidayData US_HOLIDAY_1 = new HolidayApiHolidayDataBuilder()
            .withCountry(US_CODE)
            .withDate(FROM_DATE.minusDays(1))
            .build();
    private static final HolidayData US_HOLIDAY_2 = new HolidayApiHolidayDataBuilder()
            .withCountry(US_CODE)
            .withDate(FROM_DATE.plusDays(2))
            .build();
    private static final HolidayData US_HOLIDAY_3 = new HolidayApiHolidayDataBuilder()
            .withCountry(US_CODE)
            .withDate(FROM_DATE.plusDays(3))
            .build();
    private static final HolidayData US_HOLIDAY_4 = new HolidayApiHolidayDataBuilder()
            .withCountry(US_CODE)
            .withDate(FROM_DATE.plusDays(4))
            .build();

    private static final HolidayData DE_HOLIDAY_1 = new HolidayApiHolidayDataBuilder()
            .withCountry(DE_CODE)
            .withDate(FROM_DATE.minusDays(1))
            .build();
    private static final HolidayData DE_HOLIDAY_2 = new HolidayApiHolidayDataBuilder()
            .withCountry(DE_CODE)
            .withDate(FROM_DATE.plusDays(3))
            .build();
    private static final HolidayData DE_HOLIDAY_3 = new HolidayApiHolidayDataBuilder()
            .withCountry(DE_CODE)
            .withDate(FROM_DATE.plusDays(4))
            .build();

    @Mock
    private ExternalHolidayService externalHolidayService;

    @InjectMocks
    private HolidayService holidayService;

    @BeforeEach
    void setUp() {
        lenient().when(externalHolidayService.getForYear(US_CODE, FROM_DATE.getYear()))
                .thenReturn(List.of(US_HOLIDAY_1, US_HOLIDAY_2, US_HOLIDAY_3, US_HOLIDAY_4));
        lenient().when(externalHolidayService.getForYear(DE_CODE, FROM_DATE.getYear()))
                .thenReturn(List.of(DE_HOLIDAY_1, DE_HOLIDAY_2, DE_HOLIDAY_3));
    }

    @Test
    void shouldGetNextHolidaysForSingleCountry() {
        Map<String, HolidayData> result = holidayService.getUpcoming(Set.of(US_CODE), FROM_DATE);

        assertThat(result)
                .isNotEmpty()
                .containsEntry(US_CODE, US_HOLIDAY_2);
    }

    @Test
    void shouldGetNextHolidaysForMultipleCountries() {
        Map<String, HolidayData> result = holidayService.getUpcoming(Set.of(US_CODE, DE_CODE), FROM_DATE);

        assertThat(result)
                .isNotEmpty()
                .containsEntry(US_CODE, US_HOLIDAY_3)
                .containsEntry(DE_CODE, DE_HOLIDAY_2);
    }

    @Test
    void shouldGoToNextYearIfCouldNotFindAnythingInCurrentYear() {
        when(externalHolidayService.getForYear(US_CODE, FROM_DATE.getYear()))
                .thenReturn(List.of(US_HOLIDAY_1));
        HolidayData usNextYear = new HolidayApiHolidayDataBuilder()
                .withCountry(US_CODE)
                .withDate(FROM_DATE.plusYears(1))
                .build();
        when(externalHolidayService.getForYear(US_CODE, FROM_DATE.getYear() + 1))
                .thenReturn(List.of(usNextYear));

        when(externalHolidayService.getForYear(DE_CODE, FROM_DATE.getYear()))
                .thenReturn(List.of(DE_HOLIDAY_1));
        HolidayData deNextYear = new HolidayApiHolidayDataBuilder()
                .withCountry(DE_CODE)
                .withDate(FROM_DATE.plusYears(1))
                .build();
        when(externalHolidayService.getForYear(DE_CODE, FROM_DATE.getYear() + 1))
                .thenReturn(List.of(deNextYear));

        Map<String, HolidayData> result = holidayService.getUpcoming(Set.of(US_CODE, DE_CODE), FROM_DATE);

        assertThat(result)
                .isNotEmpty()
                .containsEntry(US_CODE, usNextYear)
                .containsEntry(DE_CODE, deNextYear);
    }

    @Test
    void shouldNotGetHolidaysOnFromDate() {
        HolidayData usOnFromDate = new HolidayApiHolidayDataBuilder()
                .withCountry(US_CODE)
                .withDate(FROM_DATE)
                .build();
        when(externalHolidayService.getForYear(US_CODE, FROM_DATE.getYear()))
                .thenReturn(List.of(US_HOLIDAY_3, usOnFromDate));
        HolidayData deOnFromDate = new HolidayApiHolidayDataBuilder()
                .withCountry(DE_CODE)
                .withDate(FROM_DATE)
                .build();
        when(externalHolidayService.getForYear(DE_CODE, FROM_DATE.getYear()))
                .thenReturn(List.of(DE_HOLIDAY_2, deOnFromDate));

        Map<String, HolidayData> result = holidayService.getUpcoming(Set.of(US_CODE, DE_CODE), FROM_DATE);

        assertThat(result)
                .isNotEmpty()
                .containsEntry(US_CODE, US_HOLIDAY_3)
                .containsEntry(DE_CODE, DE_HOLIDAY_2);
    }

    @Test
    void shouldThrowExceptionIfCountryCodesAreEmpty() {
        assertThrows(BadRequestException.class,
                () -> holidayService.getUpcoming(Collections.emptySet(), FROM_DATE),
                "Country codes have to be specified.");
        verify(externalHolidayService, never()).getForYear(anyString(), anyInt());
    }

    @Test
    void shouldThrowExceptionIfCountryCodesAreMissing() {
        assertThrows(BadRequestException.class,
                () -> holidayService.getUpcoming(null, FROM_DATE),
                "Country codes have to be specified.");
        verify(externalHolidayService, never()).getForYear(anyString(), anyInt());
    }

    @Test
    void shouldThrowExceptionIfCountryCodeIsInvalid() {
        assertThrows(BadRequestException.class,
                () -> holidayService.getUpcoming(Set.of("invalid-code", US_CODE, PL_CODE), FROM_DATE),
                "Some country codes are invalid: invalid-code");
        verify(externalHolidayService, never()).getForYear(anyString(), anyInt());
    }

    @Test
    void shouldThrowExceptionIfMultipleCountryCodesAreInvalid() {
        assertThrows(BadRequestException.class,
                () -> holidayService.getUpcoming(Set.of("invalid-code", "not-a-code", PL_CODE), FROM_DATE),
                "Some country codes are invalid: invalid-code, not-a-code");
        verify(externalHolidayService, never()).getForYear(anyString(), anyInt());
    }

    @Test
    void shouldThrowExceptionIfDateFromIsMissing() {
        assertThrows(BadRequestException.class,
                () -> holidayService.getUpcoming(Set.of(US_CODE, DE_CODE), null),
                "Date have to be specified");
        verify(externalHolidayService, never()).getForYear(anyString(), anyInt());
    }

    @Test
    void shouldReturnEmptyMapIfNoHolidaysCouldBeFoundInYearsLimit() {
        when(externalHolidayService.getForYear(eq(US_CODE), anyInt())).thenReturn(List.of(US_HOLIDAY_1));
        when(externalHolidayService.getForYear(eq(DE_CODE), anyInt())).thenReturn(List.of(DE_HOLIDAY_1));

        Map<String, HolidayData> result = holidayService.getUpcoming(Set.of(US_CODE, DE_CODE), FROM_DATE);

        assertThat(result).isEmpty();
        verify(externalHolidayService, times(10)).getForYear(eq(US_CODE), anyInt());
        verify(externalHolidayService, times(10)).getForYear(eq(DE_CODE), anyInt());
    }

    @Test
    void shouldAcceptEmptyListOfHolidaysFromExternalService() {
        when(externalHolidayService.getForYear(eq(US_CODE), anyInt())).thenReturn(Collections.emptyList());
        when(externalHolidayService.getForYear(eq(DE_CODE), anyInt())).thenReturn(Collections.emptyList());

        Map<String, HolidayData> result = holidayService.getUpcoming(Set.of(US_CODE, DE_CODE), FROM_DATE);

        assertThat(result).isEmpty();
        verify(externalHolidayService, times(10)).getForYear(eq(US_CODE), anyInt());
        verify(externalHolidayService, times(10)).getForYear(eq(DE_CODE), anyInt());
    }
}