package com.fhb.meeconnect.Fragmnets;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fhb.meeconnect.Activities.MainActivity;
import com.fhb.meeconnect.Adapters.PeopleRecyclerAdapter;
import com.fhb.meeconnect.DataElements.Catagory;
import com.fhb.meeconnect.DataElements.Faculty;
import com.fhb.meeconnect.DataElements.Student;
import com.fhb.meeconnect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class PeopleFragment extends Fragment {

    private TextView title;
    private ImageView backButton;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private String catagoryName;
    public static ArrayList<Student> students;
    public static ArrayList<Faculty> faculties;
    public ArrayList<Catagory> catagories;
    private int catagoryIndex;
    private DatabaseReference myRef;
    private Context ctx;
    private LinearLayoutManager layoutManager;

    public static int listPosition;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PeopleFragment() {
        // Required empty public constructor
    }
    public static PeopleFragment newInstance(String param1, String param2) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container,false);


        listPosition = 1;


        students = null;
        faculties = null;
        ctx = view.getContext();


        recyclerView = view.findViewById(R.id.people_fragment_recycler);
        refreshLayout = view.findViewById(R.id.people_fragment_refresh);

        layoutManager = new LinearLayoutManager(ctx);

        recyclerView.setLayoutManager(layoutManager);

        catagoryName = mParam1;
        catagoryIndex = 0;


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users").child(catagoryName).child("peoples");
        myRef.keepSynced(true);

        refreshPeoples();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPeoples();
            }
        });






        // Inflate the layout for this fragment
        return view;
    }


    private void refreshPeoples() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                refreshLayout.setRefreshing(true);
                if(catagoryName.equals("Teacher") || catagoryName.equals("Staff")){
                    ArrayList<Faculty> tempFaculty = new ArrayList<>();
                    for(DataSnapshot faculty : dataSnapshot.getChildren()){
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
                        tempFaculty.add(member);
                    }
                    PeopleRecyclerAdapter adapter = new PeopleRecyclerAdapter(catagoryIndex, catagoryName, null, tempFaculty, ctx);
                    recyclerView.setAdapter(adapter);


                    InitItemTouch(adapter,"Teacher");




                }else{
                    ArrayList<Student> tempStudent = new ArrayList<>();
                    for(DataSnapshot student : dataSnapshot.getChildren()){
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
                        tempStudent.add(member);
                    }



                    PeopleRecyclerAdapter adapter = new PeopleRecyclerAdapter(catagoryIndex, catagoryName, tempStudent, null, ctx);
                    recyclerView.setAdapter(adapter);


                }


                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void InitItemTouch(PeopleRecyclerAdapter adapter, String TeacherOrStudent)
    {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();



                if(direction == ItemTouchHelper.LEFT)
                {
                    adapter.SwipeToCall(TeacherOrStudent, position);
                    recyclerView.setAdapter(adapter);

                }

                else if(direction == ItemTouchHelper.RIGHT)
                {
                    adapter.SwipeToMessage(TeacherOrStudent, position);
                    recyclerView.setAdapter(adapter);
                }




                // Take action for the swiped item
                Toast.makeText(ctx, "Hi", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {




                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(ctx,R.color.green))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(ctx,R.color.red))
                        .addActionIcon(R.drawable.ic_android_black_24dp)
                        .create()
                        .decorate();





                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);



            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



}