package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ramiz on 2/19/18.
 */

public class RepeatAdjustByTimeSelectionView extends LinearLayoutCompat implements AdapterView.OnItemSelectedListener {
    ImageView iconImageView;
    TextView titleTextView;
    AppCompatSpinner timeRangeOptionsSpinner;
    int[] timeRangeOptions;
    int[] timeRangeValues;

    private int adjustByTime;
    @Nullable
    private OnRepeatAdjustByTimeChangeListener onRepeatAdjustByTimeChangeListener;

    public RepeatAdjustByTimeSelectionView(Context context) {
        super(context);
        init();
    }

    public RepeatAdjustByTimeSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepeatAdjustByTimeSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.repeat_adjust_by_time_selection_view, this, true);
        iconImageView = findViewById(R.id.imageView_icon);
        titleTextView = findViewById(R.id.textView_title);
        timeRangeOptionsSpinner = findViewById(R.id.repeat_adjust_by_time_spinner);
        timeRangeOptions = getContext().getResources().getIntArray(R.array.time_range);
        timeRangeValues = getContext().getResources().getIntArray(R.array.time_range_values);

        timeRangeOptionsSpinner.setOnItemSelectedListener(this);
        adjustByTime = timeRangeOptions[0];
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        adjustByTime = timeRangeValues[position];
        notifyListener();
    }

    private void notifyListener() {
        if (onRepeatAdjustByTimeChangeListener != null) {
            onRepeatAdjustByTimeChangeListener.onAdjustByTimeChange(this, adjustByTime);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int getAdjustByTime() {
        return adjustByTime;
    }

    public void setAdjustByTime(int adjustByTime) {
        timeRangeOptionsSpinner.setSelection(findIndex(adjustByTime));
    }

    private int findIndex(int adjustByTime) {
        for (int i = 0; i < timeRangeValues.length; i++) {
            if (adjustByTime == timeRangeValues[i]) {
                return i;
            }
        }

        return -1;
    }

    @Nullable
    public OnRepeatAdjustByTimeChangeListener getOnRepeatAdjustByTimeChangeListener() {
        return onRepeatAdjustByTimeChangeListener;
    }

    public void setOnRepeatAdjustByTimeChangeListener(@Nullable OnRepeatAdjustByTimeChangeListener onRepeatAdjustByTimeChangeListener) {
        this.onRepeatAdjustByTimeChangeListener = onRepeatAdjustByTimeChangeListener;
    }

    public interface OnRepeatAdjustByTimeChangeListener {
        void onAdjustByTimeChange(RepeatAdjustByTimeSelectionView view, int adjustByTime);
    }
}
