package com.fhb.meeconnect.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fhb.meeconnect.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class SearchDetails extends AppCompatActivity {

    private ImageView cover;
    private TextView name, nickname, regNo, bloodGroup, birthday, number;
    private Button messenger, phone;

    private String NAME, NICKNAME, REG, BLOOD, BIRTHDAY, MESSENGER, PHONE, PHOTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        cover = findViewById(R.id.search_detail_photo);
        name = findViewById(R.id.search_detail_name);
        nickname = findViewById(R.id.search_detail_nickname);
        regNo = findViewById(R.id.search_deatil_reg);
        bloodGroup = findViewById(R.id.search_detail_blood_group);
        birthday = findViewById(R.id.search_detail_birthday);
        messenger = findViewById(R.id.search_detail_messenger);
        phone = findViewById(R.id.search_detail_phone);
        number = findViewById(R.id.search_detail_number);

        getDataFromIntent();

        setData();
    }

    public void getDataFromIntent(){
        NAME = getIntent().getStringExtra("name");
        NICKNAME = getIntent().getStringExtra("nickname");
        REG = getIntent().getStringExtra("reg");
        BLOOD = getIntent().getStringExtra("blood");
        BIRTHDAY = getIntent().getStringExtra("birthday");
        MESSENGER = getIntent().getStringExtra("messenger");
        PHONE = getIntent().getStringExtra("phone");
        PHOTO  = getIntent().getStringExtra("photo");
    }

    public void setData(){
        name.setText(NAME.trim());
        nickname.setText(NICKNAME.trim());
        regNo.setText(REG.trim());

        String PhoneNumber = " " + PHONE.trim();
        number.setText(PhoneNumber);
        if(BLOOD.equals("Choose Blood Group...")){
            LinearLayout layout = findViewById(R.id.search_detail_blood_group_container);
            layout.setVisibility(View.GONE);
        }else{
            bloodGroup.setText(BLOOD.trim());
        }

        if(BIRTHDAY.equals("Day Month")){
            LinearLayout layout = findViewById(R.id.search_detail_birthday_container);
            layout.setVisibility(View.GONE);
        }else{
            String birthDate = BIRTHDAY.substring(0,2).trim();
            String birthMonth = BIRTHDAY.substring(2).trim().toUpperCase().substring(0,3);
            birthday.setText(birthDate+"\n"+birthMonth);
        }

        Picasso.get()
                .load(PHOTO)
                .placeholder(R.color.colorAccent)
                .fit()
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(cover, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(PHOTO)
                                .placeholder(R.color.colorAccent)
                                .fit()
                                .centerCrop()
                                .into(cover);
                    }
                });

        if(MESSENGER.trim().equals("NA")){
            LinearLayout layout = findViewById(R.id.search_detail_messenger_container);
            layout.setVisibility(View.GONE);
        }else{
            messenger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(MESSENGER.trim()));
                    if(MESSENGER.trim().toLowerCase().startsWith("http")) {
                        startActivity(intent);
                    }else{
                        startActivity(Intent.createChooser(intent, "Choose browser"));
                    }
                }
            });

            messenger.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                    ClipData clip = ClipData.newPlainText("ID Copied", MESSENGER);

                    assert clipboard != null;
                    clipboard.setPrimaryClip(clip);

                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(400);

                    Toasty.success(getApplicationContext(), "Link copied to clipboard!", Toast.LENGTH_SHORT, true).show();
                    return true;
                }
            });
        }

        phone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                    }
                }
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+PHONE.trim()));





                startActivity(intent);
            }
        });



        phone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("Copied phone number !!", PHONE);

                assert clipboard != null;
                clipboard.setPrimaryClip(clip);

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);

                Toasty.success(getApplicationContext(), "Number copied to clipboard!", Toast.LENGTH_SHORT, true).show();


                return true;
            }
        });
    }
}
