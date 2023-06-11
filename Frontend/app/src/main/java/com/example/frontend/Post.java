package com.example.frontend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Post implements Parcelable {
    private String avatar; // 用户头像
    private String author; // 用户名
    private String time; // 发布时间
    private String title; // 帖子标题
    private String content; // 帖子内容
    private String[] images; // 帖子图片
    private String location;
    private String tag;
    private int commentNumber = 0;
    private int thumbsupNumber = 0;
    private int likeNumber = 0;
    private ArrayList<message> comments = new ArrayList<>();
    private String id;
    public static final String[] tagList=new String[]{
        "#默认话题",
        "#校园资讯",
        "#二手交易",
        "#思绪随笔",
        "#吐槽盘点"};

    public Post() {
        this.avatar="";
        this.author = "匿名";
        this.setTime();
        this.title = "默认标题";
        this.content = "1.默认内容\n2.默认内容\n3.默认内容";
        this.location="";
        this.tag="#默认话题";
        this.commentNumber=this.thumbsupNumber=this.likeNumber=0;
        this.images=new String[]{"","","","","",""};
        this.id = "-1";
    }
    public Post(String mAvatar, String mAuthor, String mTime, String mTitle, String mContent, String mTag, String id, int thumbsupNumber, int likeNumber, int commentNumber) {
        this.avatar = mAvatar;
        this.author = mAuthor;
        this.time = mTime;
        this.title = mTitle;
        this.content = mContent;
        this.tag = mTag;
        this.images = new String[]{"", "", "", "", "", ""};
        this.id = id;
        this.thumbsupNumber = thumbsupNumber;
        this.likeNumber = likeNumber;
        this.commentNumber = commentNumber;
    }

    protected Post(Parcel in) {
        avatar = in.readString();
        author = in.readString();
        time = in.readString();
        title=in.readString();
        content=in.readString();
        images=new String[6];
        images[0] = in.readString();
        images[1] = in.readString();
        images[2] = in.readString();
        images[3] = in.readString();
        images[4] = in.readString();
        images[5] = in.readString();
        location=in.readString();
        tag=in.readString();
        commentNumber=in.readInt();
        thumbsupNumber=in.readInt();
        likeNumber=in.readInt();
        id=in.readString();
        comments= (ArrayList<message>) in.readSerializable();
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
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

    public String[] getImages() {
        return images;
    }
    public int getImagesLength(){
        int i=0;
        for(;i<6;i++){
            if(images[i]=="") break;
        }
        return i;
    }
    public void setImage(String image,int position){
        if(position>5||position<0) return;
        images[position]=image;
    }
    public void setImages(String[] images) {
        this.images = images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(avatar);
        parcel.writeString(author);
        parcel.writeString(time);
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeString(images[0]);
        parcel.writeString(images[1]);
        parcel.writeString(images[2]);
        parcel.writeString(images[3]);
        parcel.writeString(images[4]);
        parcel.writeString(images[5]);
        parcel.writeString(location);
        parcel.writeString(tag);
        parcel.writeInt(commentNumber);
        parcel.writeInt(thumbsupNumber);
        parcel.writeInt(likeNumber);
        parcel.writeString(id);
        parcel.writeSerializable(comments);
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
    public String getID() {return id;}

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public void setThumbsupNumber(int thumbsupNumber) {
        this.thumbsupNumber = thumbsupNumber;
    }
    public void setComments(ArrayList<message> comments) {
        this.comments = comments;
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
    public ArrayList<message> getComments(){
        return comments;
    }
    public static class PosttComparatorTime implements Comparator<Post> {
        @Override
        public int compare(Post obj1, Post obj2) {
            return obj1.getTime().compareTo(obj2.getTime());
        }
    }
    public static class PostComparatorThumbsup implements Comparator<Post> {
        @Override
        public int compare(Post obj1, Post obj2) {
            // 获取 obj1 和 obj2 的 attr 属性值
            int attr1 = obj1.getThumbsupNumber();
            int attr2 = obj2.getThumbsupNumber();

            // 比较 attr 属性值并返回比较结果
//            return Integer.compare(attr1, attr2);
            if (attr1 == attr2) return 0;
            else if (attr1 < attr2) return 1;
            else return -1;
        }
    }
}
