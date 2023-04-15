package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

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
