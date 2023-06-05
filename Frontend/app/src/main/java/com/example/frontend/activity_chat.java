package com.example.frontend;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class activity_chat extends AppCompatActivity {
    private chatList mChatList;
    private RecyclerView mRecyclerView;
    private chatListAdapter mAdapter;
    private final Handler handler = new Handler();

    public void chatListInsert(chat c){
        mChatList.insert(c);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_chat);
        mChatList=new chatList();
        mRecyclerView=findViewById(R.id.recyclerview);
        mAdapter=new chatListAdapter(this,mChatList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        user VirtualUser=new user(2,"username","password","nickname","my introduction");
//        ArrayList<message> VirtualList=new ArrayList<>();
//        VirtualList.add(new message("Hello!!",VirtualUser));
//        chat VirtualChat=new chat(VirtualUser,VirtualList);
//        chatListInsert(VirtualChat);
        // System.out.println(activity_homepage.User.getUsername());
        // TODO: find all followed users and add to the chatlist
        String jsonStr = "{\"curUsername\":\""+ activity_homepage.User.getUsername() + "\"}";
        String requestUrl = getString(R.string.ipv4)+"findRelatedChatUsers/";
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
                if (msg_obj_string.equals("error")) {

                } else {
                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                    for (int i = 0; i < msg_json.size(); i++) {
                        String insert_username = msg_json.getString(Integer.toString(i));
                        if (insert_username.equals(activity_homepage.User.getUsername())) {
                            continue;
                        }
                        user insertUser = new user(insert_username); // here we only store username, other attributes shouldn't be used!
                        ArrayList<message> msgList=new ArrayList<>();
                        // msgList.add(new message("Hello!!",insertUser));
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                getAllMessages(activity_homepage.User.getUsername(), insert_username, insertUser, msgList);
                            }
                        });
                        chat msgChat=new chat(insertUser,msgList);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                chatListInsert(msgChat); // overlap??
                            }
                        });
                    }
                }
            }
        });
    }
    public void getAllMessages(String srcUsername, String dstUsername, user insertUser, ArrayList<message> msgList) {
        String jsonStr = "{\"srcUsername\":\""+ srcUsername + "\",\"dstUsername\":\""+dstUsername+"\"}";
        String requestUrl = getString(R.string.ipv4)+"getRelatedMessages/";
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
                if (msg_obj_string.equals("error")) {

                } else {
                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                    System.out.println(msg_json);
                    int msg_cnt = msg_json.size() / 2;
                    for (int i = 0; i < msg_cnt; i++) {
                        String msg_i = msg_json.getString("msg" + i);
                        String is_send_i_string = msg_json.getString("is_send" + i);
                        Boolean is_send_i;
                        if (is_send_i_string.equals("false")) {
                            is_send_i = false;
                        } else {
                            is_send_i = true;
                        }
                        if (is_send_i == false) {
                            msgList.add(new message(msg_i,insertUser));
                        } else {
                            msgList.add(new message(msg_i,null));
                        }
                    }
                }
            }
        });
    }
}
