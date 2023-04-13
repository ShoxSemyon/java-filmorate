package ru.yandex.practicum.filmorate.exception;


public class IllegalUserException extends IllegalArgumentException {

    public IllegalUserException(String param) {
        super(param);
    }
}
