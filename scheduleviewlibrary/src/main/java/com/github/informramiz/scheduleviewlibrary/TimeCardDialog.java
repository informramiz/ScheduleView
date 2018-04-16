package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ramiz on 2/21/18.
 */

public class TimeCardDialog extends AlertDialog.Builder {
    private AlertDialog mAlertDialog;
    private Context mContext;
    private RecyclerView rvTimeCard;
    private RecyclerView.LayoutManager layoutManager;
    private TimeCardAdapter mAdapter;
    private ArrayList<String> mTimeCardList;


    public ArrayList<String> getTimeCardList() {
        return mTimeCardList;
    }

    public void setTimeCardList(ArrayList<String> mTimeCardList) {
        this.mTimeCardList = mTimeCardList;
    }

    public TimeCardDialog(Context context, ArrayList<String> timeCardList) {
        super(context, R.style.RepeatDialog);
        mContext = context;
        mTimeCardList = timeCardList;
    }

    @Override
    public AlertDialog show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_time_cards, null);
        rvTimeCard = (RecyclerView) view.findViewById(R.id.rv_card_time);
        layoutManager = new LinearLayoutManager(mContext);
        rvTimeCard.setLayoutManager(layoutManager);
        mAdapter = new TimeCardAdapter(mTimeCardList,mContext);
        rvTimeCard.setAdapter(mAdapter);
        setPositiveButton(mContext.getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        setTitle(mContext.getResources().getString(R.string.time_card));
        setView(view);
        mAlertDialog = super.show();
        mAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return mAlertDialog;
    }
}
