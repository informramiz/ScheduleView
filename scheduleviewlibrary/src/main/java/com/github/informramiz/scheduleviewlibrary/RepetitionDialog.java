package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ramiz on 2/21/18.
 */

public class RepetitionDialog extends AlertDialog.Builder {

    private AlertDialog mAlertDialog;


    private Context mContext;

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    private int repetitions;


    public RepetitionDialog(Context context) {
        super(context, R.style.RepeatDialog);
        mContext = context;

    }

    @Override
    public AlertDialog show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_repetition, null);

        final EditText edtRepetitions = (EditText) view.findViewById(R.id.edt_repetitions);

        setNegativeButton(mContext.getResources().getString(R.string.cancel), null);
        setPositiveButton(mContext.getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    repetitions = Integer.valueOf(edtRepetitions.getText().toString());
                    if (repetitions < 2) {
                        Toast.makeText(mContext, R.string.the_minimum_is_two_times, Toast.LENGTH_SHORT).show();
                        repetitions = 2;
                    }else if (repetitions >= 1000){
                        repetitions = 999;
                        Toast.makeText(mContext, R.string.maximum_repeat, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    repetitions = 2;
                }
            }
        });

        setTitle(mContext.getResources().getString(R.string.expires_on));
        setView(view);

        mAlertDialog = super.show();
        mAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return mAlertDialog;
    }
}
