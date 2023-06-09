package com.example.frontend.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// usually needed: buildDialog("Error","无法连接至服务器。。或许网络出错了？",this);
public class BuildDialogUtil {
    public static final void buildDialog(String title, String message, Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });
    }
}
