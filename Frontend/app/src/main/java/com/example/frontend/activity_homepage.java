package com.example.frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class activity_homepage extends AppCompatActivity {
    String username = "";
    String password = "";
    private String TESTSTRING1 = "username";
    private String TESTSTRING2 = "password";
    private String loginUsername = "";
    private String loginPassword = "";
    private String LOGINSTATUS = "loginstatus";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    boolean isLogin = false; // if True, restore the previous login status.
    private BottomNavigationView bottomNavigationView;
    private RecyclerView mPostRecyclerView;
    private PostAdapter mPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        setContentView(R.layout.activity_homepage);
        Bundle bundle = this.getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        isLogin = bundle.getBoolean("isLogin");
        System.out.println(isLogin);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = new BlankFragment();
                        break;
                    case R.id.navigation_topic:
                        selectedFragment = new BlankFragment();
                        break;
                    case R.id.navigation_guide:
                    {
                        jumpToChat();
                        return true;
                    }
                    case R.id.navigation_me:
                        {
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
        List<Post> posts = new ArrayList<>();
        // Populate the list with Post objects
        Post post1 = new Post();
        Post post2 = new Post();
        Post post3 = new Post();
        Post post4 = new Post();
        posts.add(post1);
        posts.add(post2);
        posts.add(post3);
        posts.add(post4);

        mPostAdapter = new PostAdapter(posts);
        mPostRecyclerView.setAdapter(mPostAdapter);

        mPostAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String viewType) {
                if (viewType.equals("title") || viewType.equals("content")) {
                    Intent intent = new Intent(activity_homepage.this, PostInfoActivity.class);
                    intent.putExtra("post", posts.get(position));
                    startActivity(intent);
                }
            }
        });



        FloatingActionButton addPostButton = findViewById(R.id.add_post_button);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post_new = new Post();
                post_new.setAuthor("新动态作者");
                post_new.setTitle("新动态标题");
                post_new.setContent("1.新动态内容\n2.新动态内容 \n3.新动态内容");
                int[] images = {R.drawable.image7, R.drawable.image8, R.drawable.image9,
                        R.drawable.image10, R.drawable.image11, R.drawable.image12};
                post_new.setImages(images);
                posts.add(0, post_new);
                mPostAdapter.notifyDataSetChanged();
//                Intent intent = new Intent(MainActivity.this, PostEditActivity.class);
//                startActivity(intent);
            }
        });

    }

    public void jumpToUserInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        Intent intent = new Intent(this, activity_userinfo.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void jumpToChat(){
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        Intent intent = new Intent(this, activity_chat.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void logout(View v) {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putBoolean(LOGINSTATUS, false); // login status should be false
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString(TESTSTRING1, loginUsername);
        preferencesEditor.putString(TESTSTRING2, loginPassword);
        preferencesEditor.putBoolean(LOGINSTATUS, isLogin);
        preferencesEditor.apply();
    }
}