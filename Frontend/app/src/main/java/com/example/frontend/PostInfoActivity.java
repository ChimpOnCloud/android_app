package com.example.frontend;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);

        // 获取传递过来的 Post 对象
        Post post = getIntent().getParcelableExtra("post");

        // 在页面中显示该 Post 的信息
        TextView titleTextView = findViewById(R.id.post_title);
        titleTextView.setText(post.getTitle());

        TextView contentTextView = findViewById(R.id.post_content);
        contentTextView.setText(post.getContent());

        TextView authorTextView = findViewById(R.id.post_author);
        authorTextView.setText(post.getAuthor());

        TextView timeTextView = findViewById(R.id.post_time);
        long timestamp = post.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedTime = sdf.format(new Date(timestamp));
        timeTextView.setText(formattedTime);


        // 设置返回按钮的点击事件
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}