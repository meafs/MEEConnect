package com.fhb.meeconnect.Activities;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.fhb.meeconnect.Adapters.StudentRecyclerAdapter;
import com.fhb.meeconnect.DataElements.Catagory;
import com.fhb.meeconnect.DataElements.Student;
import com.fhb.meeconnect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


public class Students extends AppCompatActivity {

        private ImageView backButton;
        private ImageView search;
        private RecyclerView recyclerView;
        private SwipeRefreshLayout refreshLayout;
        private DatabaseReference myRef;
        public static ArrayList<Catagory> catagories;
        private LinearLayoutManager layoutManager;
        private Context context;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_students);

            context = this;

            backButton = findViewById(R.id.students_back);
            search = findViewById(R.id.students_layout_search);
            recyclerView = findViewById(R.id.students_recycler);
            refreshLayout = findViewById(R.id.students_refresh);
            refreshLayout.setRefreshing(true);

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            catagories = new ArrayList<>();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Users");
            myRef.keepSynced(true);

            getDataAndSetAdapter();

            refreshLayout.setProgressViewOffset(false,
                    150, 220);

            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    catagories.clear();
                    getDataAndSetAdapter();
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Students.this, Search.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_to_left, R.anim.right_to_left_exit);

                }
            });

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

        }

    private void getDataAndSetAdapter() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot catagory : dataSnapshot.getChildren()){
                    String catagoryName = catagory.getKey();
                    String coverImageURL = (String) catagory.child("coverImage").getValue();
                    ArrayList<Student> students = null;


                    if(Objects.equals(catagory.getKey(), "Teacher") ||
                            Objects.equals(catagory.getKey(), "Staff")){
                    }else {




                        students = new ArrayList<>();

                        for (DataSnapshot student : catagory.child("peoples").getChildren()) {
                            String registrationNo = student.getKey();
                            String bloodGroup = (String) student.child("Blood").getValue();
                            String birthday = (String) student.child("DOB").getValue();
                            String nickname = (String) student.child("Nickname").getValue();
                            String messengerID = (String) student.child("MSG_Id").getValue();
                            String name = (String) student.child("Name").getValue();
                            String phone = (String) student.child("Phone").getValue();
                            String photoURL = (String) student.child("Photo").getValue();

                            Student member = new Student(registrationNo,
                                    bloodGroup,
                                    birthday,
                                    messengerID,
                                    name,
                                    nickname,
                                    phone,
                                    photoURL);
                            students.add(member);
                        }
                    }

                    Catagory userCatagory = new Catagory(catagoryName, coverImageURL, students);


                    catagories.add(userCatagory);

                }

                Collections.reverse(catagories);
                catagories.remove(1);
                catagories.remove(0);

                StudentRecyclerAdapter adapter = new StudentRecyclerAdapter(context, catagories);
                recyclerView.setAdapter(adapter);
                refreshLayout.setRefreshing(false);
            }



            @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                Toasty.error(getApplicationContext(),
                        "Can't load data. Please check your internet connection.",
                        Toasty.LENGTH_LONG).show();

                }
            });
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


