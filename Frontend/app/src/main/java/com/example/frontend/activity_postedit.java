package com.example.frontend;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import java.util.Random;
import java.util.StringTokenizer;

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
        tagText.setText(mPreferences.getString(tagString,""));
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
            locateButton.setText("Deactivate Location");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==ALBUM_RESULT_CODE){
            if(data==null) return;
            Uri uri=data.getData();
            handleImageOnKitKat(uri);
        }
        else if(requestCode==REQUEST_CODE_CAPTURE_CAMERA){
            Uri uri = data.getData();
            if(uri == null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
                    //spath :生成图片取个名字和路径包含类型
                    String spath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ File.separator+ String.valueOf(System.currentTimeMillis()) + ".jpg";
                    if(!saveImage(photo,spath)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder=new AlertDialog.Builder(activity_postedit.this);
                                builder.setTitle("Error");
                                builder.setMessage("图片保存失败");
                                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                AlertDialog dialog=builder.create();
                                dialog.show();
                            }
                        });
                        return;
                    }
                    File file=new File(spath);
                    uri=Uri.fromFile(file);
                }
                else {
                    Toast.makeText(getApplicationContext(), "err****", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            handleImageOnKitKat(uri);
        }
    }


    public static Boolean saveImage(Bitmap photo, String spath) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static int ALBUM_RESULT_CODE = 0x999 ;
    public static int REQUEST_CODE_CAPTURE_CAMERA = 0x99;

    private void getImageFromAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_RESULT_CODE);
    }

    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMERA);
        }
    }
    private void handleImageOnKitKat(Uri uri) {
        String imagePath=null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
        System.out.println(imagePath);
    }
    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int position=post.getImagesLength();
        if(position<=5){
            mImageView[position].setImageBitmap(bitmap);
        }
        // todo: save the images in post
    }

    public void onAddImageClicked(View v){
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
        popupMenu.getMenuInflater().inflate(R.menu.add_image,popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(post.getImagesLength()>6){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder=new AlertDialog.Builder(activity_postedit.this);
                            builder.setTitle("Info");
                            builder.setMessage("最多只能添加6张图片");
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
                    return true;
                }
                if ("拍照".equals(item.getTitle())) {
                    getImageFromCamera();
                }
                else{
                    getImageFromAlbum();
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
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.add_image,popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if ("录像".equals(item.getTitle())) {
                    // todo
                }
                else{
                    // todo
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
