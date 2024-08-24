package ru.practicum.mapper;

import ru.practicum.exception.WrongDateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateMapper {
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime formatToDateTime(String time) {
        try {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(PATTERN));
        } catch (Exception e) {
            throw new WrongDateFormat("Wrong date format");
        }
    }

    public static String formatToString(LocalDateTime time) {
        try {
            return time.format(DateTimeFormatter.ofPattern(PATTERN));
        } catch (Exception e) {
            throw new WrongDateFormat("Wrong date format");
        }
    }
}