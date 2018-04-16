# ScheduleView
A schedule view with date, time, repeat type, repeat day, number of repetitions options. 

![normal-screenshot](demo/screenshot-normal.png)![normal-screenshot](demo/screenshot-day-selection.png)

## Getting Started

Setting info is very simple using the Builder class provided.

```
PostScheduleView postScheduleView = findViewById(R.id.post_schedule_view);

//initialize post schedule view with custom data
PostScheduleView.PostScheduleInfo postScheduleInfo = new PostScheduleView.PostScheduleInfo.Builder()
                .setRepeatType(Post.HOURLY)
                .setRepeatCount(2)
                .setRepeatForever(false)
                .setPostScheduleDate(Calendar.getInstance().getTimeInMillis())
                .build();
postScheduleView.setScheduleInfo(postScheduleInfo);
```

And same is for getting the schedule info.

```
//after user is done, get the info with a simple method call
PostScheduleView.PostScheduleInfo postScheduleInfo = postScheduleView.getScheduleInfo();
```