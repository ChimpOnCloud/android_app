package com.example.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_search extends AppCompatActivity {
    private ArrayList<Post> mPostList;
    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;
    private Button searchButton;
    private EditText inputName;
    private final Handler handler = new Handler();
    private TextView targetText;
    private static final String[] targetList=new String[]{"title","content","tag","username"};
    private TextView prompt;

    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_search);
        // todo: create mUserList properly with post
        mPostList=new ArrayList<>();
        mRecyclerView=findViewById(R.id.recyclerview);
        mAdapter=new PostAdapter(mPostList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchButton=findViewById(R.id.buttonSearch);
        inputName=findViewById(R.id.search);
        targetText=findViewById(R.id.target);
        prompt=findViewById(R.id.promptSearchUser);
        String text = prompt.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(activity_search.this, activity_searchuser.class);
                startActivity(intent);
            }
        },0,text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        prompt.setText(spannableString);
        prompt.setMovementMethod(LinkMovementMethod.getInstance());
    }
    public void jumpToUserSearchPage(View v) {
        Intent intent = new Intent(this, activity_searchuser.class);
        startActivity(intent);
    }
    public void onTargetClicked(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("选择话题");
        builder.setSingleChoiceItems(activity_search.targetList, 0, new DialogInterface.OnClickListener() {
            @Override
            //which为你当前选中的索引，从0开始
            public void onClick(DialogInterface dialogInterface, int which) {
                targetText.setText(activity_search.targetList[which]);
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public void onSearchClicked(View v){
        String targetName=inputName.getText().toString();
        if(targetName.equals(null)) return;
        // todo: change the searching target to correct pyq
        String searchTarget=targetText.getText().toString(); // todo: use this
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity_search.this);
                        builder.setTitle("Error");
                        builder.setMessage("无法连接至服务器。。或许网络出错了？");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                });
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
                    mAdapter.mPosts.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder=new AlertDialog.Builder(activity_search.this);
                            builder.setTitle("Info");
                            builder.setMessage("该动态不存在");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                        }
                    });
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    });
                    System.out.println("no such post");
                } else {
                    System.out.println("succeeded");
                    mAdapter.mPosts.clear();
                    // todo: change treatment here
                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // todo: create mPosts properly
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    });
                }
            }
        });
    }
}