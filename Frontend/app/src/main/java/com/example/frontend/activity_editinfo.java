package com.example.frontend;

import static com.example.frontend.Utils.AvatarUtil.getAvatar;
import static com.example.frontend.Utils.BuildDialogUtil.buildDialog;
import static com.example.frontend.Utils.PhotoVideoUtil.ALBUM_REQUEST_CODE;
import static com.example.frontend.Utils.PhotoVideoUtil.REQUEST_CODE_CAPTURE_CAMERA;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.frontend.Utils.LoadingDialogUtil;
import com.example.frontend.Utils.PhotoVideoUtil;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_editinfo extends AppCompatActivity {
    String oldUsername = "";
    String oldPassword = "";
    String oldNickname = "";
    String oldIntroduction = "";
    EditText usrnameEditText;
    EditText passwdEditText;
    EditText nicknameEditText;
    EditText introEditText;
    ImageView avatarImageView;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    private PhotoVideoUtil photoVideoUtil;
    @Override
    protected void onStart() {
        super.onStart();
        getAvatar(this,avatarImageView,activity_homepage.User.getUsername());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editinfo);
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        oldUsername = mPreferences.getString("username", oldUsername);
        oldPassword = mPreferences.getString("password", oldPassword);
        oldNickname = mPreferences.getString("nickname", oldNickname);
        oldIntroduction = mPreferences.getString("introduction", oldIntroduction);

        usrnameEditText = findViewById(R.id.textView_usrname_content);
        passwdEditText = findViewById(R.id.textView_password_content);
        nicknameEditText = findViewById(R.id.textView_nickname_content);
        introEditText = findViewById(R.id.textView_introduction_content);
        avatarImageView = findViewById(R.id.imageView_avatar);

        usrnameEditText.setText(oldUsername);
        passwdEditText.setText(oldPassword);
        nicknameEditText.setText(oldNickname);
        introEditText.setText(oldIntroduction);
        photoVideoUtil=new PhotoVideoUtil(this);
    }
    public void saveInfo(View v) {
        String newUsername = usrnameEditText.getText().toString();
        String newPassword = passwdEditText.getText().toString();
        String newNickname = nicknameEditText.getText().toString();
        String newIntroduction = introEditText.getText().toString();
        LoadingDialogUtil.getInstance(this).showLoadingDialog("Loading...");
        String jsonStr = "{\"newUsername\":\""+ newUsername + "\",\"newPassword\":\""+newPassword+"\"";
        jsonStr = jsonStr + ",\"newNickname\":\"" + newNickname + "\",\"newIntroduction\":\"" + newIntroduction + "\"";
        jsonStr = jsonStr + ",\"oldUsername\":\"" + oldUsername + "\",\"oldPassword\":\"" + oldPassword + "\"";
        jsonStr = jsonStr + ",\"oldNickname\":\"" + oldNickname + "\",\"oldIntroduction\":\"" + oldIntroduction + "\"}";
        System.out.println(jsonStr);
        String requestUrl = getString(R.string.ipv4)+"changeUserinfo/";
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
                LoadingDialogUtil.getInstance(activity_editinfo.this).closeLoadingDialog();
                buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_editinfo.this);
            }

            @Override
            public void onResponse(Call call, final Response response)
                    throws IOException {
                Message msg = new Message();
                msg.obj = Objects.requireNonNull(response.body()).string();
                String msg_obj_string = msg.obj.toString(); // ok
                if (msg_obj_string.equals("ok")) {
                    System.out.println("successfully changed userinfo");
                    SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                    preferencesEditor.putString("username", newUsername);
                    preferencesEditor.putString("password", newPassword);
                    preferencesEditor.putString("nickname", newNickname);
                    preferencesEditor.putString("introduction", newIntroduction);
                    preferencesEditor.apply();
                } else if (msg_obj_string.equals("repeated username!")) {
                    buildDialog("Error","该用户名已被占用！",activity_editinfo.this);
                    System.out.println("already have this username!");
                }
                LoadingDialogUtil.getInstance(activity_editinfo.this).closeLoadingDialog();
            }
        });
        // change mpreferences
        // System.out.println("shit" + mPreferences.getString("username", newUsername));
        Intent intent = new Intent(this, activity_homepage.class);
        startActivity(intent);
    }

    public void uploadAvatar(View v) {
        if (ActivityCompat.checkSelfPermission(activity_editinfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity_editinfo.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ActivityCompat.checkSelfPermission(activity_editinfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity_editinfo.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity_editinfo.this, new String[]{Manifest.permission.CAMERA},1);
        }
        if (ActivityCompat.checkSelfPermission(activity_editinfo.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // 创建对话框以选择相册或相机
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择图片来源");
        builder.setItems(new CharSequence[]{"相册", "相机", "取消"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    // 选择相册，打开相册选择图片
                    photoVideoUtil.getImageFromAlbum();
                    break;
                case 1:
                    // 选择相机，打开相机拍摄照片
                    photoVideoUtil.getImageFromCamera();
                    break;
                case 2:
                    // 取消，不执行任何操作
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null) return;
        if (requestCode == ALBUM_REQUEST_CODE) {
            Bitmap bitmap= photoVideoUtil.PhotoAlbumRequest(data);
            photoVideoUtil.uploadBitmap(bitmap,oldUsername);
            avatarImageView.setImageBitmap(bitmap);
        }
        else if (requestCode == REQUEST_CODE_CAPTURE_CAMERA) {
            // 从相机拍摄照片
            Bitmap bitmap = photoVideoUtil.PhotoCameraRequest(data);
            photoVideoUtil.uploadBitmap(bitmap,oldUsername);
            avatarImageView.setImageBitmap(bitmap);
        }
        getAvatar(this,avatarImageView,activity_homepage.User.getUsername());
    }
}