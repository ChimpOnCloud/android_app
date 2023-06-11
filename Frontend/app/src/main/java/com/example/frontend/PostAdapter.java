package com.example.frontend;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        // todo: get subscribe info here
        Boolean sub=true;
        if(!sub) {
            holder.mSubscribed.setEnabled(false);
            holder.mSubscribed.setText("");
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
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
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
        }

        public void bind(Post post) {
            // todo: 在Avatar中存储String的情况下，将数据绑定到ViewHolder中的控件上
            mAuthor.setText(post.getAuthor());
            mTime.setText(post.getTime());
            mTitle.setText(post.getTitle());
            mContent.setText(post.getContent());
//            if(post.getImages() != null) {
//                for (int i = 0; i < post.getImages().size(); i++) {
//                    if (post.getImages().get(i) != "") {
//                        mImages[i].setVisibility(View.VISIBLE);
//                        // todo: 类似的
//                        // mImages[i].setImageResource(post.getImages()[i]);
//                    } else {
//                        mImages[i].setVisibility(View.GONE);
//                    }
//                }
//            }
            mLocation.setText(post.getLocation());
            mTag.setText(post.getTag());
            mComment.setText(String.valueOf(post.getCommentNumber()));
            mThumbs.setText(String.valueOf(post.getThumbsupNumber()));
            mLike.setText(String.valueOf(post.getLikeNumber()));
        }
    }
}

