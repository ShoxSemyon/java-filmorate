package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Map;


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

    private Map<Long, FriendshipStatus> myFriend;

    public void setFriend(Long id, FriendshipStatus friendshipStatus) {
        myFriend.put(id, friendshipStatus);
    }

    public void deleteFriend(long id) {

        myFriend.remove(id);

    }
}
