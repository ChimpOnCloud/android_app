package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        Bundle bundle = this.getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        nickname = bundle.getString("nickname");
        introduction = bundle.getString("introduction");

        usrnameContent = findViewById(R.id.textView_usrname_content);
        nicknameContent = findViewById(R.id.textView_nickname_content);
        introductionContent = findViewById(R.id.textView_nickname_content);
        passwdContent = findViewById(R.id.textView_password_content);

        usrnameContent.setText(username);
        passwdContent.setText(password);
        nicknameContent.setText(nickname);
        introductionContent.setText(introduction);
    }
    public void jumpToHomePage(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        Intent intent = new Intent(this, activity_homepage.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void changeUsername(View v) {

    }
}