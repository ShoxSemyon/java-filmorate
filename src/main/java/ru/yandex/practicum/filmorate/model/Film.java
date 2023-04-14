package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import org.springframework.boot.convert.DurationUnit;


import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class Film {
    int id;

    @NotBlank
    String name;

    @Size(max = 200)
    String description;

    LocalDate releaseDate;


    Duration duration;

    @JsonProperty("duration")
    public long getDurationTimeMinutes() {
        return duration.toMinutes();
    }
}
