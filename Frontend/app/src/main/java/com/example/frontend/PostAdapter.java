package com.example.frontend;

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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
                        Log.d("a","reached here");
                        Message msg = new Message();
                        msg.obj = Objects.requireNonNull(response.body()).string();
                        String msg_obj_string = msg.obj.toString();
                        if (msg_obj_string.equals("ok")) {
                            Log.d("a","not");
                        } else if (msg_obj_string.equals("followed")){
                            Log.d("a","fol");
                            holder.sub=true;
                            holder.mSubscribed.setText("已关注");
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
            context=itemView.getContext();

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
            // todo: 在Avatar中存储String的情况下，将数据绑定到ViewHolder中的控件上
            // mAvatar.setImageResource(post.getAvatar());
            // Log.d("a",post.getAvatar());
            mAuthor.setText(post.getAuthor());
            mTime.setText(post.getTime());
            mTitle.setText(post.getTitle());
            mContent.setText(post.getContent());
            if(post.getImages() != null) {
                for (int i = 0; i < post.getImages().length; i++) {
                    if (post.getImages()[i] != "") {
                        mImages[i].setVisibility(View.VISIBLE);
                        // todo: 类似的
                        // mImages[i].setImageResource(post.getImages()[i]);
                    } else {
                        mImages[i].setVisibility(View.GONE);
                    }
                }
            }
            mLocation.setText(post.getLocation());
            mTag.setText(post.getTag());
            mComment.setText(String.valueOf(post.getCommentNumber()));
            mThumbs.setText(String.valueOf(post.getThumbsupNumber()));
            mLike.setText(String.valueOf(post.getLikeNumber()));
        }
    }
}

