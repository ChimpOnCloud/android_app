package com.example.frontend;

import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Post implements Parcelable {
    private int avatar; // 用户头像
    private String author; // 用户名
    private String time; // 发布时间
    private String title; // 帖子标题
    private String content; // 帖子内容
    private int[] images; // 帖子图片
    private String location;
    private String tag;
    private int commentNumber;
    private int thumbsupNumber;
    private int likeNumber;
    public static final String[] tagList=new String[]{
        "#默认话题",
        "#校园资讯",
        "#二手交易",
        "#思绪随笔",
        "#吐槽盘点"};

    public Post() {
        this.avatar = R.drawable.ic_default_avatar;
        this.author = "匿名";
        this.setTime();
        this.title = "默认标题";
        this.content = "1.默认内容\n2.默认内容\n3.默认内容";
        this.images = null;
        this.location=null;
        this.tag="#默认话题";
        this.commentNumber=this.thumbsupNumber=this.likeNumber=0;
        this.images=new int[]{0,0,0,0,0,0};
    }

    protected Post(Parcel in) {
        avatar = in.readInt();
        author = in.readString();
        time = in.readString();
        title=in.readString();
        content=in.readString();
        images = in.createIntArray();
        location=in.readString();
        tag=in.readString();
        commentNumber=in.readInt();
        thumbsupNumber=in.readInt();
        likeNumber=in.readInt();
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
    public int getImagesLength(){
        int i=0;
        for(;i<6;i++){
            if(images[i]==0) break;
        }
        return i;
    }
    public void setImage(int image,int position){
        if(position>5||position<0) return;
        images[position]=image;
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
        parcel.writeString(location);
        parcel.writeString(tag);
        parcel.writeInt(commentNumber);
        parcel.writeInt(thumbsupNumber);
        parcel.writeInt(likeNumber);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public void setThumbsupNumber(int thumbsupNumber) {
        this.thumbsupNumber = thumbsupNumber;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public int getThumbsupNumber() {
        return thumbsupNumber;
    }
}
