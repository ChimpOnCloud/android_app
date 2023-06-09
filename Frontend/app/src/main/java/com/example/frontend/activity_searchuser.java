package com.example.frontend;

import static com.example.frontend.BuildDialogUtil.buildDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.SyncFailedException;
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
    user targetUser;

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
    }
    public void jumpToUserSearchPage(View v) {
        Intent intent = new Intent(this, activity_searchuser.class);
        startActivity(intent);
    }

    public void onSearchClicked(View v){
        String targetName=inputName.getText().toString();
        if(targetName.equals(null)) return;
        LoadingDialogUtil.getInstance(this).showLoadingDialog("Loading...");
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
                LoadingDialogUtil.getInstance(activity_searchuser.this).closeLoadingDialog();
                buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_searchuser.this);
                // System.out.println("failed");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                if (msg_obj_string.equals("notfound")) {
                    mAdapter.mUserList.clear();
                    buildDialog("Info","未找到用户",activity_searchuser.this);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    });
                    System.out.println("no such user");
                } else {
                    System.out.println("succeeded");
                    // empty the mUserList
                    mAdapter.mUserList.clear();
                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                    int id = msg_json.getIntValue("ID");
                    String username = msg_json.getString("username");
                    String password = msg_json.getString("password");
                    String nickname = msg_json.getString("nickname");
                    String introduction = msg_json.getString("introduction");
                    targetUser = new user(id, username, password, nickname, introduction);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            userInsert(targetUser);
                        }
                    });
                }
                LoadingDialogUtil.getInstance(activity_searchuser.this).closeLoadingDialog();
            }
        });
    }
}