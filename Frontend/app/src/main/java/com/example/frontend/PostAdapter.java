package com.example.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> mPosts;
    private OnItemClickListener mItemClickListener;

    public PostAdapter(List<Post> posts) {
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
                .inflate(R.layout.post_item, parent, false);
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
        private LinearLayout commentLayout;
        private LinearLayout thumbsupLayout;
        private LinearLayout likeLayout;

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

//            mTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (listener != null) {
//                        int position = getBindingAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(position,"title");
//                        }
//                    }
//                }
//            });
//            mContent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (listener != null) {
//                        int position = getBindingAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(position,"content");
//                        }
//                    }
//                }
//            });
        }

        public void bind(Post post) {
            // 将数据绑定到ViewHolder中的控件上
            mAvatar.setImageResource(post.getAvatar());
            mAuthor.setText(post.getAuthor());
            mTime.setText(post.getTime());
            mTitle.setText(post.getTitle());
            mContent.setText(post.getContent());
            if(post.getImages() != null) {
                for (int i = 0; i < post.getImages().length; i++) {
                    if (post.getImages()[i] != 0) {
                        mImages[i].setVisibility(View.VISIBLE);
                        mImages[i].setImageResource(post.getImages()[i]);
                    } else {
                        mImages[i].setVisibility(View.GONE);
                    }
                }
            }
            mLocation.setText(post.getLocation());
            mTag.setText(post.getTag());
        }
    }
}

