package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.widget.TintableCheckedTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class userAdapter extends RecyclerView.Adapter<userViewHolder> {
    private final ArrayList<user> mUserList;
    private final LayoutInflater inflater;

    public userAdapter(Context context,ArrayList<user> list){
        mUserList=list;
        inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView=inflater.inflate(R.layout.useritem,parent,false);
        return new userViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, int position) {
        holder.mUser=mUserList.get(position);
        // todo: set username and usericon
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}

class userViewHolder extends RecyclerView.ViewHolder{
    public final Button userNameButton;
    public final ImageButton userIconButton;
    public user mUser;
    public userViewHolder(@NonNull View itemView,userAdapter adapter){
        super(itemView);
        Context context=itemView.getContext();
        userNameButton=itemView.findViewById(R.id.userName);
        userIconButton=itemView.findViewById(R.id.userIcon);
        userIconButton.setOnClickListener(view ->  {
            Intent intent=new Intent(context,activity_userinfo.class);
            intent.putExtra("user",mUser);
            context.startActivity(intent);
        });
        userNameButton.setOnClickListener(view ->  {
            Intent intent=new Intent(context,activity_userinfo.class);
            intent.putExtra("user",mUser);
            context.startActivity(intent);
        });
    }
}