package com.example.frontend;

import static com.example.frontend.Utils.AvatarUtil.getAvatar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import org.jetbrains.annotations.NonNls;
import java.util.zip.Inflater;

public class chatListAdapter extends RecyclerView.Adapter<chatViewHolder> {
    private final chatList mChatList;
    private final LayoutInflater inflater;
    public chatListAdapter(Context context, chatList cList){
        inflater=LayoutInflater.from(context);
        mChatList=cList;
    }
    @NonNull
    @Override
    public chatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View mItemView = inflater.inflate(
                R.layout.chatitem, parent, false);
        return new chatViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull chatViewHolder holder, int position) {
        holder.lastChat.setText("");
        holder.chatHistory=mChatList.get(position);
        holder.chatName.setText(mChatList.get(position).getOpposite().getUsername());
        getAvatar(holder.itemView.getContext(),holder.userIcon,mChatList.get(position).getOpposite().getUsername());
    }

    @Override
    public int getItemCount() {
        return mChatList.getCount();
    }
}


class chatViewHolder extends RecyclerView.ViewHolder{
    public final ImageButton userIcon;
    public final TextView chatName;
    public final TextView lastChat;
    public chat chatHistory;
    public chatViewHolder(@NonNull View itemView, chatListAdapter adapter) {
        super(itemView);
        Context context= itemView.getContext();
        userIcon=itemView.findViewById(R.id.userIcon);
        chatName=itemView.findViewById(R.id.chatName);
        lastChat=itemView.findViewById(R.id.lastChat);
        userIcon.setOnClickListener(view -> {
            onChatClicked(context);
        });
        chatName.setOnClickListener(view -> {
            onChatClicked(context);
        });
        lastChat.setOnClickListener(view -> {
            onChatClicked(context);
        });
    }
    public void onChatClicked(Context context){
        Intent intent=new Intent(context, activity_chatdetail.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("chat",chatHistory);
        intent.putExtras(bundle);
        Bitmap bitmap=getBitmap(userIcon.getDrawable());
        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
        byte[] result = output.toByteArray();//转换成功了  result就是一个bit的资源数组
        intent.putExtra("bitmap",result);
        context.startActivity(intent);
    }

    private Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}