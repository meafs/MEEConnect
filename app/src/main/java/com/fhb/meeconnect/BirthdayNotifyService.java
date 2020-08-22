package com.fhb.meeconnect;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fhb.meeconnect.Activities.Birthday;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BirthdayNotifyService extends IntentService {

    private DatabaseReference myRef;
    private ArrayList<String> allPeoples;
    private int numberOfPeoples;

    public BirthdayNotifyService() {
        super("BirthdayNotifyService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String CHANNEL_ID = "Foreground_service";
            String CHANNEL_NAME = "Foreground Service";

            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);

            final Notification noti = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_notify)
                    .setAutoCancel(true)
                    .build();
            manager.notify(2021, noti);
            startForeground(1, noti);


            initPeoples();

            StartAlarm();

            stopForeground(true);

        }

        else {

            initPeoples();

            StartAlarm();

        }

        stopSelf();


    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("d MMMM");
        String strDate = mdformat.format(calendar.getTime());

        return strDate;
    }


    private void initPeoples() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        myRef.keepSynced(true);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                allPeoples = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("Teacher") || snapshot.getKey().equals("Staff")) {
                        for (DataSnapshot faculty : snapshot.child("peoples").getChildren()) {
                            String birthday = (String) faculty.child("DOB").getValue();

                            if (birthday.equals(getCurrentDate())) {
                                String member = birthday;
                                allPeoples.add(member);
                            }
                        }

                    } else {

                        for (DataSnapshot student : snapshot.child("peoples").getChildren()) {

                            String birthday = (String) student.child("DOB").getValue();

                            if (birthday.equals(getCurrentDate())) {
                                String member = birthday;
                                allPeoples.add(member);
                            }
                        }
                    }
                }

                numberOfPeoples = allPeoples.size();

                if(numberOfPeoples>0){

                    ShowNotification();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void ShowNotification()
    {
        String CHANNEL_ID = "Channel_1453";
        String CHANNEL_NAME = "Birthday Notification";

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle("Birthday Reminder")
                .setColor(Color.GREEN)
                .setContentText("Click to see who has birthday today!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(),0, new Intent(getApplicationContext(), Birthday.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .build();

        manager.notify(454, notification);
    }


    public void StartAlarm() {

        long day = 86400000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);


        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);


        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        //alarmIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 156, alarmIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + day, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + day, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + day, pendingIntent);
        }

    }

}