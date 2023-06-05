package com.example.frontend;

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
import android.os.Looper;
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

import java.security.Guard;
import java.util.ArrayList;

public class activity_userinfo extends AppCompatActivity {
    user mUser;
    TextView usrnameContent;
    TextView nicknameContent;
    TextView introductionContent;
    Button checkSubButton;
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
    private TextView logoutText;
    private final String LOGINSTATUS = "loginstatus";


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
                    // todo: notify backend
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder=new AlertDialog.Builder(activity_userinfo.this);
                            builder.setTitle("...");
                            builder.setMessage("to be finished");
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog=builder.create();
                            dialog.show();
                        }
                    });
                }
                else if("屏蔽/解除".equals(item.getTitle())){
                    shielded=!shielded;
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

        // todo: create userIcon with image
        usrnameContent = findViewById(R.id.textView_usrname_content);
        nicknameContent = findViewById(R.id.textView_nickname_content);
        introductionContent = findViewById(R.id.textView_introduction_content);
        checkSubButton=findViewById(R.id.checkSubscribed);
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
        nicknameContent.setText(mUser.getNickname());
        introductionContent.setText(mUser.getIntroduction());
        // todo: get subscribe pattern
        subscribe=false;
        // todo: get shield pattern
        shielded=false;

        // todo: crete mPostList
        mPostList=new ArrayList<>();
        mAdapter=new PostAdapter(mPostList);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        postInsert(new Post());
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
}