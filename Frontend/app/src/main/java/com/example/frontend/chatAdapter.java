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

public class chatAdapter extends RecyclerView.Adapter<detailViewHolder> {
    private final chat mChat;
    private final LayoutInflater inflater;
    public chatAdapter(Context context, chat ch){
        inflater=LayoutInflater.from(context);
        mChat=ch;
    }
    @NonNull
    @Override
    public detailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View mItemView = inflater.inflate(
                R.layout.detailitem, parent, false);
        return new detailViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull detailViewHolder holder, int position) {
        message m=mChat.getChatContent().get(position);
        holder.mMessage.setText(m.getMessageString());
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