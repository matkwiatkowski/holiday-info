package com.mkwiatkowski.holiday_info.model.holiday_api;

import java.time.LocalDate;

public class HolidayApiHolidayDataBuilder {

    private static int instanceNumber = 0;

    private String name = "holiday-" + ++instanceNumber;
    private LocalDate date = LocalDate.of(2024, 11, 28);
    private String country = "US";
    private Boolean isPublic = true;

    public HolidayApiHolidayDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public HolidayApiHolidayDataBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public HolidayApiHolidayDataBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public HolidayApiHolidayDataBuilder withPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    public HolidayApiHoliday build() {
        return new HolidayApiHoliday(name, date, country, isPublic);
    }
}