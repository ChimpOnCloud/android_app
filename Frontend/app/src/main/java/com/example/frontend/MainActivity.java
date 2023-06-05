package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.text.TextRunShaper;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private String loginUsername = "";
    private String loginPassword = "";
    private boolean isLogin = false;
    final private String TESTSTRING1 = "username";
    final private String TESTSTRING2 = "password";
    final private String LOGINSTATUS = "loginstatus";
    private String sharedPrefFile = "com.example.frontend";
    private TextView welcomeText;
    private TextView pLogin;
    private TextView pRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        isLogin = mPreferences.getBoolean(LOGINSTATUS, isLogin);
        setContentView(R.layout.activity_main);
        if (isLogin == true) {
            mPreferences.getString(TESTSTRING1, loginUsername);
            mPreferences.getString(TESTSTRING2, loginPassword);
            Intent intent = new Intent(this, activity_homepage.class);
            startActivity(intent);
        }
        welcomeText=findViewById(R.id.welcome);
        welcomeText.setText("Welcome to "+getString(R.string.project_name)+"!");
        pLogin=findViewById(R.id.promptLogin);
        pRegister=findViewById(R.id.promptRegister);
        String textL="登录";
        String textR="注册";
        SpannableString spannableStringL=new SpannableString(textL);
        SpannableString spannableStringR=new SpannableString(textR);
        spannableStringL.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(MainActivity.this, activity_login.class);
                startActivity(intent);
            }
        },0,textL.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pLogin.setText(spannableStringL);
        spannableStringR.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(MainActivity.this, activity_register.class);
                startActivity(intent);
            }
        },0,textR.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pRegister.setText(spannableStringR);
        pLogin.setMovementMethod(LinkMovementMethod.getInstance());
        pRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        preferencesEditor.apply();
    }
}