package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class activity_homepage extends AppCompatActivity {
    String username = "";
    String password = "";
    private String TESTSTRING1 = "username";
    private String TESTSTRING2 = "password";
    private String loginUsername = "";
    private String loginPassword = "";
    private String LOGINSTATUS = "loginstatus";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    boolean isLogin = false; // if True, restore the previous login status.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        setContentView(R.layout.activity_homepage);
        Bundle bundle = this.getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        isLogin = bundle.getBoolean("isLogin");
        System.out.println(isLogin);
    }
    public void jumpToUserInfo(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        Intent intent = new Intent(this, activity_userinfo.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void logout(View v) {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, false); // login status should be false
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(TESTSTRING1, loginUsername);
        preferencesEditor.putString(TESTSTRING2, loginPassword);
        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        preferencesEditor.apply();
    }
}