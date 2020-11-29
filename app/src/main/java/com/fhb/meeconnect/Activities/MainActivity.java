package com.fhb.meeconnect.Activities;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.fhb.meeconnect.AlarmReceiver;
import com.fhb.meeconnect.BuildConfig;
import com.fhb.meeconnect.DataElements.Category;
import com.fhb.meeconnect.DataElements.Faculty;
import com.fhb.meeconnect.R;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";


    public static ArrayList<Category> catagories;
    private DatabaseReference myRef;
    private Context context;
    private ImageView settings, search, cover_teacher, cover_student, cover_staff;
    private Toolbar toolbar;
    private CardView card;
    private CardView teachers_card;
    private CardView staffs_card;
    private CardView students_card;
    private ConstraintLayout constraintLayout;
    ArrayList<Faculty> teachers = new ArrayList<>();
    ArrayList<Faculty> staffs = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private Button testButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = findViewById(R.id.home_settings);
        search = findViewById(R.id.home_search);
        constraintLayout = findViewById(R.id.parent);
        refreshLayout = findViewById(R.id.rehreshome);
        testButton =  findViewById(R.id.testButton);


        
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OfficialActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right_exit, R.anim.left_to_right);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission( Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
            }
        }else{

            card = findViewById(R.id.home_card);
            toolbar = findViewById(R.id.toolbar);

            card.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);

            settings = findViewById(R.id.toolbar_settings);
            search = findViewById(R.id.toolbar_search);
        }

        context = this;

        cover_teacher =  findViewById(R.id.cover_teachers);
        cover_staff = findViewById(R.id.cover_staffs);
        cover_student = findViewById(R.id.cover_students);




        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Search.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_to_left, R.anim.right_to_left_exit);
            }
        });


        teachers_card = findViewById(R.id.catagory_teachers);
        staffs_card = findViewById(R.id.catagory_staffs);
        students_card =  findViewById(R.id.catagory_students);
        catagories = new ArrayList<>();


        StartAlarm();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        myRef.keepSynced(true);

        refreshLayout.setRefreshing(true);


        GetData();


        refreshLayout.setProgressViewOffset(false,
                150, 220);




        teachers_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PeopleList.class);
                intent.putExtra("Catagory Name", "Teacher");
                intent.putExtra("Catagory Index", 0);
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });



        staffs_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PeopleList.class);
                intent.putExtra("Catagory Name", "Staff");
                intent.putExtra("Catagory Index", 0);
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });


        students_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, Students.class);
                context.startActivity(intent);
                Activity activity = (Activity) context;
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                teachers.clear();
                staffs.clear();
                GetData();
            }
        });

    }


    public void GetData()
    {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String url1 = (String) dataSnapshot.child("Teacher").child("coverImage").getValue();
                Picasso.get()
                        .load(url1)
                        .fit()
                        .centerCrop()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(cover_teacher,new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(url1)
                                        .fit()
                                        .centerCrop()
                                        .into(cover_teacher);
                            }
                        });


                for (DataSnapshot faculty : dataSnapshot.child("Teacher").getChildren()) {
                    String id = faculty.getKey();
                    String bloodGroup = (String) faculty.child("Blood").getValue();
                    String birthday = (String) faculty.child("DOB").getValue();
                    String designation = (String) faculty.child("Designation").getValue();
                    String email = (String) faculty.child("Email").getValue();
                    String messengerID = (String) faculty.child("MSG_Id").getValue();
                    String name = (String) faculty.child("Name").getValue();
                    String phone = (String) faculty.child("Phone").getValue();
                    String photoURL = (String) faculty.child("Photo").getValue();

                    Faculty member = new Faculty(id,
                            bloodGroup,
                            birthday,
                            designation,
                            email,
                            messengerID,
                            name,
                            phone,
                            photoURL);
                    teachers.add(member);
                }

                final String url2 = (String) dataSnapshot.child("Staff").child("coverImage").getValue();
                Picasso.get()
                        .load(url2)
                        .fit()
                        .centerCrop()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(cover_staff,new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(url2)
                                        .fit()
                                        .centerCrop()
                                        .into(cover_staff);
                            }
                        });


                for (DataSnapshot faculty : dataSnapshot.child("Staff").getChildren()) {
                    String id = faculty.getKey();
                    String bloodGroup = (String) faculty.child("Blood").getValue();
                    String birthday = (String) faculty.child("DOB").getValue();
                    String designation = (String) faculty.child("Designation").getValue();
                    String email = (String) faculty.child("Email").getValue();
                    String messengerID = (String) faculty.child("MSG_Id").getValue();
                    String name = (String) faculty.child("Name").getValue();
                    String phone = (String) faculty.child("Phone").getValue();
                    String photoURL = (String) faculty.child("Photo").getValue();

                    Faculty member = new Faculty(id,
                            bloodGroup,
                            birthday,
                            designation,
                            email,
                            messengerID,
                            name,
                            phone,
                            photoURL);
                    staffs.add(member);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toasty.error(getApplicationContext(),"Can't load data. Please check your internet connection.", Toasty.LENGTH_LONG).show();

            }
        });


        DatabaseReference mref = FirebaseDatabase.getInstance().getReference();
        mref.keepSynced(true);

        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String RemoteVersion = (String) dataSnapshot.child("Version").getValue();
                assert RemoteVersion != null;
                float LatestVersion = Float.parseFloat(RemoteVersion);

                String versionName = BuildConfig.VERSION_NAME;
                float ThisVersion = Float.parseFloat(versionName);
                if (LatestVersion > ThisVersion) {
                    final String url5 = (String) dataSnapshot.child("DownloadLink").getValue();
                    Vibrator vibrator =  (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);

                    Snackbar snackbar = Snackbar.make(constraintLayout,"Updates Available!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Download", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url5));
                                    startActivity(intent);

                                }
                            }).setActionTextColor(Color.GREEN);


                    snackbar.show();

                }
                else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String url3 = (String) dataSnapshot.child("StudentImg").getValue();
                Picasso.get()
                        .load(url3)
                        .fit()
                        .centerCrop()
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(cover_student,new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(url3)
                                        .fit()
                                        .centerCrop()
                                        .into(cover_student);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        refreshLayout.setRefreshing(false);
    }


    public void StartAlarm() {
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
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }


    }



}
