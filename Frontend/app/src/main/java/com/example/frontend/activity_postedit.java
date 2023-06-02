package com.example.frontend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class activity_postedit extends AppCompatActivity implements LocationListener {
    private Button confirmButton;
    private Button backButton;
    private EditText titleText;
    private EditText contentText;
    private Button addImageButton;
    public SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    private Boolean useLocation=false;
    private Double longitude,latitude;
    private LocationManager locationManager;
    private String mLocation="";
    private TextView tagText;
    private Post post;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_post_edit);
        confirmButton=findViewById(R.id.confirmButton);
        backButton=findViewById(R.id.backButton);
        titleText=findViewById(R.id.title);
        contentText=findViewById(R.id.content);
        addImageButton=findViewById(R.id.addImageButton);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        tagText=findViewById(R.id.post_topic);
        post=new Post();
    }

    public void onBackClicked(View v){
        setResult(RESULT_CANCELED);
        finish();
    }
    public void onConfirmClicked(View v){
        Intent intent=new Intent();
        post.setTitle(titleText.getText().toString());
        post.setContent(contentText.getText().toString());
        post.setTime();
        if(useLocation) post.setLocation(mLocation);
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
            Toast.makeText(activity_postedit.this, "定位失败", Toast.LENGTH_SHORT).show();
        }
        return locationManager.getLastKnownLocation(provider);
    }
    public void onLocationClicked(View v){
        // final todo: add the following part
        // useLocation=!useLocation;
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
            Location location=getLocation();
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
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
    }
}
