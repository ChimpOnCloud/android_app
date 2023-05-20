package com.example.frontend;

import android.content.Context;
import android.content.Intent;
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
    public chatAdapter(Context context, chat ch){
        inflater=LayoutInflater.from(context);
        mChat=ch;
    }
    @Override
    public int getItemViewType(int position){
        if(mChat.getChatContent().get(position).getFrom().equals(activity_homepage.User)) return BY_USER;
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
        }
        else{
            detailViewHolder dholder=(detailViewHolder)holder;
            dholder.mMessage.setText(m.getMessageString());
        }
        //todo: add a picture transport
    }

    @Override
    public int getItemCount() {
        return mChat.getChatContent().size();
    }
}


class detailViewHolder extends RecyclerView.ViewHolder{
    public final ImageButton oppositeImg;
    public final TextView mMessage;
    public detailViewHolder(@NonNull View itemView, chatAdapter adapter) {
        super(itemView);
        oppositeImg=itemView.findViewById(R.id.oppoIcon);
        mMessage=itemView.findViewById(R.id.oneMessage);
        oppositeImg.setOnClickListener(view -> {
            Intent intent=new Intent(itemView.getContext(),activity_userinfo.class);
            //todo: call another user's userinfo page
            //itemView.getContext().startActivity(intent);
        });
    }
}

class detailViewHolder2 extends RecyclerView.ViewHolder{
    public final ImageButton selfImg;
    public final TextView mMessage;
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