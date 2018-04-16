package com.github.informramiz.scheduleviewlibrary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ramiz on 2/21/18.
 */

public class TimeCardAdapter extends RecyclerView.Adapter<TimeCardAdapter.TimeCardHolder> {

    ArrayList<String> timeCardList;
    private Context context;
    private LayoutInflater inflater;

    public TimeCardAdapter(ArrayList<String> timeCardList, Context context) {
        this.timeCardList = timeCardList;
        this.context = context;
    }

    @Override
    public TimeCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_card_item, parent, false);
        TimeCardHolder holder = new TimeCardHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TimeCardHolder holder, int position) {
        if(holder.getAdapterPosition() == 0) {
            holder.icTimeCard.setImageResource(R.drawable.ic_schedule);
        } else {
            holder.icTimeCard.setImageResource(R.drawable.ic_frequency);
        }
        holder.textData.setText(timeCardList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return timeCardList.size();
    }


    class TimeCardHolder extends RecyclerView.ViewHolder {

        TextView textData;
        ImageView icTimeCard;

        public TimeCardHolder(View itemView) {
            super(itemView);

            textData = (TextView) itemView.findViewById(R.id.tv_card_time);
            icTimeCard = (ImageView) itemView.findViewById(R.id.ic_time_card);

        }
    }
}
