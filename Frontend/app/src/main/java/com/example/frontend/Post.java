package com.example.frontend;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
    private int avatar; // 用户头像
    private String author; // 用户名
    private long time; // 发布时间
    private String title; // 帖子标题
    private String content; // 帖子内容
    private int[] images; // 帖子图片

    public Post() {
        this.avatar = R.drawable.ic_default_avatar;
        this.author = "匿名";
        this.time = System.currentTimeMillis();
        this.title = "默认标题";
        this.content = "1.默认内容\n2.默认内容\n3.默认内容";
        this.images = null;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int[] getImages() {
        return images;
    }

    public void setImages(int[] images) {
        this.images = images;
    }


    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    protected Post(Parcel in) {
        // 从 Parcel 中读取数据并赋值给对象
        title = in.readString();
        author = in.readString();
        content = in.readString();
        time = in.readLong();
        avatar = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeLong(time);
        dest.writeInt(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
