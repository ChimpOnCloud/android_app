package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import com.alibaba.fastjson.JSONObject;
import com.example.frontend.Filter.FilterActivity;
import com.example.frontend.Filter.FilterBean;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

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

public class activity_homepage extends AppCompatActivity {
    private final Handler handler = new Handler();
    String username = "";
    String password = "";
    String nickname = "";
    String introduction = "";
    private String TESTSTRING1 = "username";
    private String TESTSTRING2 = "password";
    private String loginUsername = "";
    private String loginPassword = "";
    private final String LOGINSTATUS = "loginstatus";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    boolean isLogin = false; // if True, restore the previous login status.
    private BottomNavigationView bottomNavigationView;
    public static user User;
    private RecyclerView mPostRecyclerView;
    private PostAdapter mPostAdapter;
    private static final int newPost=1;
    private static final int filter=0;
    private ArrayList<Post> posts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        setContentView(R.layout.activity_homepage);
        username = mPreferences.getString("username", username);
        password = mPreferences.getString("password", password);
        nickname = mPreferences.getString("nickname", nickname);
        introduction = mPreferences.getString("introduction", introduction);
        isLogin = mPreferences.getBoolean("loginstatus", isLogin);
        // todo: create the User with params
        User=new user(1,username,password,nickname,introduction);
        // Log.d("a",User.getUsername());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        jumpToHomePage();
                        return true;
                    case R.id.navigation_topic:
                        jumpToSearch();
                        return true;
                    case R.id.navigation_guide: {
                        jumpToChat();
                        return true;
                    }
                    case R.id.navigation_me: {
                        jumpToUserInfo(); // 调用跳转到用户信息界面的方法
                        return true; // 注意要在此处返回 true，表示已处理点击事件
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
        // Find the RecyclerView and set its LayoutManager
        mPostRecyclerView = findViewById(R.id.post_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPostRecyclerView.setLayoutManager(layoutManager);

        // Create a list of Post objects and set the adapter
        posts = new ArrayList<>();
        // Populate the list with Post objects
        String jsonStr = "";
        String requestUrl = getString(R.string.ipv4)+"getAllPosts/";
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
                    for (int i = 0; i < msg_json.size() / 6; i++) {
                        Post post = new Post("", msg_json.getString("username" + i), msg_json.getString("posttime" + i).substring(0,19), msg_json.getString("title" + i), msg_json.getString("content" + i), msg_json.getString("tag" + i));
                        posts.add(post);
                    }
                    handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mPostAdapter = new PostAdapter(posts);
                                mPostRecyclerView.setAdapter(mPostAdapter);
                            }
                    });
                }
            }
        });

//        Post post1 = new Post();
//        Post post2 = new Post();
//        Post post3 = new Post();
//        Post post4 = new Post();
//        posts.add(post1);
//        posts.add(post2);
//        posts.add(post3);
//        posts.add(post4);

//        mPostAdapter = new PostAdapter(posts);
//        mPostRecyclerView.setAdapter(mPostAdapter);
//        mPostAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position, String viewType) {
//                Intent intent = new Intent(activity_homepage.this, PostInfoActivity.class);
//                intent.putExtra("post", posts.get(position));
//                startActivity(intent);
//            }
//        });
        FloatingActionButton addPostButton = findViewById(R.id.add_post_button);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_homepage.this, activity_postedit.class);
                startActivityForResult(intent,newPost);
            }
        });
        FloatingActionButton filterButton=findViewById(R.id.filter_button);
        filterButton.setOnClickListener(view -> {
            Intent intent=new Intent(activity_homepage.this, FilterActivity.class);
            startActivityForResult(intent,filter);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case newPost:
                if (resultCode == RESULT_OK) {
                    Post post=data.getParcelableExtra("newPost");
                    post.setAuthor(User.getUsername());
                    // todo: notify backend
                    posts.add(post);
                    mPostAdapter.notifyDataSetChanged();
                }
                break;
            case filter:
                if(resultCode==RESULT_OK){
                    ArrayList<FilterBean> arrayList= (ArrayList<FilterBean>) data.getSerializableExtra("result");
                    Log.d("a",arrayList.toString());
                    Boolean onlyCheckSubscribed=false;
                    if(arrayList.get(0).name.equals("开启")) onlyCheckSubscribed=true;
                    int sorting=0; // 0: 未指定 1:按时间 2:按热度
                    if(arrayList.get(1).name.equals("按时间排序")) sorting=1;
                    else if(arrayList.get(1).name.equals("按热度排序")) sorting=2;
                    String tag=arrayList.get(2).name;
                    // todo: reset pyq in posts while connecting backend
                }
        }
    }

    public void jumpToHomePage(){
        Intent intent=new Intent(this,activity_homepage.class);
        startActivity(intent);
    }
    public void jumpToUserInfo() {
        Intent intent = new Intent(this, activity_userinfo.class);
        startActivity(intent);
    }

    public void jumpToChat(){
        Intent intent = new Intent(this, activity_chat.class);
        startActivity(intent);
    }

    public void jumpToSearch(){
        Intent intent=new Intent(this,activity_search.class);
        startActivity(intent);
    }


    @SuppressLint("ApplySharedPref")
    public void logout(View v) {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, false); // login status should be false
        preferencesEditor.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    protected void onPause() {
        super.onPause();
    }
}