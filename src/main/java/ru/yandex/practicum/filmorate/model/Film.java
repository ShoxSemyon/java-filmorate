package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    @NotNull
    private long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;

    private Duration duration;
    private Set<Long> userLikeIds;

    private Set<Genre> genres;

    @NotNull
    private Rating mpa;

    public boolean setLike(Long id) {

        return userLikeIds.add(id);
    }

    public void setGenre(Genre genre) {
        genres.add(genre);
    }

    public boolean deleteLike(long id) {

        return userLikeIds.remove(id);

    }

    @JsonProperty("duration")
    public long getDurationTimeMinutes() {
        return duration.toMinutes();
    }
}
