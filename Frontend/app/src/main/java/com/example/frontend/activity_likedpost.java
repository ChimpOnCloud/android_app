package com.example.frontend;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class activity_likedpost extends AppCompatActivity {
    private RecyclerView mRecyclerview;
    private PostAdapter mAdapter;
    private ArrayList<Post> mPostList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likedpost);
        mPostList=new ArrayList<>();
        // todo: create mPostList
        mRecyclerview=findViewById(R.id.recyclerview);
        mAdapter=new PostAdapter(mPostList);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
}
