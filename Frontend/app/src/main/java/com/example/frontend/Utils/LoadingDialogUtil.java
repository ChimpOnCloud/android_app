package com.example.frontend.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.frontend.R;

// LoadingDialogUtil.getInstance(this).showLoadingDialog("Loading..."); 开启加载动画
// LoadingDialogUtil.getInstance(this).closeLoadingDialog(); 关闭动画
public class LoadingDialogUtil {
    public static LoadingDialogUtil mInstance;
    public static final int BUTTON_OK = 0;
    public static final int BUTTON_CANCEL = 1;
    private static AlertDialog dlg;
    private static AlertDialog loginDlg;
    private static Animation operatingAnim;
    private static ImageView imageView;
    private static boolean isLoading;
    private static boolean loading=false;
    private Context context;
    private LoadingDialogUtil(Context c) {
        context=c;
    }
    public void showLoadingDialog(String msg) {
        if(null == context || ((Activity)context).isFinishing())return;
        if(isLoading())return;
        dlg = new AlertDialog.Builder(context, R.style.dialogStyle).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView text = view.findViewById(R.id.dialog_loading_text);
        imageView = view.findViewById(R.id.dialog_loading_img);
        text.setText(msg);
        Window window = dlg.getWindow();
        if(null != window){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
        dlg.show();
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(view);

        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.loading);
        operatingAnim.setInterpolator(new LinearInterpolator());
        openAnim();
        isLoading = true;
    }


    public void showLoadingDialog() {
        if(isLoading()|| null == context)return;
        dlg = new AlertDialog.Builder(context, R.style.dialogStyle).create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView text = view.findViewById(R.id.dialog_loading_text);
        imageView = view.findViewById(R.id.dialog_loading_img);
        text.setVisibility(View.GONE);
        Window window = dlg.getWindow();
        if(null != window){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
        dlg.show();
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        dlg.setContentView(view);

        operatingAnim = AnimationUtils.loadAnimation(context, R.anim.loading);
        operatingAnim.setInterpolator(new LinearInterpolator());
        openAnim();
        isLoading = true;
        view.bringToFront();
    }

    /**
     * 关闭拨打电话返回信息对话框
     */
    public void closeCallMsgDialog() {
        if (dlg != null) {
            dlg.dismiss();
        }
    }

    /**
     * 加载状态
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 开始旋转
     */
    public void openAnim() {
        if (operatingAnim != null) {
            imageView.startAnimation(operatingAnim);
        }
    }

    /**
     * 停止旋转
     */
    public void closeAnim() {
        if (operatingAnim != null) {
            imageView.clearAnimation();
        }
    }

    /**
     * 关闭请求对话框
     */
    public void closeLoadingDialog() {
        if (dlg != null) {
            closeAnim();
            try {
                dlg.dismiss();
            }catch (Throwable ignored){
            }
        }
        isLoading = false;
    }

    public interface PressCallBack {
        void onPressButton(int buttonIndex);
    }

    public static LoadingDialogUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LoadingDialogUtil.class) {
                if (mInstance == null) {
                    mInstance = new LoadingDialogUtil(context);
                    return mInstance;
                }
            }
        }
        return mInstance;
    }
}