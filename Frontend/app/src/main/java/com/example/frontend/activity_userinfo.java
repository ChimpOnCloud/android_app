package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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
    TextView shieldText;
    Boolean shielded;
    Button changeInfoButton;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    private Boolean isCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        isCurrentUser=false;
        mUser= (user) getIntent().getSerializableExtra("user");
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        if(mUser==null){ // default user is the user currently using the app
            mUser=activity_homepage.User;
            isCurrentUser=true;
        }

        // todo: create userIcon with image
        usrnameContent = findViewById(R.id.textView_usrname_content);
        passwdContent = findViewById(R.id.textView_password_content);
        nicknameContent = findViewById(R.id.textView_nickname_content);
        introductionContent = findViewById(R.id.textView_introduction_content);
        checkSubButton=findViewById(R.id.checkSubscribed);
        userIcon=findViewById(R.id.userIcon);
        subButton=findViewById(R.id.subscribe);
        shieldText=findViewById(R.id.shield);
        changeInfoButton=findViewById(R.id.changeInfo);

        usrnameContent.setText(mUser.getUsername());
        if(isCurrentUser) passwdContent.setText("Password: "+mUser.getPassword());
        else passwdContent.setText("");
        nicknameContent.setText(mUser.getNickname());
        introductionContent.setText(mUser.getIntroduction());
        // todo: get subscribe info and change the button icon
        subscribe=false;
        if(!isCurrentUser) {
            subButton.setOnClickListener(view -> {
                subscribe = !subscribe;
                subButtonSetIcon();
                // todo: post the data to backend
            });
            subButtonSetIcon();
        }
        else{
            subButton.setEnabled(false);
        }
        // todo: get the shield pattern here
        shielded=false;
        if(!isCurrentUser) {
            shieldTextSet();
        }
        else{
            shieldText.setText("");
            shieldText.setEnabled(false);
        }
        if(!isCurrentUser){
            changeInfoButton.setEnabled(false);
            changeInfoButton.setVisibility(View.INVISIBLE);
        }
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

    public void shieldTextSet(){
        String sText;
        SpannableString spannableString;
        if(shielded) sText="已屏蔽";
        else sText="屏蔽";
        spannableString =new SpannableString(sText);
        spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    shielded=!shielded;
                    // todo: post the data to backend
                    shieldTextSet();
                }
            },0,sText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(shielded) spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")),0,sText.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        shieldText.setText(spannableString);
        shieldText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void changeUsername(View v) {
    }
    
    public void checkSubscribed(View v){
        Intent intent=new Intent(this,activity_subscribelist.class);
        intent.putExtra("user",mUser);
        startActivity(intent);
    }
}