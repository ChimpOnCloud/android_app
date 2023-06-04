package com.example.frontend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class activity_video extends AppCompatActivity{
    private VideoView videoView;
    private Uri uri;
    private MediaController controller;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_video);
        videoView=findViewById(R.id.videoview);
        controller=new MediaController(this);
        Intent intent=getIntent();
        String stringUri= intent.getStringExtra("Uri");
        uri=Uri.parse(stringUri);
        if(ContextCompat.checkSelfPermission(activity_video.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity_video.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        videoView.setVideoURI(uri);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);//让视频循环播放
            }
        });
        videoView.start();
    }
}
