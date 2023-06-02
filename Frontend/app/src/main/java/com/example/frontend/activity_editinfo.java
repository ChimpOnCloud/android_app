package com.example.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_editinfo extends AppCompatActivity {
    String oldUsername = "";
    String oldPassword = "";
    String oldNickname = "";
    String oldIntroduction = "";
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
        oldUsername = mPreferences.getString("username", oldUsername);
        oldPassword = mPreferences.getString("password", oldPassword);
        oldNickname = mPreferences.getString("nickname", oldNickname);
        oldIntroduction = mPreferences.getString("introduction", oldIntroduction);

        usrnameEditText = findViewById(R.id.textView_usrname_content);
        passwdEditText = findViewById(R.id.textView_password_content);
        nicknameEditText = findViewById(R.id.textView_nickname_content);
        introEditText = findViewById(R.id.textView_introduction_content);
        usrnameEditText.setText(oldUsername);
        passwdEditText.setText(oldPassword);
        nicknameEditText.setText(oldNickname);
        introEditText.setText(oldIntroduction);

    }
    public void saveInfo(View v) {
        String newUsername = usrnameEditText.getText().toString();
        String newPassword = passwdEditText.getText().toString();
        String newNickname = nicknameEditText.getText().toString();
        String newIntroduction = introEditText.getText().toString();
        // TODO: 传数据给后端，改变用户信息
        String jsonStr = "{\"newUsername\":\""+ newUsername + "\",\"newPassword\":\""+newPassword+"\"";
        jsonStr = jsonStr + ",\"newNickname\":\"" + newNickname + "\",\"newIntroduction\":\"" + newIntroduction + "\"";
        jsonStr = jsonStr + ",\"oldUsername\":\"" + oldUsername + "\",\"oldPassword\":\"" + oldPassword + "\"";
        jsonStr = jsonStr + ",\"oldNickname\":\"" + oldNickname + "\",\"oldIntroduction\":\"" + oldIntroduction + "\"}";
        System.out.println(jsonStr);
        String requestUrl = getString(R.string.ipv4)+"changeUserinfo/";
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
                // e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString(); // ok
                if (msg_obj_string.equals("ok")) {
                    System.out.println("successfully changed userinfo");
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                    preferencesEditor.putString("username", newUsername);
                    preferencesEditor.putString("password", newPassword);
                    preferencesEditor.putString("nickname", newNickname);
                    preferencesEditor.putString("introduction", newIntroduction);
                    preferencesEditor.apply();
                } else if (msg_obj_string.equals("repeated username!")) {
                    System.out.println("already have this username!");
                }
            }
        });
        // change mpreferences
        System.out.println("shit" + mPreferences.getString("username", newUsername));
        Intent intent = new Intent(this, activity_homepage.class);
        startActivity(intent);
    }
}