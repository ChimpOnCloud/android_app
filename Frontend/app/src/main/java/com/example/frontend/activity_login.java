package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_login extends AppCompatActivity {
    String username;
    String password;
    EditText usernameText;
    EditText passwordText;
    private TextView reg;
    // TextView notificationText;
    private String loginUsername;
    private String loginPassword;
    private String TESTSTRING1 = "username";
    private String TESTSTRING2 = "password";
    private String LOGINSTATUS = "loginstatus";
    private boolean isLogin = false;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        // we can get status of loginUser from the following lines:
        System.out.println(mPreferences.getString(TESTSTRING1, loginUsername));
        System.out.println(mPreferences.getString(TESTSTRING2, loginPassword));
        setContentView(R.layout.activity_login);
        reg=findViewById(R.id.signUp);
        String textR="还没有账户？注册";
        SpannableString spannableStringR=new SpannableString(textR);
        spannableStringR.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(activity_login.this, activity_register.class);
                startActivity(intent);
            }
        },6,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reg.setText(spannableStringR);
        reg.setMovementMethod(LinkMovementMethod.getInstance());
    }
    public void ReturnToMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void UserLogin(View v) {
        usernameText = findViewById(R.id.usrname);
        passwordText = findViewById(R.id.passwd);
        // notificationText = findViewById(R.id.textView_notification);
        // get the usrname and passwd
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        if(username.isEmpty()||password.isEmpty()){
            return;
        }
        String jsonStr = "{\"username\":\""+ username + "\",\"password\":\""+password+"\"}";
        String requestUrl = getString(R.string.ipv4)+"login/";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        @SuppressWarnings("deprecation")
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failed");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                String repeatString = "repeated!";
                if (msg_obj_string.equals("wrong password")) {
                    System.out.println("password not correct");
                    // notificationText.setText("repeated username. Please choose another one!");
                } else if (msg_obj_string.equals("ok")) {
                    System.out.println("succeeded");
                    loginUsername = username;
                    loginPassword = password;
                    jumpToHomePage(v);
                    // notificationText.setText("successfully registered a new account! Now you can login");
                }  else if (msg_obj_string.equals("not registered yet!")) {
                    System.out.println("please register first");
                }
            }
        });
    }
    public void jumpToHomePage(View v) {
        isLogin = true;
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        Intent intent = new Intent(this, activity_homepage.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(TESTSTRING1, loginUsername);
        preferencesEditor.putString(TESTSTRING2, loginPassword);
        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        preferencesEditor.apply();
    }
}