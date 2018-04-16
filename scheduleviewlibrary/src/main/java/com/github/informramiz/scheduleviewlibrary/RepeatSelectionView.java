package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import java.util.ArrayList;

/**
 * Created by ramiz on 2/18/18.
 */

public class RepeatSelectionView extends LinearLayoutCompat {
    RepeatTypeSelectionView repeatTypeSelectionView;
    WeekDaySelectionView weekDaySelectionView;
    RepeatCountView repeatCountView;
    RepeatAdjustByTimeSelectionView adjustByTimeSelectionView;

    @Nullable
    private OnRepeatInfoChangeListener onRepeatInfoChangeListener;

    public RepeatSelectionView(Context context) {
        super(context);
        init();
    }

    public RepeatSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepeatSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.repeat_selection_view, this, true);
        repeatTypeSelectionView = findViewById(R.id.repeat_type_selection_view);
        weekDaySelectionView = findViewById(R.id.week_day_selection_view);
        repeatCountView = findViewById(R.id.repeat_count_view);
        adjustByTimeSelectionView = findViewById(R.id.repeat_adjust_by_time_view);

        //not used at the moment
        adjustByTimeSelectionView.setVisibility(GONE);
        repeatTypeSelectionView.setOnRepeatTypeSelectionListener(new RepeatTypeSelectionView.OnRepeatTypeSelectionListener() {
            @Override
            public void onRepeatTypeSelected(String repeatType) {
                updateViewsVisibility(repeatType);
                notifyListener();
            }
        });

        repeatCountView.setOnRepeatCountChangeListener(new RepeatCountView.OnRepeatCountChangeListener() {
            @Override
            public void onRepeatCountChange(RepeatCountView repeatCountView, int newRepeatCount) {
                notifyListener();
            }
        });

        adjustByTimeSelectionView.setOnRepeatAdjustByTimeChangeListener(new RepeatAdjustByTimeSelectionView.OnRepeatAdjustByTimeChangeListener() {
            @Override
            public void onAdjustByTimeChange(RepeatAdjustByTimeSelectionView view, int adjustByTime) {
                notifyListener();
            }
        });

        weekDaySelectionView.setOnWeekDayChangeListener(new WeekDaySelectionView.OnWeekDayChangeListener() {
            @Override
            public void onWeekDayChange(WeekDaySelectionView view, ArrayList<String> selectedDays) {
                notifyListener();
            }
        });
    }

    public void setRepeatCount(int repeatCount) {
        repeatCountView.setRepeatCount(repeatCount);
    }

    public boolean isRepeatForever() {
        return repeatCountView.isRepeatForever();
    }

    private void notifyListener() {
        if (onRepeatInfoChangeListener != null) {
            onRepeatInfoChangeListener.onRepeatInfoChanged(RepeatSelectionView.this, getRepeatInfo());
        }
    }

    private void updateViewsVisibility(String repeatType) {
        if (repeatType.equalsIgnoreCase(Post.NOT_REPEAT)) {
            weekDaySelectionView.setVisibility(GONE);
            repeatCountView.setVisibility(GONE);
//            adjustByTimeSelectionView.setVisibility(GONE);
        } else {
            repeatCountView.setVisibility(VISIBLE);
//            adjustByTimeSelectionView.setVisibility(VISIBLE);
        }

        if (repeatType.equalsIgnoreCase(Post.WEEKLY)) {
            weekDaySelectionView.setVisibility(VISIBLE);
        } else {
            weekDaySelectionView.setVisibility(GONE);
        }
    }

    public void setRepeatInfo(RepeatInfo repeatInfo) {
        if (!repeatInfo.isRepeatForever && repeatInfo.repeatCount <= 1) {
            repeatInfo.repeatType = Post.NOT_REPEAT;
        }
        repeatTypeSelectionView.setRepeatType(repeatInfo.repeatType);
        weekDaySelectionView.setSelectedDays(repeatInfo.weekDays);
        repeatCountView.setRepeatCount(repeatInfo.repeatCount);
        repeatCountView.setRepeatForeverValue(repeatInfo.isRepeatForever);
        adjustByTimeSelectionView.setAdjustByTime(repeatInfo.adjustByTime);
        updateViewsVisibility(repeatInfo.repeatType);
    }

    public RepeatInfo getRepeatInfo() {
        RepeatInfo.Builder repeatInfoBuilder = new RepeatInfo.Builder(repeatTypeSelectionView.getRepeatType());
        if (repeatInfoBuilder.repeatType.equalsIgnoreCase(Post.NOT_REPEAT)) {
            return repeatInfoBuilder.build();
        }

        repeatInfoBuilder
                .setRepeatCount(repeatCountView.getRepeatCount())
                .setRepeatForever(repeatCountView.isRepeatForever())
                .setAdjustByTime(adjustByTimeSelectionView.getAdjustByTime());

        if (repeatInfoBuilder.repeatType.equalsIgnoreCase(Post.WEEKLY)) {
            repeatInfoBuilder.setWeekDays(weekDaySelectionView.getSelectedDaysNames());
            repeatInfoBuilder.setWeekDaysInCalendarFormat(weekDaySelectionView.getSelectDaysInCalendarFormat());
        }

        return repeatInfoBuilder.build();
    }

    @Nullable
    public OnRepeatInfoChangeListener getOnRepeatInfoChangeListener() {
        return onRepeatInfoChangeListener;
    }

    public void setOnRepeatInfoChangeListener(@Nullable OnRepeatInfoChangeListener onRepeatInfoChangeListener) {
        this.onRepeatInfoChangeListener = onRepeatInfoChangeListener;
    }

    public static class RepeatInfo {
        public static final int REPEAT_FOREVER = RepeatCountView.REPEAT_FOREVER_VALUE;
        @Post.PostRepeatType
        public String repeatType;
        public int repeatCount;
        public boolean isRepeatForever = false;
        public int adjustByTime;
        public ArrayList<String> weekDays;
        ArrayList<Integer> weekDaysInCalendarFormat = new ArrayList<>();

        public RepeatInfo() {
            repeatType = Post.NOT_REPEAT;
            repeatCount = 1;
            adjustByTime = 0;
            weekDays = new ArrayList<>();
        }

        public RepeatInfo(Builder builder) {
            repeatType = builder.repeatType;
            repeatCount = builder.repeatCount;
            isRepeatForever = builder.isRepeatForever;
            adjustByTime = builder.adjustByTime;
            weekDays = builder.weekDays;
            weekDaysInCalendarFormat = builder.weekDaysInCalendarFormat;
        }

        public static class Builder {
            String repeatType;
            int repeatCount = 1;
            public boolean isRepeatForever = false;
            int adjustByTime = 0;
            ArrayList<String> weekDays = new ArrayList<>();
            ArrayList<Integer> weekDaysInCalendarFormat = new ArrayList<>();

            public Builder(@Nullable String repeatType) {
                this.repeatType = repeatType != null ? repeatType : Post.NOT_REPEAT;
            }

            public Builder setRepeatCount(@Nullable Integer repeatCount) {
                if (repeatCount != null) {
                    this.repeatCount = repeatCount;
                }
                return this;
            }

            public Builder setRepeatForever(boolean isRepeatForever) {
                this.isRepeatForever = isRepeatForever;
                return this;
            }

            public Builder setAdjustByTime(@Nullable Integer adjustByTime) {
                if (adjustByTime != null) {
                    this.adjustByTime = adjustByTime;
                }
                return this;
            }

            public Builder setWeekDays(ArrayList<String> weekDays) {
                this.weekDays = weekDays;
                return this;
            }

            public Builder setWeekDaysInCalendarFormat(ArrayList<Integer> weekDaysInCalendarFormat) {
                this.weekDaysInCalendarFormat = weekDaysInCalendarFormat;
                return this;
            }

            public RepeatInfo build() {
                return new RepeatInfo(this);
            }
        }
    }

    public interface OnRepeatInfoChangeListener {
        void onRepeatInfoChanged(RepeatSelectionView repeatSelectionView, RepeatInfo repeatInfo);
    }
}
