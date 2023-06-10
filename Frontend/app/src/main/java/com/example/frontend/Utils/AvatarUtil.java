package com.example.frontend.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.R;
import com.example.frontend.activity_userinfo;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

public class AvatarUtil {
    public static void getAvatar(Context context, ImageView userIcon, String username) {
        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 添加日志输出
                Log.d("Debug", "username: " + username);
                String avatarUrl = context.getString(R.string.ipv4) + "getAvatar/" + username;

                Picasso p = new Picasso.Builder(context).downloader(new OkHttp3Downloader(new OkHttpClient())).build();
                p.load(avatarUrl)
                        .placeholder(R.drawable.ic_default_avatar)
                        .resize(200,200)
                        .centerCrop() // 可选，如果需要将图像裁剪为正方形
                        .into(userIcon);
            }
        });
    }
}
