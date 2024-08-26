package ru.practicum.exception;

public class WrongInitiatorException extends RuntimeException {
    public WrongInitiatorException(String message) {
        super(message);
    }
}
