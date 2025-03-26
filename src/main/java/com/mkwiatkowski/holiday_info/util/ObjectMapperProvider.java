package com.mkwiatkowski.holiday_info.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Provides instance of {@link ObjectMapper} with registered {@link JavaTimeModule} for whole application.
 */
public class ObjectMapperProvider {

    public static final ObjectMapper INSTANCE = new ObjectMapper();

    static {
        INSTANCE.registerModule(new JavaTimeModule());
    }

    private ObjectMapperProvider() {
        throw new IllegalStateException("This class should not have an instance.");
    }
}
