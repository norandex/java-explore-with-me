package ru.practicum.mapper;

import ru.practicum.exceptions.WrongDateFormatException;
import ru.practicum.util.DatePattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateMapper {
    ;

    public static LocalDateTime formatToDateTime(String time) {
        try {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DatePattern.PATTERN));
        } catch (WrongDateFormatException e) {
            throw new WrongDateFormatException("Wrong date format");
        }
    }

    public static String formatToString(LocalDateTime time) {
        try {
            return time.format(DateTimeFormatter.ofPattern(DatePattern.PATTERN));
        } catch (WrongDateFormatException e) {
            throw new WrongDateFormatException("Wrong date format");
        }
    }
}
