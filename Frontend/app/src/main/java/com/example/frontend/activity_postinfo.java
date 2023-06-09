package com.example.frontend;

import static com.example.frontend.Utils.BuildDialogUtil.buildDialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.frontend.Utils.LoadingDialogUtil;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_postinfo extends AppCompatActivity {
    Post post;
    TextView titleTextView;
    TextView contentTextView;
    TextView authorTextView;
    TextView timeTextView;
    ImageView likeImageView;
    TextView likeTextView;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);

        // 获取传递过来的 Post 对象
        post = getIntent().getParcelableExtra("post");
        // 在页面中显示该 Post 的信息
        titleTextView = findViewById(R.id.post_title);
        titleTextView.setText(post.getTitle());

        contentTextView = findViewById(R.id.post_content);
        contentTextView.setText(post.getContent());

        authorTextView = findViewById(R.id.post_author);
        authorTextView.setText(post.getAuthor());

        timeTextView = findViewById(R.id.post_time);
        timeTextView.setText(post.getTime());

        likeImageView = findViewById(R.id.likeImageView);
        likeTextView = findViewById(R.id.likeTextView);
        likeTextView.setText(Integer.toString(post.getLikeNumber()));
        System.out.println(post.getLikeNumber());
        likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLike(v);
            }
        });
        likeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(getString(post.getLikeNumber()));
                handleLike(v);
            }
        });


        // 设置返回按钮的点击事件
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void handleLike(View v) {
        LoadingDialogUtil.getInstance(this).showLoadingDialog("Loading...");
        String jsonStr = "{\"pyqID\":\""+ post.getID() + "\",\"username\":\"" + activity_homepage.User.getUsername() + "\"}";
        String requestUrl = getString(R.string.ipv4)+"handleLike/";
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
                LoadingDialogUtil.getInstance(activity_postinfo.this).closeLoadingDialog();
                buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_postinfo.this);
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                if (msg_obj_string.equals("addlike")) {
                    post.setLikeNumber(post.getLikeNumber() + 1);
                } else {
                    post.setLikeNumber(post.getLikeNumber() - 1);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        likeTextView.setText(Integer.toString(post.getLikeNumber()));
                    }
                });
                LoadingDialogUtil.getInstance(activity_postinfo.this).closeLoadingDialog();
            }
        });
    }
    public void onAvatarClick(View view)
    {
        Intent intent = new Intent(this, activity_userinfo.class);
        Log.d("a","go to userinfo");
        // todo
        String jsonStr = "{\"username\":\""+ post.getAuthor() + "\"}";
        System.out.println(jsonStr);
        String requestUrl = getString(R.string.ipv4)+"getAuthor/";
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
                LoadingDialogUtil.getInstance(activity_postinfo.this).closeLoadingDialog();
                buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_postinfo.this);
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
                    System.out.println(msg_obj_string);
                }
            }
        });
        // intent.putExtra("user",post.getAuthor());
        // startActivity(intent);
    }
}