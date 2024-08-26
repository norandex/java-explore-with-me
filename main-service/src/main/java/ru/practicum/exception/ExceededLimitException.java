package ru.practicum.exception;

public class ExceededLimitException extends RuntimeException {
    public ExceededLimitException(String message) {
        super(message);
    }
}
