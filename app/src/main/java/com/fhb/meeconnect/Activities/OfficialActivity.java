package com.fhb.meeconnect.Activities;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.fhb.meeconnect.Fragmnets.PeopleFragment;
import com.fhb.meeconnect.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;

public class OfficialActivity extends AppCompatActivity {


    public static String TAG = "OfficialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        BottomBar bottomBar = findViewById(R.id.bottom_navigation);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title").setMessage("Hi there");

        AlertDialog alert = builder.create();
        alert.show();


        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId)
                {
                    case R.id.tab_teachers:
                        openFragment(PeopleFragment.newInstance("Teacher","0"));
                        break;


                    case R.id.tab_staffs:
                        openFragment(PeopleFragment.newInstance("Staff",""));
                        break;
                }
            }
        });


        BottomBarTab nearby = bottomBar.getTabWithId(R.id.tab_teachers);
        nearby.setBadgeCount(5);



        openFragment(PeopleFragment.newInstance("Teacher",""));






    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    @Override
    public void onBackPressed() {

        Intent intent = new Intent(OfficialActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_to_right_exit, R.anim.left_to_right);
    }
}