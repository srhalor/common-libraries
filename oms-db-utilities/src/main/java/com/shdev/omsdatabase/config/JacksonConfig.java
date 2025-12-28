package com.shdev.omsdatabase.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Jackson configuration to enable Java 8 date/time support.
 * Registers JavaTimeModule for proper serialization/deserialization of
 * LocalDate, LocalDateTime, Instant, etc.
 * <p>
 * Includes custom OffsetDateTime deserializer to handle date-only strings
 * (e.g., "2024-06-01") by defaulting to 00:00:00 UTC.
 * <p>
 * This configuration is provided by oms-db-utilities to ensure all services
 * using OMS DTOs can properly serialize/deserialize date/time fields.
 *
 * @author Shailesh Halor
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Custom module for flexible OffsetDateTime deserialization
        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(OffsetDateTime.class, new FlexibleOffsetDateTimeDeserializer());
        mapper.registerModule(customModule);

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Custom deserializer for OffsetDateTime that accepts:
     * 1. Full ISO-8601 datetime with timezone: "2024-06-01T10:30:00Z" or "2024-06-01T10:30:00+01:00"
     * 2. Date-only string: "2024-06-01" (defaults to 00:00:00 UTC)
     */
    private static class FlexibleOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

        @Override
        public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getText();

            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }

            try {
                // Try parsing as full OffsetDateTime first
                return OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (DateTimeParseException e1) {
                try {
                    // Try parsing as ISO_DATE_TIME (with time but maybe no offset)
                    return OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
                } catch (DateTimeParseException e2) {
                    try {
                        // Try parsing as date-only string and default to 00:00:00 UTC
                        LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
                        return localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
                    } catch (DateTimeParseException e3) {
                        throw new IOException("Unable to parse date string: " + dateString +
                            ". Expected formats: 'yyyy-MM-dd', 'yyyy-MM-dd'T'HH:mm:ss', or 'yyyy-MM-dd'T'HH:mm:ssXXX'", e3);
                    }
                }
            }
        }
    }
}

