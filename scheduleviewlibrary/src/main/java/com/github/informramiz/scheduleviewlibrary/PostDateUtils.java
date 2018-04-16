package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.github.informramiz.scheduleviewlibrary.Post.DAILY;
import static com.github.informramiz.scheduleviewlibrary.Post.HOURLY;
import static com.github.informramiz.scheduleviewlibrary.Post.MONTHLY;
import static com.github.informramiz.scheduleviewlibrary.Post.NOT_REPEAT;
import static com.github.informramiz.scheduleviewlibrary.Post.WEEKLY;
import static com.github.informramiz.scheduleviewlibrary.Post.YEARLY;

/**
 * Created by Ramiz on 16/04/2018
 */

public class PostDateUtils {
    public static final String DATE_TIME_FORMAT = "EEE, MMM dd yyyy, hh:mm";

    public static final String SUNDAY = "SUN";
    public static final String MONDAY = "MON";
    public static final String TUESDAY = "TUE";
    public static final String WEDNESDAY = "WED";
    public static final String THURSDAY = "THU";
    public static final String FRIDAY = "FRI";
    public static final String SATURDAY = "SAT";
    @Retention(RetentionPolicy.SOURCE)
    @StringDef(value = {SUNDAY, MONDAY, TUESDAY, WEDNESDAY, TUESDAY, FRIDAY, SATURDAY})
    public @interface DayOfWeek{}

    public static String formatDate(int year, int month, int day, Context context){
        Calendar time = Calendar.getInstance();
        time.set(year, month, day);
        DateFormat formatter = java.text.DateFormat.getDateInstance(
                DateFormat.MEDIUM);
        formatter.setTimeZone(time.getTimeZone());
        return formatter.format(time.getTime());

    }

    public static String formatDate(long dateInMillis){
        DateFormat formatter = java.text.DateFormat.getDateInstance(
                DateFormat.MEDIUM);
        return formatter.format(dateInMillis);
    }

    public static String formatDateTime(Date date){
//        Calendar cal = Calendar.getInstance();
//        DateFormat formatter = java.text.DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.SHORT);
//        formatter.setTimeZone(cal.getTimeZone());
//        cal.setTime(date);
//        return formatter.format(cal.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String formatDateTime(long timeInMillis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
//        DateFormat formatter = java.text.DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.SHORT);
//        formatter.setTimeZone(cal.getTimeZone());
//        return formatter.format(cal.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(cal.getTime());
    }

    public static String formatTime(long timeInMillis) {
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        return timeFormat.format(timeInMillis);
    }

    public static ArrayList<String> getPostSchedulesSummary(int pastPostsCount,
                                                            long startTime,
                                                            final int totalRepetitions,
                                                            @Post.PostRepeatType String repeatType,
                                                            boolean isRepeatForever,
                                                            @Nullable String adjustByTimeText,
                                                            @Nullable ArrayList<String> weekDayNames) {
        int actualRepetitions = totalRepetitions;
        if (isRepeatForever) {
            actualRepetitions = Post.MAX_REPEAT_COUNT_FOR_SUMMARY + 1;
        }

        List<Long> schedulesList = getPostSchedules(startTime, actualRepetitions, repeatType, weekDayNames);
        ArrayList<String> schedulesListFormatted = new ArrayList<>();

        int repeatCount = pastPostsCount + 1;
        for (int i = 0; i < schedulesList.size(); i++) {
            Long scheduleTimeInMillis = schedulesList.get(i);
            String scheduleText = PostDateUtils.formatDateTime(scheduleTimeInMillis);
            if (adjustByTimeText != null && i != 0) {
                scheduleText += " " + adjustByTimeText;
            }
            scheduleText = repeatCount + ". " + scheduleText;
            schedulesListFormatted.add(scheduleText);

            repeatCount++;
        }

        if (isRepeatForever) {
            String foreverText = repeatCount + ". ...";
            schedulesListFormatted.add(foreverText);
        }

        return schedulesListFormatted;
    }

    public static List<Long> getPostSchedules(long startTime,
                                              int totalRepetitions,
                                              @Post.PostRepeatType String repeatType,
                                              @Nullable ArrayList<String> weekDayNames) {

        if (isWeeklyPostWithCustomDays(repeatType, weekDayNames)) {
            return getWeeklyPostSchedules(startTime, totalRepetitions, weekDayNames);
        }

        //initialize the calendar with first schedule time
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(startTime);

        ArrayList<Long> schedulesList = new ArrayList<>();
        //initial schedule
        schedulesList.add(timeCalendar.getTimeInMillis());
        //if post type is not repeat or total repeat count is < 2 then there
        //is one schedule so return it
        if (repeatType.equalsIgnoreCase(NOT_REPEAT) || totalRepetitions < 2) {
            return schedulesList;
        }

        for (int i = 1; i < totalRepetitions; ++i) {
            switch (repeatType) {
                case HOURLY:
                    timeCalendar.add(Calendar.HOUR, 1);
                    break;
                case DAILY:
                    timeCalendar.add(Calendar.DATE, 1);
                    break;
                case WEEKLY:
                    timeCalendar.add(Calendar.DATE, 7);
                case MONTHLY:
                    timeCalendar.add(Calendar.MONTH, 1);
                    break;
                case YEARLY:
                    timeCalendar.add(Calendar.YEAR, 1);
                    break;
            }
            schedulesList.add(timeCalendar.getTimeInMillis());
        }
        return schedulesList;
    }

    private static boolean isWeeklyPostWithCustomDays(@Post.PostRepeatType String repeatType, @Nullable ArrayList<String> weekDayNames) {
        return repeatType.equalsIgnoreCase(WEEKLY) && weekDayNames != null && !weekDayNames.isEmpty();
    }

    private static List<Long> getWeeklyPostSchedules(long startTime,
                                                     int totalRepetitions,
                                                     @Nullable ArrayList<String> weekDayNames) {
        ArrayList<Integer> weekDays = new ArrayList<>();
        if (weekDayNames != null) {
            weekDays = convertToCalendarDays(weekDayNames);
        }

        //initialize the calendar with first schedule time
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(startTime);

        //using set to avoid duplication
//        Set<Long> schedulesSet = new LinkedHashSet<>();
        List<Long> schedulesList = new ArrayList<>();
        while (schedulesList.size() < totalRepetitions) {
            schedulesList.addAll(getCurrentWeekSchedules(timeCalendar.getTimeInMillis(), weekDays));
            moveCalendarToNextSunday(timeCalendar);
        }

        if (!schedulesList.contains(startTime)) {
            schedulesList.add(startTime);
        }

        Collections.sort(schedulesList);
        schedulesList = schedulesList.subList(0, totalRepetitions);
        return schedulesList;
    }

    private static ArrayList<Long> getCurrentWeekSchedules(long startTimeInMillis, ArrayList<Integer> weekDays) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(startTimeInMillis);

        ArrayList<Long> schedulesList = new ArrayList<>();
        weekDays = getRemainingWeekDaysInCurrentWeek(timeCalendar.getTimeInMillis(), weekDays);
        for (int i = 0; i < weekDays.size(); i++) {
            Integer weekDay = weekDays.get(i);
            timeCalendar.set(Calendar.DAY_OF_WEEK, weekDay);
            schedulesList.add(timeCalendar.getTimeInMillis());
        }

        return schedulesList;
    }

    public static int getFirstFutureDayDate(final long startTimeInMillis,
                                            ArrayList<Integer> selectedWeekDays) {
        Calendar startTimeCalendar = getCalendar(startTimeInMillis);
        ArrayList<Integer> weekDays = new ArrayList<>();
        while (weekDays.isEmpty()) {
            weekDays = getRemainingWeekDaysInCurrentWeek(startTimeCalendar.getTimeInMillis(), selectedWeekDays);
            moveCalendarToNextSunday(startTimeCalendar);
        }

        return weekDays.get(0);
    }

    public static ArrayList<Integer> getRemainingWeekDaysInCurrentWeek(final long startTimeInMillis,
                                                                       ArrayList<Integer> selectedWeekDays) {
        Calendar startTimeCalendar = getCalendar(startTimeInMillis);
        Calendar endTimeCalendar = getCalendar(startTimeInMillis);
        moveCalendarToNextSunday(endTimeCalendar);

        ArrayList<Integer> weekDaysInThisWeek = new ArrayList<>();
        for (Integer weekDay : selectedWeekDays) {
            Calendar dayOfWeek = getCalendar(startTimeInMillis, weekDay);
            if (!dayOfWeek.before(startTimeCalendar) && dayOfWeek.before(endTimeCalendar)) {
                weekDaysInThisWeek.add(weekDay);
            }
        }

        Collections.sort(weekDaysInThisWeek);
        return weekDaysInThisWeek;
    }

    public static void moveCalendarToNextSunday(Calendar timeCalendar) {
        timeCalendar.add(Calendar.DATE, (Calendar.SATURDAY + 1) - timeCalendar.get(Calendar.DAY_OF_WEEK));
    }

    private static Calendar getCalendar(final long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar;
    }

    private static Calendar getCalendar(final long startTimeInMillis, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTimeInMillis);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        return calendar;
    }

    public static Calendar getCalendar(int year, int month, int day, int hours, int minutes) {
        Calendar now = Calendar.getInstance();
        now.set(year, month, day, hours, minutes);
        return now;
    }

    public static long getTimeInMillis(int year, int month, int day, int hours, int minutes) {
        Calendar now = Calendar.getInstance();
        now.set(year, month, day, hours, minutes);
        return now.getTimeInMillis();
    }

    public static int getCalendarDayValue(String dayName) {
        switch (dayName.toUpperCase()) {
            case SUNDAY:
                return Calendar.SUNDAY;
            case MONDAY:
                return Calendar.MONDAY;
            case TUESDAY:
                return Calendar.TUESDAY;
            case WEDNESDAY:
                return Calendar.WEDNESDAY;
            case THURSDAY:
                return Calendar.THURSDAY;
            case FRIDAY:
                return Calendar.FRIDAY;
            case SATURDAY:
                return Calendar.SATURDAY;
        }

        return Calendar.SUNDAY;
    }

    public static String getCalendarDayName(long timeInMillis) {
        Calendar calendar = getCalendar(timeInMillis);
        return getCalendarDayName(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public static String getCalendarDayName(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return SUNDAY;
            case Calendar.MONDAY:
                return MONDAY;
            case Calendar.TUESDAY:
                return TUESDAY;
            case Calendar.WEDNESDAY:
                return WEDNESDAY;
            case Calendar.THURSDAY:
                return THURSDAY;
            case Calendar.FRIDAY:
                return FRIDAY;
            case Calendar.SATURDAY:
                return SATURDAY;
        }

        return SUNDAY;
    }

    public static ArrayList<Integer> convertToCalendarDays(ArrayList<String> dayNames) {
        ArrayList<Integer> calendarDayValues = new ArrayList<>();
        for (String dayName : dayNames) {
            calendarDayValues.add(getCalendarDayValue(dayName));
        }

        return calendarDayValues;
    }

    public static Calendar getFutureDateFromDay(long time, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DAY_OF_WEEK, day);

        return calendar;
    }

    public static Calendar getFutureDateFromDay(int day) {
        return getFutureDateFromDay(Calendar.getInstance().getTimeInMillis(), day);
    }

    public static Calendar getFutureDateFromDay(long startTime, @DayOfWeek String dayName) {
        return getFutureDateFromDay(startTime, getCalendarDayValue(dayName));
    }

    public static Calendar getFutureDateFromDay(@DayOfWeek String dayName) {
        return getFutureDateFromDay(getCalendarDayValue(dayName));
    }

    public static boolean isDateTimeSame(long date1, long date2) {
        Calendar calendar1 = getCalendar(date1);
        Calendar calendar2 = getCalendar(date2);

        return calendar1.equals(calendar2);
    }

    public static boolean isDateInPast(long dateInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        return isDateInPast(calendar);
    }

    public static boolean isDateInPast(Calendar calendar) {
        return !calendar.after(Calendar.getInstance());
    }

    public static long validatePostDateToSchedule(@Nullable Long postDate) {
        if (postDate == null || PostDateUtils.isDateInPast(postDate)) {
            long time2Mins = 2 * 60 * 1000;
            postDate = Calendar.getInstance().getTimeInMillis() + time2Mins;
        }

        return postDate;
    }

    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}
