package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.textclassifier.TextClassification;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class activity_subscribelist extends AppCompatActivity {
    private ArrayList<user> mUserList;
    private RecyclerView mRecyclerView;
    private userAdapter mAdapter;
    private TextView prompt;

    public void userInsert(user u){
        mUserList.add(u);
        mRecyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_subscribelist);
        // todo: create mUserList properly with post
        mUserList=new ArrayList<>();
        mRecyclerView=findViewById(R.id.recyclerview);
        mAdapter=new userAdapter(this,mUserList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        user VirtualUser=new user();
        userInsert(VirtualUser);

        prompt=findViewById(R.id.promptSearch);
        String target="寻找更多用户";
        SpannableString spannableString=new SpannableString(target);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                jumpToUserSearchPage();
            }
        },0,target.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        prompt.setText(spannableString);
        prompt.setMovementMethod(LinkMovementMethod.getInstance());
    }
    public void jumpToUserSearchPage() {
        Intent intent = new Intent(this, activity_searchuser.class);
        startActivity(intent);
    }
}
