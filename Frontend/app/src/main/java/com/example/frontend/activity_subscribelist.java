package com.example.frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.textclassifier.TextClassification;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_subscribelist extends AppCompatActivity {
    private ArrayList<user> mUserList;
    private RecyclerView mRecyclerView;
    private userAdapter mAdapter;
    private TextView prompt;
    Set<String> followedUsers;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";

    public void userInsert(user u){
        mUserList.add(u);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_subscribelist);
        // todo: create mUserList properly with post
        mUserList=new ArrayList<>();
        mRecyclerView=findViewById(R.id.recyclerview);
        mAdapter=new userAdapter(this,mUserList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        followedUsers = mPreferences.getStringSet("followedUsers", followedUsers);


        if (followedUsers != null) {
            for (String followedUser : followedUsers) {
                // TODO: GET userinfo from id in string format
                String jsonStr = "{\"followedUserID\":\""+ followedUser + "\"}";
                String requestUrl = getString(R.string.ipv4)+"showSubscribedlist/";
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
//                System.out.println("hello" + msg_json.getString("username"));
                        String repeatString = "repeated!";
                        if (msg_obj_string.equals("notfound")) {

                        } else {
                            JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                            int ID = msg_json.getIntValue("ID");
                            String username = msg_json.getString("username");
                            // TODO: add to the list in UI
                        }
                    }
                });
            }
        }

        user VirtualUser=new user();
        userInsert(VirtualUser);

        prompt=findViewById(R.id.promptSearch);
        String target="寻找更多用户";
        SpannableString spannableString=new SpannableString(target);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                jumpToUserSearchPage();
            }
        },0,target.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        prompt.setText(spannableString);
        prompt.setMovementMethod(LinkMovementMethod.getInstance());
    }
    public void jumpToUserSearchPage() {
        Intent intent = new Intent(this, activity_searchuser.class);
        startActivity(intent);
    }
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putStringSet("followedUsers", followedUsers);
        preferencesEditor.apply();
    }
}
