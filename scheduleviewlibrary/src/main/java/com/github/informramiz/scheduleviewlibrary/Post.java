package com.github.informramiz.scheduleviewlibrary;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ramiz on 2/21/18.
 */
public class Post {
    public static final String HOURLY = "HOURLY";
    public static final String DAILY = "DAILY";
    public static final String WEEKLY = "WEEKLY";
    public static final String MONTHLY = "MONTHLY";
    public static final String YEARLY = "YEARLY";
    public static final String CUSTOM_BY_DAY = "CUSTOM_BY_DAY";
    public static final String NOT_REPEAT = "NOT_REPEAT";
    @Retention(RetentionPolicy.SOURCE)
    @StringDef(value = {HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM_BY_DAY, NOT_REPEAT})
    public @interface PostRepeatType{}

    public static final int MAX_REPEAT_COUNT_FOR_SUMMARY = 4;
    private static final int DEFAULT_REPEAT_COUNT_FOR_NEVER_ENDING_POST = 2;
}
