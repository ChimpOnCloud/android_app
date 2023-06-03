package com.example.frontend;

import android.content.Context;
import android.content.Intent;
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
        ArrayList<message> History=mChatList.get(position).getChatContent();
        if(History.size()!=0) holder.lastChat.setText(History.get(History.size() - 1).getMessageString());
        else holder.lastChat.setText("");
        holder.chatHistory=mChatList.get(position);
        holder.chatName.setText(mChatList.get(position).getOpposite().getUsername());
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
            Intent intent=new Intent(context, activity_chatdetail.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("chat",chatHistory);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
        chatName.setOnClickListener(view -> {
            Intent intent=new Intent(context, activity_chatdetail.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("chat",chatHistory);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
        lastChat.setOnClickListener(view -> {
            Intent intent=new Intent(context, activity_chatdetail.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("chat",chatHistory);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }
}