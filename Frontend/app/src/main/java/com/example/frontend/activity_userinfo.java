package com.example.frontend;

import static com.example.frontend.Utils.AvatarUtil.getAvatar;
import static com.example.frontend.Utils.BuildDialogUtil.buildDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.frontend.Utils.LoadingDialogUtil;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.Guard;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_userinfo extends AppCompatActivity {
    user mUser;
    TextView usrnameContent;
    TextView nicknameContent;
    TextView introductionContent;
    Button checkSubButton;
    Button subscribeMeButton;
    ImageView userIcon;
    Boolean subscribe;
    Boolean shielded;
    private ImageButton moreButton;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    private Boolean isCurrentUser;
    private RecyclerView mRecyclerview;
    private ArrayList<Post> mPostList;
    private PostAdapter mAdapter;
    private final String LOGINSTATUS = "loginstatus";
    private final Handler handler = new Handler();


    private void postInsert(Post p){
        mPostList.add(p);
        mRecyclerview.setAdapter(mAdapter);
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        if(isCurrentUser) popupMenu.getMenuInflater().inflate(R.menu.user_self, popupMenu.getMenu());
        else popupMenu.getMenuInflater().inflate(R.menu.user_other,popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if ("退出".equals(item.getTitle())) {
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                    preferencesEditor.putBoolean(LOGINSTATUS, false); // login status should be false
                    preferencesEditor.commit();
                    Intent intent = new Intent(activity_userinfo.this, MainActivity.class);
                    startActivity(intent);
                }
                else if("设置".equals(item.getTitle())){
                    jumpToInfoEditPage();
                }
                else if ("通知".equals(item.getTitle())) {
                    jumpToInfoPage();
                }
                else if("关注/取关".equals(item.getTitle())){
                    subscribe=!subscribe;
                    if(subscribe) Toast.makeText(activity_userinfo.this,"关注成功",Toast.LENGTH_SHORT).show();
                    else Toast.makeText(activity_userinfo.this,"取关成功",Toast.LENGTH_SHORT).show();
                    // todo: notify backend
                }
                else if("屏蔽/解除".equals(item.getTitle())){
                    shielded=!shielded;
                    if(shielded) Toast.makeText(activity_userinfo.this,"屏蔽成功",Toast.LENGTH_SHORT).show();
                    else Toast.makeText(activity_userinfo.this,"解除成功",Toast.LENGTH_SHORT).show();
                    // todo: notify backend
                }
                else if("私聊".equals(item.getTitle())){
                    // todo: get chat content
                    chat targetChat=new chat(mUser,new ArrayList<>());
                    Intent intent=new Intent(activity_userinfo.this,activity_chatdetail.class);
                    intent.putExtra("chat",targetChat);
                    startActivity(intent);
                }
                return true;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        isCurrentUser=false;
        mUser= (user) getIntent().getSerializableExtra("user");
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        if(mUser==null){ // default user is the user currently using the app
            mUser=activity_homepage.User;
            isCurrentUser=true;
        }
        usrnameContent = findViewById(R.id.textView_usrname_content);
        nicknameContent = findViewById(R.id.textView_nickname_content);
        introductionContent = findViewById(R.id.textView_introduction_content);
        checkSubButton=findViewById(R.id.checkSubscribed);
        subscribeMeButton=findViewById(R.id.subscribeMe);
        userIcon=findViewById(R.id.userIcon);
        moreButton=findViewById(R.id.moreButton);
        mRecyclerview=findViewById(R.id.pyqlist);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        usrnameContent.setText(mUser.getUsername());
        // todo: get all user info here..
        nicknameContent.setText(mUser.getNickname());
        introductionContent.setText(mUser.getIntroduction());
        getAvatar(this,userIcon,mUser.getUsername());
        // todo: get subscribe pattern
        subscribe=false;
        // todo: get shield pattern
        shielded=false;

        // todo: crete mPostList
        mPostList=new ArrayList<>();
        mAdapter=new PostAdapter(mPostList);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
//        postInsert(new Post());
        String jsonStr = "{\"username\":\""+ mUser.getUsername() + "\"}";
        System.out.println(jsonStr);
        String requestUrl = getString(R.string.ipv4)+"getUserPosts/";
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
                LoadingDialogUtil.getInstance(activity_userinfo.this).closeLoadingDialog();
                buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_userinfo.this);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                if (msg_obj_string.equals("error")) {

                } else {
                    JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                    JSONObject post_name_dict = new JSONObject();
                    post_name_dict.put("0", "#默认话题");
                    post_name_dict.put("1", "#校园资讯");
                    post_name_dict.put("2", "#二手交易");
                    post_name_dict.put("3", "#思绪随笔");
                    post_name_dict.put("4", "#吐槽盘点");
                    for (int i = 0; i < msg_json.size() / 8; i++) {
                        Post post = new Post("",
                                msg_json.getString("username" + i),
                                msg_json.getString("posttime" + i).substring(0,19),
                                msg_json.getString("title" + i),
                                msg_json.getString("content" + i),
                                msg_json.getString("tag" + i),
                                msg_json.getString("id" + i),
                                Integer.parseInt(msg_json.getString("like_number" + i)));
                        handler.post(new Runnable() {
                        @Override
                        public void run() {
                            postInsert(post);
                        }
                    });
                    }
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mAdapter = new PostAdapter(mPostList);
//                            mRecyclerview.setAdapter(mAdapter);
//                        }
//                    });
                }
                LoadingDialogUtil.getInstance(activity_userinfo.this).closeLoadingDialog();
            }
        });
    }

    public void jumpToInfoEditPage() {
        Intent intent = new Intent(this, activity_editinfo.class);
        startActivity(intent);
    }

    public void jumpToInfoPage(){
        Intent intent=new Intent(this,activity_newinfo.class);
        startActivity(intent);
    }
    public void checkSubscribed(View v){
        Intent intent=new Intent(this,activity_subscribelist.class);
        intent.putExtra("user",mUser);
        startActivity(intent);
    }
    public void checkLikedPost(View v){
        Intent intent=new Intent(this,activity_likedpost.class);
        startActivity(intent);
    }
}