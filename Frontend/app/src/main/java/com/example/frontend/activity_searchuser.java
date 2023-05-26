package com.example.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class activity_searchuser extends AppCompatActivity {
    private ArrayList<user> mUserList;
    private RecyclerView mRecyclerView;
    private userAdapter mAdapter;
    private Button searchButton;
    private EditText inputName;

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
        user VirtualUser=new user();
        userInsert(VirtualUser);
    }
    public void jumpToUserSearchPage(View v) {
        Intent intent = new Intent(this, activity_searchuser.class);
        startActivity(intent);
    }

    public void onSearchClicked(View v){
        // todo: get info from backend and create mUserList properly
        String targetName=inputName.getText().toString();
        if(targetName.equals(null)) return;
    }
}