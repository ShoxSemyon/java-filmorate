package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
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
