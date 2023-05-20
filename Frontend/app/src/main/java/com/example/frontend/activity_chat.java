package com.example.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class activity_chat extends AppCompatActivity {
    private chatList mChatList;
    private RecyclerView mRecyclerView;
    private chatListAdapter mAdapter;

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
        user VirtualUser=new user(2,"username","password","nickname","my introduction");
        ArrayList<message> VirtualList=new ArrayList<>();
        VirtualList.add(new message("Hello!",VirtualUser));
        chat VirtualChat=new chat(VirtualUser,VirtualList);
        chatListInsert(VirtualChat);
    }
}
