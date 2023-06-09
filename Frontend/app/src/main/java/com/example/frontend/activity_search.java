package com.example.frontend;

import static com.example.frontend.Utils.BuildDialogUtil.buildDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
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
import com.example.frontend.Utils.LoadingDialogUtil;

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
        LoadingDialogUtil.getInstance(this).showLoadingDialog("Loading...");
        String searchTarget=targetText.getText().toString(); // todo: use this
        String jsonStr = "{\"targetName\":\""+ targetName + "\",\"targetKind\":\"" + targetText.getText().toString() + "\"}";
        System.out.println(jsonStr);
        String requestUrl = getString(R.string.ipv4)+"getSearchedPyq/";
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
                LoadingDialogUtil.getInstance(activity_search.this).closeLoadingDialog();
                buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_search.this);
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
                    buildDialog("Info","一个动态都没找到。。",activity_search.this);
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
                    JSONObject post_name_dict = new JSONObject();
                    post_name_dict.put("0", "#默认话题");
                    post_name_dict.put("1", "#校园资讯");
                    post_name_dict.put("2", "#二手交易");
                    post_name_dict.put("3", "#思绪随笔");
                    post_name_dict.put("4", "#吐槽盘点");
                    Log.d("ssss", msg_obj_string);
//                    ArrayList<String> bitmaps = new ArrayList<>();
                    for (int i = 0; i < Integer.parseInt(msg_json.getString("num")); i++) {
                        ArrayList<String> bitmaps = new ArrayList<>();
                        int lenImgs = Integer.parseInt(msg_json.getString("num_imgs" + i));
//                        Log.d("1", String.valueOf(lenImgs));
                        for (int j = 0; j < lenImgs; j++) {
                            bitmaps.add(msg_json.getString("pyq_" + i + "_img" + j));
                        }
                        Post post = new Post("",
                                msg_json.getString("username" + i),
                                msg_json.getString("posttime" + i).substring(0, 19),
                                msg_json.getString("title" + i),
                                msg_json.getString("content" + i),
                                msg_json.getString("tag" + i),
                                msg_json.getString("id" + i),
                                Integer.parseInt(msg_json.getString("like_number" + i)),
                                Integer.parseInt(msg_json.getString("shoucang_number" + i)),
                                Integer.parseInt(msg_json.getString("comment_number" + i)),
                                bitmaps);
                        ArrayList<message> cur_comments = new ArrayList<message>();
//                        Log.d("hello", msg_json.getString("comment_number" + i));
                        for (int j = 0; j < Integer.parseInt(msg_json.getString("comment_number" + i)); j++) {
                            cur_comments.add(new message(msg_json.getString("commentcontent" + i + "number" + j),
                                    new user(msg_json.getString("commentusername" + i + "number" + j))));
//                            Log.d("msg", cur_comments.get(j).getMessageString());
//                            Log.d("usr", cur_comments.get(j).getFrom().getUsername());
                        }
                        post.setComments(cur_comments);
                        mAdapter.mPosts.add(post);
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // todo: create mPosts properly
                            mRecyclerView.setAdapter(mAdapter);
                        }
                    });
                }
                LoadingDialogUtil.getInstance(activity_search.this).closeLoadingDialog();
            }
        });
    }
}
