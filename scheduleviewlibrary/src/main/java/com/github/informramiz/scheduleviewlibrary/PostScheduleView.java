package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ramiz on 2/21/18.
 */

public class PostScheduleView extends LinearLayoutCompat implements DateTimeView.OnDateTimeChangeListener, RepeatSelectionView.OnRepeatInfoChangeListener {
    RepeatSelectionView repeatSelectionView;
    DateTimeView dateTimeView;
    private PostScheduleInfo postScheduleInfo;

    public PostScheduleView(Context context) {
        super(context);
        init();
    }

    public PostScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PostScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.post_schedule_view, this, true);
        repeatSelectionView = findViewById(R.id.repeat_selection_view);
        dateTimeView = findViewById(R.id.date_time_view);
        findViewById(R.id.textView_view_summary).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showScheduleSummary();
            }
        });
        dateTimeView.setOnDateTimeChangeListener(this);
        repeatSelectionView.setOnRepeatInfoChangeListener(this);

        postScheduleInfo = new PostScheduleInfo();
        postScheduleInfo.timeInMillis = dateTimeView.getTimeInMillis();
        postScheduleInfo.repeatInfo = repeatSelectionView.getRepeatInfo();
    }

    private void showScheduleSummary() {
        RepeatSelectionView.RepeatInfo repeatInfo = repeatSelectionView.getRepeatInfo();
        long startTimeInMillis = postScheduleInfo.timeInMillis;
        String adjustByTimeText = null;
        if (repeatInfo.adjustByTime > 0) {
            adjustByTimeText = getContext().getString(R.string.adjust_by_time_text_for_display, repeatInfo.adjustByTime);
        }

        ArrayList<String> timeCardList = PostDateUtils.getPostSchedulesSummary(0,
                startTimeInMillis,
                repeatInfo.repeatCount,
                repeatInfo.repeatType,
                repeatInfo.isRepeatForever,
                adjustByTimeText,
                repeatInfo.weekDays);
        TimeCardDialog timeCardDialog = new TimeCardDialog(getContext(), timeCardList);
        timeCardDialog.show();
    }

    public void setScheduleInfo(@NonNull PostScheduleInfo postScheduleInfo) {
        this.postScheduleInfo.repeatInfo = postScheduleInfo.repeatInfo;
        repeatSelectionView.setRepeatInfo(postScheduleInfo.repeatInfo);
        dateTimeView.setTimeInMillis(postScheduleInfo.timeInMillis);
    }

    public PostScheduleInfo getScheduleInfo() {
        PostScheduleInfo postScheduleInfo = new PostScheduleInfo();
        postScheduleInfo.repeatInfo = repeatSelectionView.getRepeatInfo();
        postScheduleInfo.timeInMillis = dateTimeView.getTimeInMillis();

        return postScheduleInfo;
    }

    @Override
    public void onDateTimeChange(DateTimeView dateTimeView, long timeInMillis) {
        postScheduleInfo.timeInMillis = timeInMillis;
    }

    @Override
    public void onRepeatInfoChanged(RepeatSelectionView repeatSelectionView, RepeatSelectionView.RepeatInfo repeatInfo) {
        if (!StringUtils.areEqual(repeatInfo.weekDays, postScheduleInfo.repeatInfo.weekDays)
                || postScheduleInfo.repeatInfo.repeatCount != repeatInfo.repeatCount) {
            //repeat count is changed so update it
            updateRepeatCountView(repeatInfo);
        }
        postScheduleInfo.repeatInfo = repeatInfo;
    }

    private void updateDateTimeView(RepeatSelectionView.RepeatInfo newRepeatInfo,
                                    RepeatSelectionView.RepeatInfo oldRepeatInfo) {
        if (newRepeatInfo.repeatType.equalsIgnoreCase(Post.NOT_REPEAT)) {
            dateTimeView.setTitleText(getContext().getString(R.string.schedule));
        } else {
            dateTimeView.setTitleText(getContext().getString(R.string.schedule_start));
        }

        if (newRepeatInfo.repeatType.equalsIgnoreCase(Post.WEEKLY)
                && !newRepeatInfo.weekDaysInCalendarFormat.isEmpty()) {
            if (oldRepeatInfo.repeatType.equalsIgnoreCase(Post.WEEKLY)) {
                dateTimeView.setDateToDay(
                        PostDateUtils.getFirstFutureDayDate(postScheduleInfo.timeInMillis,
                                newRepeatInfo.weekDaysInCalendarFormat)
                );
            }
        }
    }

    private void updateRepeatCountView(RepeatSelectionView.RepeatInfo repeatInfo) {
        if (repeatInfo.repeatType.equalsIgnoreCase(Post.WEEKLY)) {
            int newRepeatCount = repeatInfo.weekDays.size();
            String startDateDay = PostDateUtils.getCalendarDayName(postScheduleInfo.timeInMillis);
            if (!repeatInfo.weekDays.contains(startDateDay)) {
                //we need to count start date as well
                newRepeatCount += 1;
            }
            if (repeatInfo.repeatCount != RepeatSelectionView.RepeatInfo.REPEAT_FOREVER
                    && newRepeatCount > repeatInfo.repeatCount)
                repeatSelectionView.setRepeatCount(newRepeatCount);
        }
    }

    public boolean validateData() {
        return dateTimeView.validateDateTime();
    }

    public static class PostScheduleInfo {
        private RepeatSelectionView.RepeatInfo repeatInfo ;
        private long timeInMillis;

        PostScheduleInfo() {
            repeatInfo = new RepeatSelectionView.RepeatInfo();
            timeInMillis = Calendar.getInstance().getTimeInMillis();
        }
        public PostScheduleInfo(Builder builder) {
            this.repeatInfo = builder.repeatInfo;
            timeInMillis = builder.timeInMillis;
        }

        public @Post.PostRepeatType
        String getRepeatType() {
            return this.repeatInfo.repeatType;
        }

        public int getRepeatCount() {
            return repeatInfo.repeatCount;
        }

        public boolean isRepeatForever() {
            return repeatInfo.isRepeatForever;
        }

        public int getAdjustByTime() {
            return this.repeatInfo.adjustByTime;
        }

        public ArrayList<String> getWeekDays() {
            return this.repeatInfo.weekDays;
        }

        public ArrayList<Integer> getWeekDaysInCalendarFormat() {
            return this.repeatInfo.weekDaysInCalendarFormat;
        }

        public long getPostScheduleDate() {
            return this.timeInMillis;
        }

        public static class Builder {
            RepeatSelectionView.RepeatInfo repeatInfo;
            long timeInMillis;

            public Builder() {
                repeatInfo = new RepeatSelectionView.RepeatInfo();
                timeInMillis = Calendar.getInstance().getTimeInMillis();
            }

            public Builder setRepeatType(@Post.PostRepeatType String repeatType) {
                this.repeatInfo.repeatType = repeatType != null ? repeatType : Post.NOT_REPEAT;
                return this;
            }

            public Builder setRepeatCount(@Nullable Integer repeatCount) {
                if (repeatCount != null) {
                    this.repeatInfo.repeatCount = repeatCount;
                }
                return this;
            }

            public Builder setRepeatForever(boolean isRepeatForever) {
                this.repeatInfo.isRepeatForever = isRepeatForever;
                return this;
            }

            public Builder setAdjustByTime(@Nullable Integer adjustByTime) {
                if (adjustByTime != null) {
                    this.repeatInfo.adjustByTime = adjustByTime;
                }
                return this;
            }

            public Builder setWeekDays(ArrayList<String> weekDays) {
                this.repeatInfo.weekDays = weekDays;
                return this;
            }

            public Builder setWeekDaysInCalendarFormat(ArrayList<Integer> weekDaysInCalendarFormat) {
                this.repeatInfo.weekDaysInCalendarFormat = weekDaysInCalendarFormat;
                return this;
            }

            public Builder setPostScheduleDate(long timeInMillis) {
                this.timeInMillis = timeInMillis;
                return this;
            }

            public PostScheduleInfo build() {
                return new PostScheduleInfo(this);
            }
        }
    }
}
