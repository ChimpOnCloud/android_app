package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private BottomNavigationView bottomNavigationView;
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

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = new BlankFragment();
                        break;
                    case R.id.navigation_topic:
                        selectedFragment = new BlankFragment();
                        break;
                    case R.id.navigation_guide: {
                        jumpToChat();
                        return true;
                    }
                    case R.id.navigation_me: {
                        jumpToUserInfo(); // 调用跳转到用户信息界面的方法
                        return true; // 注意要在此处返回 true，表示已处理点击事件
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
    }

    public void jumpToUserInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        Intent intent = new Intent(this, activity_userinfo.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void jumpToChat(){
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        Intent intent = new Intent(this, activity_chat.class);
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