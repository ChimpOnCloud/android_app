package com.example.frontend;

import static com.example.frontend.BuildDialogUtil.buildDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhotoVideoUtil {
    public static final int ALBUM_REQUEST_CODE = 0x99 ;
    public static final int REQUEST_CODE_CAPTURE_CAMERA = 0x999;
    public static final int VIDEO_REQUEST_CODE = 0x9999;
    public static final int REQUEST_CODE_CAPTURE_VIDEO = 0x99999;
    private Context context;
    private Uri uri;

    public Uri getUri() {
        return uri;
    }

    public PhotoVideoUtil(Context c){
        context=c;
    }
    public Bitmap PhotoAlbumRequest(Intent data){
        if(data==null) return null;
        uri=data.getData();
        return handleImageOnKitKat(uri);
    }
    public Bitmap PhotoCameraRequest(Intent data){
        uri = data.getData();
        if(uri == null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
                //spath :生成图片取个名字和路径包含类型
                String spath =context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ File.separator+ String.valueOf(System.currentTimeMillis()) + ".jpg";
                if(!saveImage(photo,spath)){
                    buildDialog("Error","无法保存图片！",(Activity) context);
                    return null;
                }
                File file=new File(spath);
                uri=Uri.fromFile(file);
            }
            else {
                return null;
            }
        }
        return handleImageOnKitKat(uri);
    }
    public Uri VideoAlbumRequest(Intent data){
        uri=data.getData();
        String[] filePathColumn = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String videopath = cursor.getString(columnIndex);
        Log.d("a",videopath);
        return uri;
    }
    public void VideoCameraRequest(Intent data){
        // todo
    }
    public void getImageFromAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        ((AppCompatActivity)context).startActivityForResult(albumIntent, ALBUM_REQUEST_CODE);
    }

    public void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            ((AppCompatActivity)context).startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMERA);
        }
    }

    public void getVideoFromAlbum() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        ((AppCompatActivity)context).startActivityForResult(i, VIDEO_REQUEST_CODE);
    }

    public void getVideoFromCamera(){
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.VIDEO_CAPTURE");
            ((AppCompatActivity)context).startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_VIDEO);
        }
    }

    private Boolean saveImage(Bitmap photo, String spath) {
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
    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    public Bitmap handleImageOnKitKat(Uri uri) {
        String imagePath=null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
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
        }else{
            return null;
        }
        // 根据图片路径显示图片
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        return bitmap;
    }

    public static String getRealPathFromUri(Context context, Uri uri) {
        String filePath = "";
        String scheme = uri.getScheme();
        if (scheme == null)
            filePath = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            filePath = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            if (TextUtils.isEmpty(filePath)) {
                filePath = getFilePathForNonMediaUri(context, uri);
            }
        }
        return filePath;
    }
    private static String getFilePathForNonMediaUri(Context context, Uri uri) {
        String filePath = "";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow("_data");
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
    }

    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }

    public void uploadBitmap(Bitmap bitmap,String oldUsername) {
        LoadingDialogUtil.getInstance(context).showLoadingDialog("Loading...");
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
        String requestUrl = context.getString(R.string.ipv4)+"uploadAvatar/";
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(requestBody)
                .build();
        // 发送请求
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialogUtil.getInstance(context).closeLoadingDialog();
                e.printStackTrace();
                buildDialog("Error","无法连接至服务器。。或许网络出错了？",(AppCompatActivity)context);
                // 上传失败的处理逻辑
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LoadingDialogUtil.getInstance(context).closeLoadingDialog();
                // 上传成功的处理逻辑
            }
        });
    }
}
