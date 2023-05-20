package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
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
    // TextView notificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameText = findViewById(R.id.usrname);
        passwordText = findViewById(R.id.passwd);
        passwordAgain=findViewById(R.id.passwdagain);
        login=findViewById(R.id.signIn);
        String textL="已有账户？登录";
        SpannableString spannableStringL=new SpannableString(textL);
        spannableStringL.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(activity_register.this, activity_login.class);
                startActivity(intent);
            }
        },5,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(spannableStringL);
        login.setMovementMethod(LinkMovementMethod.getInstance());
    }
    public void RegisterAccount(View v) {
        // notificationText = findViewById(R.id.textView_notification);
        // get the usrname and passwd
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        if(!password.equals(passwordAgain.getText().toString())){
            // todo: tell the user the password entered is wrong
            return;
        }
        if(username.isEmpty()||password.isEmpty()){
            return;
        }
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
                if (msg_obj_string.equals(repeatString)) {
                    System.out.println("repeated");
                    // notificationText.setText("repeated username. Please choose another one!");
                } else if (msg_obj_string.equals("succeeded")) {
                    System.out.println("succeeded");
                    jumpToHomePage(v);
                    // notificationText.setText("successfully registered a new account! Now you can login");
                }
            }
        });
    }
    public void jumpToHomePage(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}