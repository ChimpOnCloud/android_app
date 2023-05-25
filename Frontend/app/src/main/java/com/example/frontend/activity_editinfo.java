package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class activity_editinfo extends AppCompatActivity {
    String username = "";
    String password = "";
    String nickname = "";
    String introduction = "";
    EditText usrnameEditText;
    EditText passwdEditText;
    EditText nicknameEditText;
    EditText introEditText;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editinfo);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        usrnameEditText = findViewById(R.id.textView_usrname_content);
        passwdEditText = findViewById(R.id.textView_password_content);
        nicknameEditText = findViewById(R.id.textView_nickname_content);
        introEditText = findViewById(R.id.textView_introduction_content);
        username = mPreferences.getString("username", username);
        password = mPreferences.getString("password", password);
        nickname = mPreferences.getString("nickname", nickname);
        introduction = mPreferences.getString("introduction", introduction);
        usrnameEditText.setText(username);
        passwdEditText.setText(password);
        nicknameEditText.setText(nickname);
        introEditText.setText(introduction);

    }
    public void saveInfo(View v) {
        String newUsername = usrnameEditText.getText().toString();
        String newPassword = usrnameEditText.getText().toString();
        String newNickname = usrnameEditText.getText().toString();
        String newIntroduction = usrnameEditText.getText().toString();
        // TODO: 传数据给后端，改变用户信息！

    }
}