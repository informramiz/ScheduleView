package com.github.informramiz.scheduleview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.github.informramiz.scheduleviewlibrary.Post;
import com.github.informramiz.scheduleviewlibrary.PostDateUtils;
import com.github.informramiz.scheduleviewlibrary.PostScheduleView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PostScheduleView postScheduleView = findViewById(R.id.post_schedule_view);
        //initialize post schedule view with custom data
        PostScheduleView.PostScheduleInfo postScheduleInfo = new PostScheduleView.PostScheduleInfo.Builder()
                .setRepeatType(Post.HOURLY)
                .setRepeatCount(2)
                .setRepeatForever(false)
                .setPostScheduleDate(Calendar.getInstance().getTimeInMillis())
                .build();
        postScheduleView.setScheduleInfo(postScheduleInfo);

        //after user is done, get the info with a simple method call
//        PostScheduleView.PostScheduleInfo postScheduleInfo = postScheduleView.getScheduleInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
