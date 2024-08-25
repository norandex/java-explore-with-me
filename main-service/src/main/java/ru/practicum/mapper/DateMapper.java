package ru.practicum.mapper;

import ru.practicum.exception.WrongDateFormat;
import ru.practicum.util.DatePattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateMapper {

    public static LocalDateTime formatToDateTime(String time) {
        if (Objects.isNull(time)) {
            return null;
        }
        try {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DatePattern.PATTERN));
        } catch (Exception e) {
            throw new WrongDateFormat("Wrong date format");
        }
    }

    public static String formatToString(LocalDateTime time) {
        if (Objects.isNull(time)) {
            return null;
        }
        try {
            return time.format(DateTimeFormatter.ofPattern(DatePattern.PATTERN));
        } catch (Exception e) {
            throw new WrongDateFormat("Wrong date format");
        }
    }
}