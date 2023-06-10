package com.example.frontend;

import static com.example.frontend.Utils.AvatarUtil.getAvatar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class chatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final chat mChat;
    private final LayoutInflater inflater;
    public static final int FROM_OTHER=R.layout.detailitem;
    public static final int BY_USER=R.layout.detailitem_2;
    public static Bitmap oppoIconBitmap;
    public static Bitmap selfIconBitmap;
    public chatAdapter(Context context, chat ch, Bitmap bitmap,Bitmap b2){
        inflater=LayoutInflater.from(context);
        mChat=ch;
        oppoIconBitmap=bitmap;
        selfIconBitmap=b2;
    }
    @Override
    public int getItemViewType(int position){
        if(activity_homepage.User.getUsername().equals(mChat.getChatContent().get(position).getFrom().getUsername())) return BY_USER;
        else return FROM_OTHER;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if(viewType==BY_USER){
            return new detailViewHolder2((inflater.inflate(BY_USER, parent, false)),this);
        }
        else{
            return new detailViewHolder((inflater.inflate(FROM_OTHER, parent, false)),this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        message m=mChat.getChatContent().get(position);
        if(getItemViewType(position)==BY_USER){
            detailViewHolder2 dholder=(detailViewHolder2)holder;
            dholder.mMessage.setText(m.getMessageString());
            getAvatar(holder.itemView.getContext(),((detailViewHolder2) holder).selfImg,activity_homepage.User.getUsername());
        }
        else{
            detailViewHolder dholder=(detailViewHolder)holder;
            dholder.mMessage.setText(m.getMessageString());
            dholder.oppositeImg.setImageBitmap(oppoIconBitmap);
            dholder.opposite=mChat.getOpposite();
        }
    }

    @Override
    public int getItemCount() {
        return mChat.getChatContent().size();
    }
}

class detailViewHolder extends RecyclerView.ViewHolder{
    public ImageButton oppositeImg;
    public TextView mMessage;
    public user opposite;
    public detailViewHolder(@NonNull View itemView, chatAdapter adapter) {
        super(itemView);
        oppositeImg=itemView.findViewById(R.id.oppoIcon);
        mMessage=itemView.findViewById(R.id.oneMessage);
        opposite=null;
        oppositeImg.setOnClickListener(view -> {
            Intent intent=new Intent(itemView.getContext(),activity_userinfo.class);
            intent.putExtra("user",opposite);
            itemView.getContext().startActivity(intent);
        });
    }
}

class detailViewHolder2 extends RecyclerView.ViewHolder{
    public ImageButton selfImg;
    public TextView mMessage;
    public detailViewHolder2(@NonNull View itemView, chatAdapter adapter) {
        super(itemView);
        selfImg=itemView.findViewById(R.id.selfIcon);
        mMessage=itemView.findViewById(R.id.oneMessage);
        selfImg.setOnClickListener(view -> {
            Intent intent=new Intent(itemView.getContext(),activity_userinfo.class);
            itemView.getContext().startActivity(intent);
        });
    }
}