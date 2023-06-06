package com.example.frontend;

import static com.example.frontend.BuildDialogUtil.buildDialog;
import static com.example.frontend.PhotoVideoUtil.ALBUM_REQUEST_CODE;
import static com.example.frontend.PhotoVideoUtil.REQUEST_CODE_CAPTURE_CAMERA;
import static com.example.frontend.PhotoVideoUtil.REQUEST_CODE_CAPTURE_VIDEO;
import static com.example.frontend.PhotoVideoUtil.VIDEO_REQUEST_CODE;
import static com.example.frontend.PhotoVideoUtil.getRealPathFromUri;
import static com.example.frontend.PhotoVideoUtil.getVideoThumb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSONObject;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.StringTokenizer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_postedit extends AppCompatActivity implements LocationListener {
    private Button confirmButton;
    private Button backButton;
    private EditText titleText;
    private EditText contentText;
    private Button addImageButton;
    private Button locateButton;
    public SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    private Boolean useLocation=false;
    private Double longitude,latitude;
    private LocationManager locationManager;
    private String mLocation="";
    private TextView tagText;
    private Post post;
    private ImageView[] mImageView=new ImageView[5];
    private static final String titleString="title";
    private static final String contentString="content";
    private static final String tagString="tag";
    private Uri VideoUri;
    private PhotoVideoUtil photoVideoUtil;
    private Boolean confirm;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_post_edit);
        confirmButton=findViewById(R.id.confirmButton);
        backButton=findViewById(R.id.backButton);
        titleText=findViewById(R.id.title);
        contentText=findViewById(R.id.content);
        addImageButton=findViewById(R.id.addImageButton);
        locateButton=findViewById(R.id.useLocate);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        tagText=findViewById(R.id.post_topic);
        post=new Post();
        mImageView[0]=findViewById(R.id.image_1);
        mImageView[1]=findViewById(R.id.image_2);
        mImageView[2]=findViewById(R.id.image_3);
        mImageView[3]=findViewById(R.id.image_4);
        mImageView[4]=findViewById(R.id.image_5);
        titleText.setText(mPreferences.getString(titleString,""));
        contentText.setText(mPreferences.getString(contentString,""));
        tagText.setText(mPreferences.getString(tagString,Post.tagList[0]));
        photoVideoUtil=new PhotoVideoUtil(this);
        confirm=false;
    }

    public void onBackClicked(View v){
        SharedPreferences.Editor editor=mPreferences.edit();
        editor.putString(titleString,titleText.getText().toString());
        editor.putString(contentString,contentText.getText().toString());
        editor.putString(tagString,tagText.getText().toString());
        editor.apply();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(confirm) return;
        SharedPreferences.Editor editor=mPreferences.edit();
        editor.putString(titleString,titleText.getText().toString());
        editor.putString(contentString,contentText.getText().toString());
        editor.putString(tagString,tagText.getText().toString());
        editor.apply();
    }
    public void onConfirmClicked(View v){
        SharedPreferences.Editor editor=mPreferences.edit();
        confirm=true;
        editor.remove(titleString);
        editor.remove(contentString);
        editor.remove(tagString);
        editor.commit();


        Intent intent=new Intent();
        post.setTitle(titleText.getText().toString());
        post.setContent(contentText.getText().toString());
        post.setTag(tagText.getText().toString());
        post.setTime();
        if(useLocation) post.setLocation(mLocation);
        String jsonStr = "{\"titleString\":\""+ titleText.getText().toString() + "\",\"contentString\":\""+contentText.getText().toString()+"\"";
        jsonStr = jsonStr + ",\"tagString\":\"" + tagText.getText().toString() + "\",\"locationString\":\"" + mLocation + "\"";
        jsonStr = jsonStr + ",\"username\":\"" + activity_homepage.User.getUsername() + "\"}";
        System.out.println(tagString);
        String requestUrl = getString(R.string.ipv4)+"postPublish/";
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

                }
            }
        });
        intent.putExtra("newPost", post);
        setResult(RESULT_OK,intent);
        finish();
    }
    private static String getProvider(LocationManager locationManager){
        //获取位置信息提供者列表
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            //获取NETWORK定位
            return LocationManager.NETWORK_PROVIDER;
        }else if (providerList.contains(LocationManager.GPS_PROVIDER)){
            //获取GPS定位
            return LocationManager.GPS_PROVIDER;
        }
        return null;
    }
    @SuppressLint("MissingPermission")
    private Location getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, activity_postedit.this);
        String provider = getProvider(locationManager);
        if (provider == null) {
            buildDialog("Error","定位失败。。没有开启位置信息？",this);
            useLocation=false;
            // Toast.makeText(activity_postedit.this, "定位失败", Toast.LENGTH_SHORT).show();
        }
        return locationManager.getLastKnownLocation(provider);
    }
    public void onLocationClicked(View v){
        // final todo: add the following part
        useLocation=!useLocation;
        if(useLocation){
            if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity_postedit.this, new String[] {Manifest.permission.INTERNET}, 100);
            }
            if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity_postedit.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
            if (ActivityCompat.checkSelfPermission(activity_postedit.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity_postedit.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
            if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (ActivityCompat.checkSelfPermission(activity_postedit.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location=getLocation();
            if(location==null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity_postedit.this);
                        builder.setTitle("Error");
                        builder.setMessage("定位失败。。可能没有开启位置信息？");
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                });
                useLocation=false;
                return;
            }
            latitude = location.getLatitude();
            // 获取当前经度
            longitude = location.getLongitude();
            // 定义位置解析
            Geocoder geocoder = new Geocoder(activity_postedit.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                Address address = addresses.get(0);
                 mLocation = address.getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            locateButton.setText("关闭定位");
        }
        else{
            locateButton.setText("开启定位");
        }
    }

    public void onTagChangeClicked(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("选择话题");
        builder.setSingleChoiceItems(Post.tagList, 0, new DialogInterface.OnClickListener() {
            @Override
            //which为你当前选中的索引，从0开始
            public void onClick(DialogInterface dialogInterface, int which) {
                tagText.setText(Post.tagList[which]);
                post.setTag(Post.tagList[which]);
                SharedPreferences.Editor editor=mPreferences.edit();
                editor.putString(tagString,tagText.getText().toString());
                editor.apply();
                System.out.println(mPreferences.getString(tagString, ""));
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null) return;
        if(requestCode== ALBUM_REQUEST_CODE){
            displayImage(photoVideoUtil.PhotoAlbumRequest(data));
        }
        else if(requestCode==REQUEST_CODE_CAPTURE_CAMERA){
            displayImage(photoVideoUtil.PhotoCameraRequest(data));
        }
        else if(requestCode==VIDEO_REQUEST_CODE){
            Intent intent=new Intent(this,activity_video.class);
            VideoUri=photoVideoUtil.VideoAlbumRequest(data);
            if(VideoUri==null) return;
            displayVideoPreview(getVideoThumb(getRealPathFromUri(this,VideoUri)));
            intent.putExtra("Uri",VideoUri.toString());
            startActivity(intent);
        }
        else if(requestCode==REQUEST_CODE_CAPTURE_VIDEO){
            // todo
            photoVideoUtil.VideoCameraRequest(data);
        }
    }
    private void displayVideoPreview(Bitmap bitmap){
        if(bitmap==null) return;
        mImageView[0].setImageBitmap(bitmap);
        // todo: save the video in the post
    }
    private void displayImage(Bitmap bitmap) {
        if(bitmap==null) return;
        int position=post.getImagesLength();
        if(position<5){
            mImageView[position].setImageBitmap(bitmap);
        }
        // todo: save the images in post
        post.setImage(photoVideoUtil.getUri().toString(),position);
    }

    public void onAddImageClicked(View v) {
        if((VideoUri!=null)){
            buildDialog("Info", "已经添加了视频！",activity_postedit.this);
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity_postedit.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity_postedit.this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.add_image, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if ((post.getImagesLength() == 6)) {
                    buildDialog("Info", "最多只能添加6张图片",activity_postedit.this);
                    return true;
                }
                if ("拍照".equals(item.getTitle())) {
                    photoVideoUtil.getImageFromCamera();
                }
                else {
                    photoVideoUtil.getImageFromAlbum();
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }

    public void onAddVideoClicked(View v){
        if(post.getImagesLength()>0){
            buildDialog("Info","已经添加了图片！",activity_postedit.this);
        }
        if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity_postedit.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity_postedit.this, new String[]{Manifest.permission.CAMERA},1);
        }
        if (ActivityCompat.checkSelfPermission(activity_postedit.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.add_video,popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if ("录像".equals(item.getTitle())) {
                    photoVideoUtil.getVideoFromCamera();
                }
                else{
                    photoVideoUtil.getVideoFromAlbum();
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }
}
