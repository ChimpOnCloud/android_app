package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.Guard;

public class activity_userinfo extends AppCompatActivity {
    user mUser;
    TextView usrnameContent;
    TextView passwdContent;
    TextView nicknameContent;
    TextView introductionContent;
    ImageButton subButton;
    Button checkSubButton;
    ImageView userIcon;
    Boolean subscribe;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        mUser= (user) getIntent().getSerializableExtra("user");
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        if(mUser==null){
            mUser=activity_homepage.User;
            Log.d("a",activity_homepage.User.getUsername());
        }

        Log.d("a",mUser.getUsername());
        // todo: create userIcon with image
        usrnameContent = findViewById(R.id.textView_usrname_content);
        passwdContent = findViewById(R.id.textView_password_content);
        nicknameContent = findViewById(R.id.textView_nickname_content);
        introductionContent = findViewById(R.id.textView_introduction_content);
        checkSubButton=findViewById(R.id.checkSubscribed);
        userIcon=findViewById(R.id.userIcon);
        subButton=findViewById(R.id.subscribe);

        usrnameContent.setText(mUser.getUsername());
        if(mUser.getUsername().equals(activity_homepage.User.getUsername())) passwdContent.setText("Password: "+mUser.getPassword());
        else passwdContent.setText("");
        nicknameContent.setText(mUser.getNickname());
        introductionContent.setText(mUser.getIntroduction());
        // todo: get subscribe info and change the button icon
        subscribe=false;
        subButton.setOnClickListener(view -> {
            subscribe=!subscribe;
            subButtonSetIcon();
            // todo: post the data to backend
        });
        subButtonSetIcon();
    }
    public void jumpToHomePage(View v) {
        Intent intent = new Intent(this, activity_homepage.class);
        startActivity(intent);
    }
    public void jumpToInfoEditPage(View v) {
        Intent intent = new Intent(this, activity_editinfo.class);
        startActivity(intent);
    }

    public void subButtonSetIcon(){
        if(subscribe) subButton.setImageDrawable(getResources().getDrawable(android.R.drawable.star_big_on));
        else subButton.setImageDrawable(getResources().getDrawable(android.R.drawable.star_big_off));
    }
    public void changeUsername(View v) {
    }
    
    public void checkSubscribed(View v){
        Intent intent=new Intent(this,activity_subscribelist.class);
        intent.putExtra("user",mUser);
        startActivity(intent);
    }
}