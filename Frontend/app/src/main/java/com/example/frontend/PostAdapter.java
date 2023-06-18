package com.example.frontend;

import static com.example.frontend.Utils.BuildDialogUtil.buildDialog;

import android.app.Activity;
import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.example.frontend.Utils.LoadingDialogUtil;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import okhttp3.OkHttpClient;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    ArrayList<Post> mPosts;
    private OnItemClickListener mItemClickListener;

    public PostAdapter(ArrayList<Post> posts) {
        mPosts = posts;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String viewType);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.postitem, parent, false);
        return new PostViewHolder(view, mItemClickListener);
    }

    @Override
    public int getItemCount() {
        if (mPosts == null) {
            return 0;
        }
        return mPosts.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        if (mPosts == null || mPosts.size() <= position) {
            return;
        }
        holder.bind(mPosts.get(position));
        Post p = mPosts.get(position);
        for (int i = 0; i < p.getImagesLength(); i++) {
            String imgUrl = holder.context.getString(R.string.ipv4) + p.getImages().get(i);
            System.out.println(imgUrl);
            Picasso tp = new Picasso.Builder(holder.context).downloader(new OkHttp3Downloader(new OkHttpClient())).build();
            tp.load(imgUrl)
                    .placeholder(R.drawable.ic_default_avatar)
                    .fit()
                    .centerCrop() // 可选，如果需要将图像裁剪为正方形
                    .into(holder.mImages[i]);
        }
        for (int i = 0; i < p.getImages().size(); i++) {
            if (p.getImages().get(i) != "") {
                holder.mImages[i].setVisibility(View.VISIBLE);
                // todo: 类似的
                // mImages[i].setImageResource(post.getImages()[i]);
            } else {
                holder.mImages[i].setVisibility(View.GONE);
            }
        }
        if (p.getImages().size() == 0) {
            for (int i = 0; i < 6; i++) {
                ViewGroup.LayoutParams layoutParams = holder.mImages[i].getLayoutParams();
                layoutParams.height = 0;
                holder.mImages[i].setLayoutParams(layoutParams);
            }
        } else if (p.getImages().size() >= 1 && p.getImages().size() <= 3) {
            for (int i = 0; i < 2; i++) {
                ViewGroup.LayoutParams layoutParams = holder.mImages[i].getLayoutParams();
                layoutParams.height = 240;
                holder.mImages[i].setLayoutParams(layoutParams);
            }
            for (int i = 3; i < 6; i++) {
                ViewGroup.LayoutParams layoutParams = holder.mImages[i].getLayoutParams();
                layoutParams.height = 0;
                holder.mImages[i].setLayoutParams(layoutParams);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                ViewGroup.LayoutParams layoutParams = holder.mImages[i].getLayoutParams();
                layoutParams.height = 240;
                holder.mImages[i].setLayoutParams(layoutParams);
            }
        }
//        for (int i = 3; i < 6; i++) {
//            ViewGroup.LayoutParams layoutParams = holder.mImages[i].getLayoutParams();
//            layoutParams.height = 0;
//            holder.mImages[i].setLayoutParams(layoutParams);
//        }
        // todo: get subscribe info here
        holder.handler.post(new Runnable() {
            @Override
            public void run() { // on create, judge if this user was already followed
                String mSrcusername = mPosts.get(position).getAuthor();
                String mJsonStr = "{\"dstusername\":\""+ mSrcusername + "\"";
                mJsonStr = mJsonStr + ",\"srcusername\":\"" + activity_homepage.User.getUsername() + "\"}";
                String mRequestUrl = holder.itemView.getContext().getString(R.string.ipv4)+"getFollowuser/";
                OkHttpClient mclient = new OkHttpClient();
                MediaType mJSON = MediaType.parse("application/json; charset=utf-8");
                @SuppressWarnings("deprecation")
                RequestBody mbody = RequestBody.create(mJSON, mJsonStr);
                Request mrequest = new Request.Builder()
                        .url(mRequestUrl)
                        .post(mbody)
                        .build();
                mclient.newCall(mrequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("failed");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response)
                            throws IOException {
//                        Log.d("a","reached here");
                        Message msg = new Message();
                        msg.obj = Objects.requireNonNull(response.body()).string();
                        String msg_obj_string = msg.obj.toString();
                        if (msg_obj_string.equals("ok")) {
                            holder.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.mSubscribed.setText("");
                                }
                            });
//                            Log.d("a","not");
                        } else if (msg_obj_string.equals("followed")){
//                            Log.d("a","fol");
                            holder.sub=true;
                            holder.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.mSubscribed.setText("已关注");
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        public final Handler handler = new Handler();
        private ImageView mAvatar;
        private TextView mAuthor;
        private TextView mTime;
        private TextView mTitle;
        private TextView mContent;
        private ImageView[] mImages= new ImageView[6];
        private TextView mSubscribed;
        private TextView mLocation;
        private TextView mTag;
        private TextView mComment;
        private TextView mThumbs;
        private TextView mLike;
        private Context context;
        private Boolean sub;
        public Button blockButton;

        public PostViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.post_avatar);
            mAuthor = itemView.findViewById(R.id.post_author);
            mTime = itemView.findViewById(R.id.post_time);
            mTitle = itemView.findViewById(R.id.post_title);
            mContent = itemView.findViewById(R.id.post_content);
            mLocation=itemView.findViewById(R.id.post_location);
            mTag=itemView.findViewById(R.id.post_topic);
            mImages[0] = itemView.findViewById(R.id.post_image_1);
            mImages[1] = itemView.findViewById(R.id.post_image_2);
            mImages[2] = itemView.findViewById(R.id.post_image_3);
            mImages[3] = itemView.findViewById(R.id.post_image_4);
            mImages[4] = itemView.findViewById(R.id.post_image_5);
            mImages[5] = itemView.findViewById(R.id.post_image_6);
            mSubscribed=itemView.findViewById(R.id.subscribe);
            mComment=itemView.findViewById(R.id.commentNumber);
            mThumbs=itemView.findViewById(R.id.thumbsNumber);
            mLike=itemView.findViewById(R.id.likeNumber);
            blockButton = itemView.findViewById(R.id.post_block);
            context=itemView.getContext();
            blockButton.setOnClickListener(view -> {
                String isBlock = "true";
                int position = getAdapterPosition();
                if (blockButton.getText().toString().equals("屏蔽此用户")) {
                    String blockUsername = mPosts.get(position).getAuthor();
                    if (!activity_homepage.blockUsernames.contains(blockUsername)) {
                        activity_homepage.blockUsernames.add(blockUsername);
                    }
                    isBlock = "true";
                    System.out.println(activity_homepage.blockUsernames);
//                    String JsonStr = "{\"blockUsername\":\""+ blockUsername + "\"}";
                    String JsonStr = "{\"blockUsername\":\""+ blockUsername + "\",\"isBlock\":\""+ isBlock +"\"}";
                    System.out.println(JsonStr);
                    String requestUrl = context.getString(R.string.ipv4)+"block/";
                    OkHttpClient client = new OkHttpClient();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    @SuppressWarnings("deprecation")
                    RequestBody body = RequestBody.create(JSON, JsonStr);
                    Request request = new Request.Builder()
                            .url(requestUrl)
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            System.out.println("failed");
//                        LoadingDialogUtil.getInstance(activity_homepage.this).closeLoadingDialog();
//                        buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_homepage.this);
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, final Response response)
                                throws IOException {
                            Message msg = new Message();
                            msg.obj = Objects.requireNonNull(response.body()).string();
                            String msg_obj_string = msg.obj.toString();
                            if (msg_obj_string.equals("ok")) {

                            }
                        }

                    });
                    for (Post p : mPosts) {
                        if (p.getAuthor().equals(mPosts.get(position).getAuthor())) {
                            blockButton.setText("解除屏蔽");
                            blockButton.setBackgroundColor(Color.RED);
                        }
                    }
                } else {
                    String blockUsername = mPosts.get(position).getAuthor();
                    if (activity_homepage.blockUsernames.contains(blockUsername)) {
                        activity_homepage.blockUsernames.remove(blockUsername);
                    }
                    isBlock = "false";
                    System.out.println(activity_homepage.blockUsernames);
//                    String JsonStr = "{\"blockUsername\":\""+ blockUsername + "\"}";
                    String JsonStr = "{\"blockUsername\":\""+ blockUsername + "\",\"isBlock\":\""+ isBlock +"\"}";
                    System.out.println(JsonStr);
                    String requestUrl = context.getString(R.string.ipv4)+"block/";
                    OkHttpClient client = new OkHttpClient();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    @SuppressWarnings("deprecation")
                    RequestBody body = RequestBody.create(JSON, JsonStr);
                    Request request = new Request.Builder()
                            .url(requestUrl)
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            System.out.println("failed");
//                        LoadingDialogUtil.getInstance(activity_homepage.this).closeLoadingDialog();
//                        buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_homepage.this);
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, final Response response)
                                throws IOException {
                            Message msg = new Message();
                            msg.obj = Objects.requireNonNull(response.body()).string();
                            String msg_obj_string = msg.obj.toString();
                            if (msg_obj_string.equals("ok")) {

                            }
                        }

                    });
                    for (Post p : mPosts) {
                        if (p.getAuthor().equals(mPosts.get(position).getAuthor())) {
                            blockButton.setText("屏蔽此用户");
                            blockButton.setBackgroundColor(Color.RED);
                        }
                    }
                }
//                Intent intent=new Intent(context, activity_postinfo.class);
//                intent.putExtra("post", mPosts.get(position));
//                context.startActivity(intent);
//                String blockUsername = mPosts.get(position).getAuthor();
//                activity_homepage.blockUsernames.add(blockUsername);
//                System.out.println(activity_homepage.blockUsernames);
//                String JsonStr = "{\"blockUsername\":\""+ blockUsername + "\"}";
//                System.out.println(JsonStr);
//                String requestUrl = context.getString(R.string.ipv4)+"block/";
//                OkHttpClient client = new OkHttpClient();
//                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                @SuppressWarnings("deprecation")
//                RequestBody body = RequestBody.create(JSON, JsonStr);
//                Request request = new Request.Builder()
//                        .url(requestUrl)
//                        .post(body)
//                        .build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        System.out.println("failed");
////                        LoadingDialogUtil.getInstance(activity_homepage.this).closeLoadingDialog();
////                        buildDialog("Error","无法连接至服务器。。或许网络出错了？",activity_homepage.this);
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(Call call, final Response response)
//                            throws IOException {
//                        Message msg = new Message();
//                        msg.obj = Objects.requireNonNull(response.body()).string();
//                        String msg_obj_string = msg.obj.toString();
//                        if (msg_obj_string.equals("ok")) {
//
//                        }
//                    }
//
//                });
            });

            mTitle.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Intent intent=new Intent(context, activity_postinfo.class);
                intent.putExtra("post", mPosts.get(position));
                context.startActivity(intent);
            });
            mContent.setOnClickListener(view -> {
                int position = getAdapterPosition();
                Intent intent=new Intent(context, activity_postinfo.class);
                intent.putExtra("post", mPosts.get(position));
                // System.out.println("before" + mPosts.get(position).getID());
                context.startActivity(intent);
            });
            sub=false;
        }

        public void bind(Post post) {
            String avatarUrl = context.getString(R.string.ipv4) + "getAvatar/" + post.getAuthor();

            Picasso p = new Picasso.Builder(context).downloader(new OkHttp3Downloader(new OkHttpClient())).build();
            p.load(avatarUrl)
                    .placeholder(R.drawable.ic_default_avatar)
                    .fit()
                    .centerCrop() // 可选，如果需要将图像裁剪为正方形
                    .into(mAvatar);
            // todo: 在Avatar中存储String的情况下，将数据绑定到ViewHolder中的控件上
            mAuthor.setText(post.getAuthor());
            mTime.setText(post.getTime());
            mTitle.setText(post.getTitle());
            mContent.setText(post.getContent());
            mLocation.setText(post.getLocation());
            mTag.setText(Post.tagList[Integer.parseInt(post.getTag())]);
            mComment.setText(String.valueOf(post.getCommentNumber()));
            mThumbs.setText(String.valueOf(post.getThumbsupNumber()));
            mLike.setText(String.valueOf(post.getLikeNumber()));
        }
    }
}

