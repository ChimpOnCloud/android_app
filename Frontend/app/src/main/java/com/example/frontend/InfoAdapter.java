package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TintableCheckedTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoAdapter extends RecyclerView.Adapter<InfoViewHolder> {
    public final ArrayList<Info> mInfoList;
    public final LayoutInflater inflater;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    public InfoAdapter(Context context,ArrayList<Info> list){
        mInfoList=list;
        inflater=LayoutInflater.from(context);
        mPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView=inflater.inflate(R.layout.infoitem,parent,false);
        return new InfoViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        holder.mInfo=mInfoList.get(position);
        holder.userName.setText(holder.mInfo.getPerformer().getUsername());
        holder.descriptionButton.setText(holder.mInfo.getDescription());
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }
}

class InfoViewHolder extends RecyclerView.ViewHolder{
    public final Button descriptionButton;
    public final TextView userName;
    public ImageButton userIconButton;
    public Info mInfo;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    public InfoViewHolder(@NonNull View itemView,InfoAdapter adapter){
        super(itemView);
        Context context=itemView.getContext();
        userName=itemView.findViewById(R.id.userName);
        userIconButton=itemView.findViewById(R.id.userIcon);
        descriptionButton = itemView.findViewById(R.id.description);
        mPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        // todo: post here

        userIconButton.setOnClickListener(view ->  {
            Intent intent=new Intent(context,activity_userinfo.class);
            intent.putExtra("user",mInfo.getPerformer());
            context.startActivity(intent);
        });
        descriptionButton.setOnClickListener(view -> {
            user target=mInfo.getPerformer();
            if(mInfo.getType()==0){
                Intent intent=new Intent(context,activity_chatdetail.class);
                intent.putExtra("chat",new chat(target,new ArrayList<>()));
                context.startActivity(intent);
            }
            else{
                // todo: wait for pyq view
            }
        });
    }
}