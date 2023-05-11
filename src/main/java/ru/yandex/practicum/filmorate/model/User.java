package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class User {

    @NotNull
    private long id;

    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "[0-9A-Za-z]+")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    private Set<Long> myFriend;

    public boolean setFriend(Long id) {

        return myFriend.add(id);
    }

    public boolean deleteFriend(long id) {

        return myFriend.remove(id);

    }
}
