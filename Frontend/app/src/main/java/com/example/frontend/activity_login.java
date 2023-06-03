package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

import org.json.*;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class activity_login extends AppCompatActivity {
    String username;
    String password;
    String nickname;
    String introduction;
    EditText usernameText;
    EditText passwordText;
    Set<String> followers = new HashSet<>();
    private TextView reg;
    private TextView reg2;
    private String LOGINSTATUS = "loginstatus";
    private boolean isLogin = false;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        setContentView(R.layout.activity_login);
        reg = findViewById(R.id.signUp);
        String textR="还没有账户？注册";
        SpannableString spannableStringR = new SpannableString(textR);
        spannableStringR.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(activity_login.this, activity_register.class);
                startActivity(intent);
            }
        },6,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reg.setText(spannableStringR);
        reg.setMovementMethod(LinkMovementMethod.getInstance());

        reg2 = findViewById(R.id.backToStart);
        String textR2 = "返回首页";
        SpannableString spannableStringR2 = new SpannableString(textR2);
        spannableStringR2.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(activity_login.this, MainActivity.class);
                startActivity(intent);
            }
        },0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        reg2.setText(spannableStringR2);
        reg2.setMovementMethod(LinkMovementMethod.getInstance());
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
        String jsonStr = "{\"username\":\""+ username + "\",\"password\":\""+password+"\"";
        jsonStr = jsonStr + ",\"nickname\":\"" + nickname + "\",\"introduction\":\"" + introduction + "\"}";
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity_login.this);
                        builder.setTitle("Error");
                        builder.setMessage("无法连接至服务器。。或许网络出错了？");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                });
                // System.out.println("failed");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                // String repeatString = "repeated!";
                if (msg_obj_string.equals("wrong password")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder=new AlertDialog.Builder(activity_login.this);
                            builder.setTitle("Error");
                            builder.setMessage("输入的密码有误");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                        }
                    });
                    // System.out.println("password not correct");
                    // notificationText.setText("repeated username. Please choose another one!");
                } else if (msg_obj_string.equals("not registered yet!")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder=new AlertDialog.Builder(activity_login.this);
                            builder.setTitle("Info");
                            builder.setMessage("看起来您还没有注册，请先注册");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                        }
                    });
                    // System.out.println("please register first");
                } else {
                    System.out.println("succeeded");
                    System.out.println(msg_obj_string);
                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                    nickname = msg_json.getString("nickname");
                    introduction = msg_json.getString("introduction");
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
        preferencesEditor.putString("nickname", nickname);
        preferencesEditor.putString("introduction", introduction);
        preferencesEditor.apply();
        Intent intent = new Intent(this, activity_homepage.class);
        startActivity(intent);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        preferencesEditor.apply();
    }
}