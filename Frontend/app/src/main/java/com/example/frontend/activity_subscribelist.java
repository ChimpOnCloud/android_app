package com.example.frontend;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class activity_subscribelist extends AppCompatActivity {
    private ArrayList<user> mUserList;
    private RecyclerView mRecyclerView;
    private userAdapter mAdapter;

    public void userInsert(user u){
        mUserList.add(u);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_chat);
        // todo: create mUserList properly with post
        mUserList=new ArrayList<>();
        mRecyclerView=findViewById(R.id.recyclerview);
        mAdapter=new userAdapter(this,mUserList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        user VirtualUser=new user();
        userInsert(VirtualUser);
    }
}
