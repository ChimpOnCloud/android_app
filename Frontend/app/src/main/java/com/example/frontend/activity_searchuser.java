package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_searchuser extends AppCompatActivity {
    private ArrayList<user> mUserList;
    private RecyclerView mRecyclerView;
    private userAdapter mAdapter;
    private Button searchButton;
    private EditText inputName;
    private final Handler handler = new Handler();

    public void userInsert(user u){
        mUserList.add(u);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_searchuser);

        // todo: create mUserList properly with post
        mUserList=new ArrayList<>();
        mRecyclerView=findViewById(R.id.recyclerview);
        mAdapter=new userAdapter(this,mUserList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchButton=findViewById(R.id.buttonSearch);
        inputName=findViewById(R.id.search);
        /*user VirtualUser=new user();
        userInsert(VirtualUser);*/
    }
    public void jumpToUserSearchPage(View v) {
        Intent intent = new Intent(this, activity_searchuser.class);
        startActivity(intent);
    }

    public void onSearchClicked(View v){
        // todo: get info from backend and create mUserList properly
        String targetName=inputName.getText().toString();
        if(targetName.equals(null)) return;
        String jsonStr = "{\"targetName\":\""+ targetName + "\"}";
        String requestUrl = getString(R.string.ipv4)+"searchUser/";
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
                if (msg_obj_string.equals("notfound")) {
                    System.out.println("no such user");
                } else {
                    System.out.println("succeeded");
                    System.out.println(msg_obj_string);
                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                    int id = msg_json.getIntValue("ID");
                    String username = msg_json.getString("username");
                    String password = msg_json.getString("password");
                    String nickname = msg_json.getString("nickname");
                    String introduction = msg_json.getString("introduction");
                    user targetUser = new user(id, username, password, nickname, introduction);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            userInsert(targetUser);
                        }
                    });// TODO: bug here!
                }
            }
        });
    }
}