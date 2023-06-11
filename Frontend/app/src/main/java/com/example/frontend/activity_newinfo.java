package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class activity_newinfo extends AppCompatActivity {
    private ArrayList<Info> mInfoList;
    private RecyclerView mRecyclerView;
    private InfoAdapter mAdapter;
    public void insertInfo (Info f) {
        mInfoList.add(f);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newinfo);
        mInfoList=new ArrayList<>();
        mRecyclerView=findViewById(R.id.recyclerview);
        mAdapter=new InfoAdapter(this,mInfoList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // todo: creata mInfoList properly
        insertInfo(new Info(new user("testuser"), 0));
    }
    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getIntent();
        user mUser = (user)intent.getSerializableExtra("user");
        int type = intent.getIntExtra("type", 0);
        insertInfo(new Info(mUser, type));
    }
}
