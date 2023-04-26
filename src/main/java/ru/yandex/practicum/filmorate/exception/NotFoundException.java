package ru.yandex.practicum.filmorate.exception;

public class NotFoundException extends IllegalArgumentException {
    public NotFoundException() {

    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
