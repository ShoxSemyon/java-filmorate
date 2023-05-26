package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Rating {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");


    private long id;
    private String name;

    Rating(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonCreator
    public static Rating forValue(@JsonProperty("id") int id) {
        for (Rating rating : Rating.values()) {
            if (rating.id == id) {
                return rating;
            }
        }

        return null;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
