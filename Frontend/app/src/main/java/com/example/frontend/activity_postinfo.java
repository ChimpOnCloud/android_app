package com.example.frontend;

import static com.example.frontend.Utils.BuildDialogUtil.buildDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class activity_postinfo extends AppCompatActivity {
    Post post;
    TextView titleTextView;
    TextView contentTextView;
    TextView authorTextView;
    TextView timeTextView;
    LinearLayout commentLayout;
    TextView commemtTextView;
    LinearLayout thumbsLayout;
    ImageView thumbsImageView;
    TextView thumbsTextView;
    LinearLayout likeLayout;
    ImageView likeImageView;
    TextView likeTextView;
    RecyclerView recyclerView;
    messageAdapter mAdapter;
    ArrayList<message> messageList;
    private final Handler handler = new Handler();
    public void messageInsert(message m) {
        messageList.add(m);
        recyclerView.setAdapter(mAdapter);
    }

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

        thumbsLayout=findViewById(R.id.thumbsLayout);
        thumbsTextView = findViewById(R.id.thumbsTextView);
        thumbsImageView = findViewById(R.id.thumbsImageView);
        thumbsTextView.setText(Integer.toString(post.getThumbsupNumber()));
        String jsonStr = "{\"username\":\""+ activity_homepage.User.getUsername() + "\",\"postID\":\""+post.getID()+"\"}";
        String requestUrl = getString(R.string.ipv4) + "getCertainPost/";
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
                buildDialog("Error", "无法连接至服务器。。或许网络出错了？", activity_postinfo.this);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString();
                JSONObject msg_json = JSONObject.parseObject(msg_obj_string);
                String isThumbsup = msg_json.getString("thumbsup");
                String isLike = msg_json.getString("like");
                if (isThumbsup.equals("yes")) {
                    thumbsTextView.setTextColor(Color.RED);
                    thumbsImageView.setColorFilter(Color.RED);
                }
                if (isLike.equals("yes")) {
                    likeTextView.setTextColor(Color.YELLOW);
                    likeImageView.setColorFilter(Color.YELLOW);
                }
                LoadingDialogUtil.getInstance(activity_postinfo.this).closeLoadingDialog();
            }
        });
        thumbsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get if it was thumbuped, to change its color
                handleThumbs(v);
                if (thumbsTextView.getCurrentTextColor() == Color.RED) {
                    thumbsTextView.setTextColor(Color.BLACK);
                    thumbsImageView.setColorFilter(Color.BLACK);

                } else {
                    thumbsTextView.setTextColor(Color.RED);
                    thumbsImageView.setColorFilter(Color.RED);
                }
            }
        });

        likeTextView=findViewById(R.id.likeTextView);
        likeLayout=findViewById(R.id.likeLayout);
        likeImageView = findViewById(R.id.likeImageView);
        likeTextView.setText(Integer.toString(post.getLikeNumber()));
        likeLayout.setOnClickListener(view -> {
            handleLike(view);
        });
        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get if it was liked, to change its color
                handleLike(v);
                if (likeTextView.getCurrentTextColor() == Color.YELLOW) {
                    likeTextView.setTextColor(Color.BLACK);
                    likeImageView.setColorFilter(Color.BLACK);

                } else {
                    likeTextView.setTextColor(Color.YELLOW);
                    likeImageView.setColorFilter(Color.YELLOW);
                }
            }
        });
        commentLayout=findViewById(R.id.commentLayout);
        commemtTextView=findViewById(R.id.commentTextView);
        commemtTextView.setText(Integer.toString(post.getCommentNumber()));
        commentLayout.setOnClickListener(view -> {
            handleComment(view);
        });
        // 设置返回按钮的点击事件
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // todo: create messageList
        messageList=new ArrayList<>();

        recyclerView=findViewById(R.id.comment_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter=new messageAdapter(this,messageList);
        recyclerView.setAdapter(mAdapter);
        messageInsert(new message("hello", activity_homepage.User));
    }
    public void handleThumbs(View v) {
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
                    post.setThumbsupNumber(post.getThumbsupNumber() + 1);
                } else {
                    post.setThumbsupNumber(post.getThumbsupNumber() - 1);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        thumbsTextView.setText(Integer.toString(post.getThumbsupNumber()));
                    }
                });
                LoadingDialogUtil.getInstance(activity_postinfo.this).closeLoadingDialog();
            }
        });
    }
    public void handleLike(View view){
        // todo: connect backend
        LoadingDialogUtil.getInstance(this).showLoadingDialog("Loading...");
        String jsonStr = "{\"pyqID\":\""+ post.getID() + "\",\"username\":\"" + activity_homepage.User.getUsername() + "\"}";
        String requestUrl = getString(R.string.ipv4)+"handleShoucang/";
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
                if (msg_obj_string.equals("addshoucang")) {
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
    public void handleComment(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("input");
        EditText editText=new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // todo
                editText.getText();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog=builder.create();
        builder.show();
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
        user tmpUser=new user(post.getAuthor());
        intent.putExtra("user",tmpUser);
        startActivity(intent);
    }
}