package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ramiz on 2/19/18.
 */

public class RepeatCountView extends LinearLayoutCompat implements AdapterView.OnItemSelectedListener {
    public static final int REPEAT_FOREVER_VALUE = -1;

    ImageView iconImageView;
    TextView titleTextView;
    AppCompatSpinner repeatCountSpinner;
    String[] repeatOptionsArray;
    int[] repeatOptionsValues;
    private String customOptionText;

    private RepetitionDialog customRepetitionDialog;
    private ArrayAdapter<String> repeatCountSpinnerAdapter;
    private ArrayList<String> repeatOptionsList = new ArrayList<>();
    private int repeatCount = 2;

    @Nullable
    private OnRepeatCountChangeListener onRepeatCountChangeListener;

    public RepeatCountView(Context context) {
        super(context);
        init();
    }

    public RepeatCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepeatCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.repeat_count_view, this, true);

        iconImageView = findViewById(R.id.imageView_icon);
        titleTextView = findViewById(R.id.textView_title);
        repeatCountSpinner = findViewById(R.id.total_repeat_count_spinner);
        repeatOptionsArray = getContext().getResources().getStringArray(R.array.repeat_count_options);
        repeatOptionsValues = getContext().getResources().getIntArray(R.array.repeat_count_options_values);

        customOptionText = repeatOptionsArray[repeatOptionsArray.length - 1];
        repeatOptionsList.addAll(Arrays.asList(repeatOptionsArray));
        repeatCountSpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, R.id.spinner_item_text, repeatOptionsList);
        repeatCountSpinner.setAdapter(repeatCountSpinnerAdapter);

        customRepetitionDialog = new RepetitionDialog(getContext());
        registerListeners();
    }

    private void registerListeners() {
        repeatCountSpinner.setOnItemSelectedListener(this);
        customRepetitionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                repeatCount = customRepetitionDialog.getRepetitions() < 2 ? 2 : customRepetitionDialog.getRepetitions();
                selectRepeatCountOption(repeatCount);
                notifyListener();
            }
        });
    }

    private void selectRepeatCountOption(int repeatCount) {
        //user has entered value between 2-4 which is already an option so just select from
        //existing option
        if (repeatCount <= repeatOptionsValues[repeatOptionsValues.length - 1]) {
            repeatCountSpinner.setSelection(findRepeatOptionValueIndex(repeatCount));
        } else {
            //user has entered a custom repeat value greater than existing maximum option
            //present so add it as a new option
            addCustomSelectedRepetitionCountToSpinner(repeatCount);
        }
    }

    private int findRepeatOptionValueIndex(int repeatValue) {
        for (int i = 0; i < repeatOptionsValues.length; i++) {
            if (repeatOptionsValues[i] == repeatValue) {
                return i;
            }
        }

        return -1;
    }

    private void addCustomSelectedRepetitionCountToSpinner(int customRepeatCount) {
        //we only keep one custom repeat option in spinner, so if there is already a custom option
        //then remove that and add this new one
        if (repeatOptionsList.size() > repeatOptionsArray.length) {
            repeatOptionsList.remove(repeatOptionsList.size() - 1);
        }

        String optionText = customOptionText + "(" + String.valueOf(customRepeatCount) + ")";
        repeatOptionsList.add(optionText);
        repeatCountSpinnerAdapter.notifyDataSetChanged();
        repeatCountSpinner.setSelection(repeatOptionsList.size() - 1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position < repeatOptionsValues.length) {
            //get the relevant integer value for the selection option
            repeatCount = repeatOptionsValues[position];
            notifyListener();
        } else if (position == repeatOptionsValues.length){
            //user selected custom repetition type
            customRepetitionDialog.show();
        }
    }

    private void notifyListener() {
        if (onRepeatCountChangeListener != null) {
            onRepeatCountChangeListener.onRepeatCountChange(this, repeatCount);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        selectRepeatCountOption(repeatCount);
    }

    public void setRepeatForeverValue(boolean isTrue) {
        if (isTrue) {
            setRepeatCount(REPEAT_FOREVER_VALUE);
        }
    }

    public boolean isRepeatForever() {
        return this.repeatCount == REPEAT_FOREVER_VALUE;
    }

    @Nullable
    public OnRepeatCountChangeListener getOnRepeatCountChangeListener() {
        return onRepeatCountChangeListener;
    }

    public void setOnRepeatCountChangeListener(@Nullable OnRepeatCountChangeListener onRepeatCountChangeListener) {
        this.onRepeatCountChangeListener = onRepeatCountChangeListener;
    }

    public interface OnRepeatCountChangeListener {
        void onRepeatCountChange(RepeatCountView repeatCountView, int newRepeatCount);
    }
}
