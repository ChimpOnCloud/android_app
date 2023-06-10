package com.example.frontend;

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

import java.util.ArrayList;

public class messageAdapter extends RecyclerView.Adapter<messageViewHolder> {
    private final ArrayList<message> messageList;
    private final LayoutInflater inflater;
    public messageAdapter(Context context, ArrayList<message> l){
        inflater=LayoutInflater.from(context);
        messageList=l;
    }
    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View mItemView=inflater.inflate(R.layout.commentitem,parent,false);
        return new messageViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position) {
        message m=messageList.get(position);
        holder.mMessage=m;
        holder.content.setText(m.getMessageString());
        holder.userName.setText(m.getFrom().getUsername());
        //todo: treat picture
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}


class messageViewHolder extends RecyclerView.ViewHolder{
    public final ImageButton userIcon;
    public final TextView userName;
    public final TextView content;
    public message mMessage;
    public messageViewHolder(@NonNull View itemView, messageAdapter adapter) {
        super(itemView);
        userIcon=itemView.findViewById(R.id.userIcon);
        userName=itemView.findViewById(R.id.userName);
        content=itemView.findViewById(R.id.content);
        userIcon.setOnClickListener(view -> {
            Intent intent=new Intent(itemView.getContext(),activity_userinfo.class);
            //todo: call another user's userinfo page
            //itemView.getContext().startActivity(intent);
        });
    }
}