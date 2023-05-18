package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
        holder.chatName.setText(mChatList.get(position).getOpposite().getNickname());
    }

    @Override
    public int getItemCount() {
        return mChatList.getCount();
    }
}


class chatViewHolder extends RecyclerView.ViewHolder{
    public final ImageButton userIcon;
    public final Button chatName;
    public final Button lastChat;
    public chat chatHistory;
    public chatViewHolder(@NonNull View itemView, chatListAdapter adapter) {
        super(itemView);
        Context context= itemView.getContext();
        userIcon=itemView.findViewById(R.id.userIcon);
        chatName=itemView.findViewById(R.id.chatName);
        lastChat=itemView.findViewById(R.id.lastChat);
        userIcon.setOnClickListener(view -> {
            Intent intent=new Intent(context, activity_chatdetail.class);
            context.startActivity(intent);
        });
        chatName.setOnClickListener(view -> {
            Intent intent=new Intent(context, activity_chatdetail.class);
            context.startActivity(intent);
        });
        lastChat.setOnClickListener(view -> {
            Intent intent=new Intent(context, activity_chatdetail.class);
            context.startActivity(intent);
        });
    }
}