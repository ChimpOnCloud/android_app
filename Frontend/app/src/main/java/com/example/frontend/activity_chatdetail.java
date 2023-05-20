package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class activity_chatdetail extends AppCompatActivity {
    private chat mChat;
    private RecyclerView mRecyclerView;
    private chatAdapter mAdapter;
    private TextView oppoName;
    private EditText text;
    private Button sendButton;
    public void chatInsert(message m){
        mChat.insert(m);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatdetail);
        // user VirtualUser=new user(2,"username","password","nickname","my introduction");
        Intent intent=this.getIntent();
        mChat=(chat)intent.getSerializableExtra("chat");
        mRecyclerView=findViewById(R.id.chatrecyclerview);
        mAdapter=new chatAdapter(this,mChat);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        oppoName=findViewById(R.id.oppoName);
        oppoName.setText(" "+mChat.getOpposite().getNickname());
        text=findViewById(R.id.chatText);
        sendButton=findViewById(R.id.buttonsend);
        sendButton.setOnClickListener(view -> {
            String content=text.getText().toString();
            if(content=="") return;
            message m=new message(content,activity_homepage.User);
            chatInsert(m);
            text.setText("");
        });
    }
}
