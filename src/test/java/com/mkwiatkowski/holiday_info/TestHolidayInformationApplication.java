package com.mkwiatkowski.holiday_info;

import org.springframework.boot.SpringApplication;

public class TestHolidayInformationApplication {

  public static void main(String[] args) {
    SpringApplication.from(HolidayInformationApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
