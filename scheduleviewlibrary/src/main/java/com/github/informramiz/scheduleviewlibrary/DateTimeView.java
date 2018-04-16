package com.github.informramiz.scheduleviewlibrary;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by ramiz on 2/21/18.
 */

public class DateTimeView extends LinearLayoutCompat implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final int REQUIRED_MINS_IN_FUTURE = 2;

    private Calendar selectedTimeCalendar;

    @Nullable
    private OnDateTimeChangeListener onDateTimeChangeListener;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView timeTextView;

    public DateTimeView(Context context) {
        super(context);
        init();
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.date_time_view, this, true);

        titleTextView = findViewById(R.id.textView_title);
        dateTextView = findViewById(R.id.textView_date);
        timeTextView = findViewById(R.id.textView_time);
        //underline date and time text
        dateTextView.setPaintFlags(dateTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        timeTextView.setPaintFlags(dateTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        selectedTimeCalendar = getCurrentTimeCalendar();
        registerListeners();
        updateDateTimeText();
    }

    private void registerListeners() {
        dateTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startDateDialog();
            }
        });

        timeTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeDialog();
            }
        });
    }

    private void updateDateTimeText() {
        dateTextView.setText(PostDateUtils.formatDate(selectedTimeCalendar.getTimeInMillis()));
        timeTextView.setText(PostDateUtils.formatTime(selectedTimeCalendar.getTimeInMillis()));
    }

    public long getTimeInMillis() {
        return selectedTimeCalendar.getTimeInMillis();
    }

    public Calendar getSelectedTimeCalendar() {
        return selectedTimeCalendar;
    }

    public void setTimeInMillis(long timeInMillis) {
        selectedTimeCalendar.setTimeInMillis(timeInMillis);
        updateDateTimeText();
    }

    public void setDateToDay(int day) {
        if (day >= selectedTimeCalendar.get(Calendar.DAY_OF_WEEK)) {
            selectedTimeCalendar.set(Calendar.DAY_OF_WEEK, day);
        } else {
            PostDateUtils.moveCalendarToNextSunday(selectedTimeCalendar);
            selectedTimeCalendar.set(Calendar.DAY_OF_WEEK, day);
        }
        updateDateTimeText();
    }

    private void resetDayToToday() {
        selectedTimeCalendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    }

    public boolean validateDateTime() {
        if (isSelectedTimeValid(selectedTimeCalendar.getTimeInMillis())) {
            return true;
        } else {
            Toast.makeText(getContext(), R.string.wrong_schedule_date, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void startDateDialog() {
        Calendar calendar = selectedTimeCalendar;
        if (isDateInPast(calendar)) {
            calendar = getCurrentTimeCalendar();
        }
        // Use the current time as the default values for the picker
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of TimePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMinDate(getCurrentTimeCalendar().getTimeInMillis() - 3 * 60 * 1000);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        datePickerDialog.show();
    }

    private void startTimeDialog() {
        Calendar calendar = selectedTimeCalendar;
        if (isDateInPast(calendar)) {
            calendar = getCurrentTimeCalendar();
        }
        // Use the current time as the default values for the picker
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        new TimePickerDialog(getContext(), this, hour, minute,
                DateFormat.is24HourFormat(getContext())).show();
    }

    private boolean isDateInPast(Calendar calendar) {
        return !calendar.after(Calendar.getInstance());
    }

    private Calendar getCurrentTimeCalendar() {
        Calendar currentTimeCalendar = Calendar.getInstance();
        //add 2 mins in future to count for the time user will
        //take create the post
        currentTimeCalendar.add(Calendar.MINUTE, REQUIRED_MINS_IN_FUTURE);
        return currentTimeCalendar;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (isSelectedTimeValid(hourOfDay, minute)) {
            selectedTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedTimeCalendar.set(Calendar.MINUTE, minute);
            updateDateTimeText();
        } else {
            Toast.makeText(getContext(), R.string.wrong_schedule_date, Toast.LENGTH_SHORT).show();
        }

        notifyListener();
    }

    public void setDateTextViewState(boolean isEnabled) {
        dateTextView.setEnabled(isEnabled);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedTimeCalendar.set(Calendar.YEAR, year);
        selectedTimeCalendar.set(Calendar.MONTH, month);
        selectedTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateTimeText();

        notifyListener();
    }

    private boolean isSelectedTimeValid(int hourOfDay, int minute) {
        Calendar selectedTime = selectedTimeCalendar;
        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedTime.set(Calendar.MINUTE, minute);

        return selectedTime.after(Calendar.getInstance());
    }

    private boolean isSelectedTimeValid(long selectedTimeMillis) {
        Calendar selectedTime = Calendar.getInstance();
        selectedTime.setTimeInMillis(selectedTimeMillis);
        return selectedTime.after(Calendar.getInstance());
    }

    private void notifyListener() {
        if (onDateTimeChangeListener != null) {
            onDateTimeChangeListener.onDateTimeChange(this, selectedTimeCalendar.getTimeInMillis());
        }
    }

    @Nullable
    public OnDateTimeChangeListener getOnDateTimeChangeListener() {
        return onDateTimeChangeListener;
    }

    public void setOnDateTimeChangeListener(@Nullable OnDateTimeChangeListener onDateTimeChangeListener) {
        this.onDateTimeChangeListener = onDateTimeChangeListener;
    }

    public void setTitleText(@NonNull String titleText) {
        titleTextView.setText(titleText);
    }

    public interface OnDateTimeChangeListener {
        void onDateTimeChange(DateTimeView dateTimeView, long timeInMillis);
    }
}
