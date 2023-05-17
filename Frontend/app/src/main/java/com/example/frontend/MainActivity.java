package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mPreferences;
    private String loginUsername = "";
    private String loginPassword = "";
    private boolean isLogin = false;
    final private String TESTSTRING1 = "username";
    final private String TESTSTRING2 = "password";
    final private String LOGINSTATUS = "loginstatus";
    private String sharedPrefFile = "com.example.frontend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        isLogin = mPreferences.getBoolean(LOGINSTATUS, isLogin);
        if (isLogin == true) {
            mPreferences.getString(TESTSTRING1, loginUsername);
            mPreferences.getString(TESTSTRING2, loginPassword);
            setContentView(R.layout.activity_main);
            Bundle bundle = new Bundle();
            bundle.putString("username", loginUsername);
            bundle.putString("password", loginPassword);
            bundle.putBoolean("isLogin", isLogin);
//            Intent intent = new Intent(this, activity_homepage.class);
//            intent.putExtras(bundle);
//            startActivity(intent);
        } else {
            setContentView(R.layout.activity_main);
        }
    }
    public void gotoRegister(View v) {
        Intent intent = new Intent(this, activity_register.class);
        startActivity(intent);
    }
    public void gotoLogin(View v) {
        Intent intent = new Intent(this, activity_login.class);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        preferencesEditor.apply();
    }
    public void jumpToHomePage(View v) {
//        isLogin = true;
//        Bundle bundle = new Bundle();
//        bundle.putString("username", username);
//        bundle.putString("password", password);
//        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
//        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        Intent intent = new Intent(this, activity_homepage.class);
//        intent.putExtras(bundle);
        startActivity(intent);
    }
}