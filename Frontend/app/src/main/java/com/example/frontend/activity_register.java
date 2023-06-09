package com.example.frontend;

import static com.example.frontend.Utils.BuildDialogUtil.buildDialog;

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

import com.example.frontend.Utils.LoadingDialogUtil;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_register extends AppCompatActivity {
    String username;
    String password;
    EditText usernameText;
    EditText passwordText;
    EditText passwordAgain;
    private TextView login;
    private TextView backToStart;
    private String LOGINSTATUS = "loginstatus";
    private boolean isLogin = false;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameText = findViewById(R.id.usrname);
        passwordText = findViewById(R.id.passwd);
        passwordAgain = findViewById(R.id.passwdagain);
        login = findViewById(R.id.signIn);
        String textL="已有账户？登录";
        SpannableString spannableStringL = new SpannableString(textL);
        spannableStringL.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(activity_register.this, activity_login.class);
                startActivity(intent);
            }
        },5,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(spannableStringL);
        login.setMovementMethod(LinkMovementMethod.getInstance());

        backToStart = findViewById(R.id.backToStart);
        String textBackToStart = "返回首页";
        SpannableString spannableStringL2 = new SpannableString(textBackToStart);
        spannableStringL2.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(activity_register.this, MainActivity.class);
                startActivity(intent);
            }
        },0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        backToStart.setText(spannableStringL2);
        backToStart.setMovementMethod(LinkMovementMethod.getInstance());
        mPreferences=getSharedPreferences(sharedPrefFile,MODE_PRIVATE);
    }
    public void RegisterAccount(View v) {
        // get the usrname and passwd
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        if(!password.equals(passwordAgain.getText().toString())){
            buildDialog("Error","两次输入的密码不一致",activity_register.this);
            return;
        }
        if(username.isEmpty()||password.isEmpty()){
            buildDialog("Info","请输入用户名和密码",activity_register.this);
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingDialogUtil.getInstance(activity_register.this).showLoadingDialog("Loading...");
            }
        });
        String jsonStr = "{\"username\":\""+ username + "\",\"password\":\""+password+"\"}";
        String requestUrl = getString(R.string.ipv4)+"register/";
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
                LoadingDialogUtil.getInstance(activity_register.this).closeLoadingDialog();
                buildDialog("Error","无法连接至服务器。。或许网络出错了？致",activity_register.this);
                // System.out.println("register failed");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                LoadingDialogUtil.getInstance(activity_register.this).closeLoadingDialog();
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                String repeatString = "repeated!";
                if (msg_obj_string.equals(repeatString)) {
                    buildDialog("Error","该用户名已经被占用，换一个吧",activity_register.this);
                    // System.out.println("repeated");
                } else if (msg_obj_string.equals("succeeded")) {
                    System.out.println("succeeded");
                    jumpToHomePage(v);
                }
            }
        });
    }
    public void jumpToHomePage(View v) {
        isLogin = true;
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        preferencesEditor.putString("username", username);
        preferencesEditor.putString("password", password);
        user mUser=new user();
        preferencesEditor.putString("nickname", mUser.getNickname());
        preferencesEditor.putString("introduction", mUser.getIntroduction());
        preferencesEditor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}