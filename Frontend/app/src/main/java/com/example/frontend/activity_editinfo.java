package com.example.frontend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
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

    private String currentPhotoPath;


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

        String avatarUrl = getString(R.string.ipv4) + "getAvatar/" + oldUsername + "/";
        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_default_avatar) // 可选的默认头像
                .into(avatarImageView);

        usrnameEditText.setText(oldUsername);
        passwdEditText.setText(oldPassword);
        nicknameEditText.setText(oldNickname);
        introEditText.setText(oldIntroduction);


    }
    public void saveInfo(View v) {
        String newUsername = usrnameEditText.getText().toString();
        String newPassword = passwdEditText.getText().toString();
        String newNickname = nicknameEditText.getText().toString();
        String newIntroduction = introEditText.getText().toString();
        // TODO: 传数据给后端，改变用户信息
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
                // e.printStackTrace();
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
                }
            }
        });
        Intent intent = new Intent(this, activity_homepage.class);
        startActivity(intent);
    }

    public void uploadAvatar(View v) {
        // 创建对话框以选择相册或相机
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择图片来源");
        builder.setItems(new CharSequence[]{"相册", "相机", "取消"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    // 选择相册，打开相册选择图片
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 2);
                    break;
                case 1:
                    // 选择相机，打开相机拍摄照片
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, 1);
                    }
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

        if (resultCode == RESULT_OK) {
            if (requestCode == 2 && data != null) {
                // 从相册选择图片
                Uri imageUri = data.getData();
                // 将图片上传到后端服务器
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    uploadBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1 && data != null) {
                // 从相机拍摄照片
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                // 将照片上传到后端服务器
                uploadBitmap(bitmap);
            }
        }
    }

    private void uploadBitmap(Bitmap bitmap) {
        // 将 Bitmap 转换为字节数组或其他合适的数据格式
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();
        // 构造请求体，包含图片数据和其他参数
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", oldUsername+".jpg", RequestBody.create(MediaType.parse("image/jpeg"), imageData))
                .addFormDataPart("oldUsername", oldUsername)  // 其他参数
                .build();
        // 构造请求
        String requestUrl = getString(R.string.ipv4)+"uploadAvatar/";
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();
        // 发送请求
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 上传失败的处理逻辑
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 上传成功的处理逻辑
            }
        });
    }

}