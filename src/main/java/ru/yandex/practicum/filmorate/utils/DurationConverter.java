package ru.yandex.practicum.filmorate.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.time.Duration;

@JsonComponent
public class DurationConverter {

    public static class Deserialize extends JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(com.fasterxml.jackson.core.JsonParser jp, DeserializationContext ctxt) {
            try {
                Integer durationAsInteger = jp.getIntValue();
                if (durationAsInteger == null) {
                    return null;
                } else {
                    return Duration.ofMinutes(((long) durationAsInteger));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
