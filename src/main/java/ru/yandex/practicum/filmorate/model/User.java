package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    int id;
    @Email
    String email;

    @NotBlank
    @Pattern(regexp = "[0-9A-Za-z]+")
    String login;

    String name;

    @PastOrPresent
    LocalDate birthday;
}
