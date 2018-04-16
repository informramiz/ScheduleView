package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.informramiz.daypickerlibrary.views.DayPickerDialog;
import com.github.informramiz.daypickerlibrary.views.DayPickerView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ramiz on 2/19/18.
 */

public class WeekDaySelectionView extends LinearLayoutCompat {
    TextView selectRepeatDaysTextView;
    String[] weekDayNames;
    @NonNull
    boolean selectedDays[] = new boolean[DayPickerView.TOTAL_DAYS];

    @Nullable
    private OnWeekDayChangeListener onWeekDayChangeListener;

    public WeekDaySelectionView(Context context) {
        super(context);
        init();
    }

    public WeekDaySelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeekDaySelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.week_day_selection_view, this, true);
        selectRepeatDaysTextView = findViewById(R.id.textView_select_repeat_days);
        weekDayNames = getContext().getResources().getStringArray(R.array.week_days);
        selectRepeatDaysTextView.setPaintFlags(selectRepeatDaysTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        selectedDays[PostDateUtils.getCurrentDay() - 1] = true;

        registerListeners();
        updateSelectedDaysText();
    }

    private void registerListeners() {
        selectRepeatDaysTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDayPicker();
            }
        });
    }

    private void showDayPicker() {
        DayPickerDialog.Builder builder = new DayPickerDialog.Builder(getContext())
                .setInitialSelectedDays(selectedDays)
                .setMultiSelectionAllowed(true)
                .setOnDaysSelectedListener(new DayPickerDialog.OnDaysSelectedListener() {
                    @Override
                    public void onDaysSelected(DayPickerView dayPickerView, boolean[] selectedDays) {
                        WeekDaySelectionView.this.selectedDays = selectedDays;
                        updateSelectedDaysText();
                        notifyListener();
                    }
                });

        builder.show();
    }

    private void updateSelectedDaysText() {
        selectRepeatDaysTextView.setText(getSelectedDaysText());
    }

    private String getDayName(int index) {
        return weekDayNames[index];
    }

    private String getSelectedDaysText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < selectedDays.length; i++) {
            if (!selectedDays[i]) {
                continue;
            }

            if (i > 0 && stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(getDayName(i));
        }
        return stringBuilder.toString();
    }
    
    public ArrayList<String> getSelectedDaysNames() {
        ArrayList<String> selectedDaysNames = new ArrayList<>();
        for (int i = 0; i < selectedDays.length; i++) {
            if (!selectedDays[i]) {
                continue;
            }

            selectedDaysNames.add(weekDayNames[i]);
        }
        return selectedDaysNames;
    }

    public ArrayList<Integer> getSelectDaysInCalendarFormat() {
        ArrayList<Integer> selectedDaysInCalendarFormat = new ArrayList<>();
        for (int i = 0; i < selectedDays.length; i++) {
            if (!selectedDays[i]) {
                continue;
            }

            //calendar days
            selectedDaysInCalendarFormat.add(i + 1);
        }

        return selectedDaysInCalendarFormat;
    }

    public void setSelectedDays(@NonNull ArrayList<String> dayNames) {
        //first let's reset the current array
        Arrays.fill(selectedDays, false);

        if (!dayNames.isEmpty()) {
            for (String dayName : dayNames) {
                int index = getDayIndex(dayName);
                selectedDays[index] = true;
            }
        } else {
            //select the current day as default
            selectedDays[PostDateUtils.getCurrentDay() - 1] = true;
        }
        updateSelectedDaysText();
    }

    private int getDayIndex(String dayName) {
        for (int i = 0; i < weekDayNames.length; i++) {
            if (weekDayNames[i].equalsIgnoreCase(dayName)) {
                return i;
            }
        }

        return -1;
    }

    private void notifyListener() {
        if (onWeekDayChangeListener != null) {
            onWeekDayChangeListener.onWeekDayChange(this, getSelectedDaysNames());
        }
    }

    @Nullable
    public OnWeekDayChangeListener getOnWeekDayChangeListener() {
        return onWeekDayChangeListener;
    }

    public void setOnWeekDayChangeListener(@Nullable OnWeekDayChangeListener onWeekDayChangeListener) {
        this.onWeekDayChangeListener = onWeekDayChangeListener;
    }

    public interface OnWeekDayChangeListener {
        public void onWeekDayChange(WeekDaySelectionView view, ArrayList<String> selectedDays);
    }
}
