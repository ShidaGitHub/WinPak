package ru.it_mm.winpak.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class Utils {
    private Utils(){}

    public static Date minDate(Date date1, Date date2) {
        return date1.before(date2) ? date1 : date2;
    }

    public static Date maxDate(Date date1, Date date2) {
        return date1.after(date2) ? date1 : date2;
    }

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }
    
    public static Date atStartOfMonth(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfMonth = localDateTime.withDayOfMonth(1).with(LocalTime.MIN);
        return localDateTimeToDate(startOfMonth);
    }
    
    public static Date atEndOfMonth(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfMonth = localDateTime.withDayOfMonth(localDateTime.toLocalDate().lengthOfMonth()).with(LocalTime.MAX);
        return localDateTimeToDate(endOfMonth);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
          
    public static boolean isHoliday(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return localDateTime.getDayOfWeek().getValue() > 5;
    }
    
    public static String validName(String name){
        return name.replaceAll("[:\\\\/*?|<>]", "_");
    }
    
}
