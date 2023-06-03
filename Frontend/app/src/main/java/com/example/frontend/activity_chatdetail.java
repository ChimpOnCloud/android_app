package com.example.frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_chatdetail extends AppCompatActivity {
    private chat mChat;
    private RecyclerView mRecyclerView;
    private chatAdapter mAdapter;
    private TextView oppoName;
    private EditText text;
    private Button sendButton;
    private Handler updateChatHandler;
    private Runnable task;
    public void chatInsert(message m){
        mChat.insert(m);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatdetail);
        Intent intent=this.getIntent();
        mChat=(chat)intent.getSerializableExtra("chat");
        mRecyclerView=findViewById(R.id.chatrecyclerview);
        mAdapter=new chatAdapter(this,mChat);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        oppoName=findViewById(R.id.oppoName);
        oppoName.setText(" "+mChat.getOpposite().getUsername());
        text=findViewById(R.id.chatText);
        sendButton=findViewById(R.id.buttonsend);
        sendButton.setOnClickListener(view -> {
            String content=text.getText().toString();
            if(content=="") return;
            message m=new message(content,activity_homepage.User);
            chatInsert(m);
            text.setText("");
            // TODO: backend adds this message
            String jsonStr = "{\"msgContent\":\""+ content + "\",\"fromUser\":\""+activity_homepage.User.getUsername()+"\"}";
            System.out.println(jsonStr);
            String requestUrl = getString(R.string.ipv4)+"addMessageToChat/";
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

                    } else if (msg_obj_string.equals("repeated username!")) {

                    }
                }
            });
        });

        updateChatHandler=new Handler();
        int delay=500;  // ms between refreshing chatContent
        task=new Runnable() {
            @Override
            public void run() {
                update();
                updateChatHandler.postDelayed(this,delay);
            }
        };
        updateChatHandler.postDelayed(task,delay);
        // use updateChatHandler.removeCallbacks(runnable); if want to close the handler
    }

    public void update(){
        // todo: refresh the mChat.chatContent
        mRecyclerView.setAdapter(mAdapter);
    }
}
