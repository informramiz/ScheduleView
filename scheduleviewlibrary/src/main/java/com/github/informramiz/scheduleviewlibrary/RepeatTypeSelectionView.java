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

public class RepeatTypeSelectionView extends LinearLayoutCompat implements AdapterView.OnItemSelectedListener {
    ImageView iconImageView;
    TextView titleTextView;
    AppCompatSpinner repeatTypeOptionsSpinner;
    String[] repeatTypeOptions;
    String[] repeatTypeOptionsValues;

    private String repeatType;
    @Nullable
    private OnRepeatTypeSelectionListener onRepeatTypeSelectionListener;

    public RepeatTypeSelectionView(Context context) {
        super(context);
        init();
    }

    public RepeatTypeSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepeatTypeSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.repeat_type_selection_view, this, true);
        iconImageView = findViewById(R.id.imageView_icon);
        titleTextView = findViewById(R.id.textView_title);
        repeatTypeOptionsSpinner = findViewById(R.id.repeat_type_options_spinner);

        repeatTypeOptions = getContext().getResources().getStringArray(R.array.repeat_options);
        repeatTypeOptionsValues = getContext().getResources().getStringArray(R.array.repeat_options_values);
        repeatTypeOptionsSpinner.setOnItemSelectedListener(this);
        repeatType = repeatTypeOptionsValues[0];
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        repeatType = repeatTypeOptionsValues[position];
        if (onRepeatTypeSelectionListener != null) {
            onRepeatTypeSelectionListener.onRepeatTypeSelected(repeatType);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        repeatTypeOptionsSpinner.setSelection(findIndex(repeatType));
    }

    private int findIndex(String repeatTypeKey) {
        for (int i = 0; i < repeatTypeOptionsValues.length; i++) {
            if (repeatTypeOptionsValues[i].equalsIgnoreCase(repeatTypeKey)) {
                return i;
            }
        }

        return -1;
    }

    @Nullable
    public OnRepeatTypeSelectionListener getOnRepeatTypeSelectionListener() {
        return onRepeatTypeSelectionListener;
    }

    public void setOnRepeatTypeSelectionListener(@Nullable OnRepeatTypeSelectionListener onRepeatTypeSelectionListener) {
        this.onRepeatTypeSelectionListener = onRepeatTypeSelectionListener;
    }

    public interface OnRepeatTypeSelectionListener {
        void onRepeatTypeSelected(String repeatType);
    }
}
