package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class userAdapter extends RecyclerView.Adapter<userViewHolder> {
    public final ArrayList<user> mUserList;
    public final LayoutInflater inflater;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";

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
        for (int i = 0; i < mUserList.size(); i++) {
            holder.mUser = mUserList.get(position);
            holder.userNameTextview.setText(holder.mUser.getUsername());
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}

class userViewHolder extends RecyclerView.ViewHolder{
    public final TextView userNameTextview;
    public final ImageButton userIconButton;
    public final Button followButton;
    public user mUser;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.frontend";
    public userViewHolder(@NonNull View itemView,userAdapter adapter){
        super(itemView);
        Context context=itemView.getContext();
        userNameTextview=itemView.findViewById(R.id.userName);
        userIconButton=itemView.findViewById(R.id.userIcon);
        followButton = itemView.findViewById(R.id.followButton);
        mPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        userIconButton.setOnClickListener(view ->  {
            Intent intent=new Intent(context,activity_userinfo.class);
            intent.putExtra("user",mUser);
            context.startActivity(intent);
        });
        userNameTextview.setOnClickListener(view ->  {
            Intent intent=new Intent(context,activity_userinfo.class);
            intent.putExtra("user",mUser);
            context.startActivity(intent);
        });
        followButton.setOnClickListener(view -> {
            String followText = followButton.getText().toString();
            if (followText.equals("follow")) { // not followed, now follow
                String srcusername = "";
                srcusername = mPreferences.getString("username", srcusername);
                String jsonStr = "{\"dstusername\":\""+ mUser.getUsername() + "\"";
                jsonStr = jsonStr + ",\"srcusername\":\"" + srcusername + "\"}";
                String requestUrl = context.getString(R.string.ipv4)+"handleFollowuser/";
                System.out.println(jsonStr);
                System.out.println(requestUrl);
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                @SuppressWarnings("deprecation")
                RequestBody body = RequestBody.create(JSON, jsonStr);
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response)
                            throws IOException {
                        Message msg = new Message();
                        msg.obj = Objects.requireNonNull(response.body()).string();
                        String msg_obj_string = msg.obj.toString();
                        if (msg_obj_string.equals("ok")) {

                        } else {

                        }
                    }
                });
            }
            else if (followText == "unfollow") { // follow, cancel it.

            }
        });
    }
}