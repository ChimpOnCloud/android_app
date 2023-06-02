package com.example.frontend;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post implements Parcelable {
    private int avatar; // 用户头像
    private String author; // 用户名
    private String time; // 发布时间
    private String title; // 帖子标题
    private String content; // 帖子内容
    private int[] images; // 帖子图片

    public Post() {
        this.avatar = R.drawable.ic_default_avatar;
        this.author = "匿名";
        this.setTime();
        this.title = "默认标题";
        this.content = "1.默认内容\n2.默认内容\n3.默认内容";
        this.images = null;
    }

    protected Post(Parcel in) {
        avatar = in.readInt();
        author = in.readString();
        time = in.readString();
        title=in.readString();
        content=in.readString();
        images = in.createIntArray();
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

    public String getTime() {
        return time;
    }

    public void setTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        this.time= simpleDateFormat.format(date);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(avatar);
        parcel.writeString(author);
        parcel.writeString(time);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeIntArray(images);
    }
}
