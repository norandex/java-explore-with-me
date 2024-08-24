package ru.practicum.exception;

public class WrongDateFormat extends RuntimeException {
    public WrongDateFormat(String message) {
        super(message);
    }
}
