package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class activity_userinfo extends AppCompatActivity {
    String username = "";
    String password = "";
    String nickname = "";
    String introduction = "";
    TextView usrnameContent;
    TextView passwdContent;
    TextView nicknameContent;
    TextView introductionContent;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        username = mPreferences.getString("username", username);
        System.out.println("new page:" + username);
        password = mPreferences.getString("password", password);
        nickname = mPreferences.getString("nickname", nickname);
        introduction = mPreferences.getString("introduction", introduction);
        usrnameContent = findViewById(R.id.textView_usrname_content);
        nicknameContent = findViewById(R.id.textView_nickname_content);
        introductionContent = findViewById(R.id.textView_nickname_content);
        passwdContent = findViewById(R.id.textView_password_content);
        nicknameContent = findViewById(R.id.textView_nickname_content);
        introductionContent = findViewById(R.id.textView_introduction_content);
        usrnameContent.setText(username);
        passwdContent.setText(password);
        nicknameContent.setText(nickname);
        introductionContent.setText(introduction);
    }
    public void jumpToHomePage(View v) {
        Intent intent = new Intent(this, activity_homepage.class);
        startActivity(intent);
    }
    public void jumpToInfoEditPage(View v) {
        Intent intent = new Intent(this, activity_editinfo.class);
        startActivity(intent);
    }
    public void changeUsername(View v) {

    }
}