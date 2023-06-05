package com.example.frontend;

public class Info {
    private user Performer;
    private String description;
    private int type;
    // Type: 0为私聊回复，1为信息被点赞or回复（在动态中），2为关注用户发布动态
    public Info(user u,int t){
        Performer=u;
        description="";
        type=t;
    }

    public String getDescription() {
        return description;
    }

    public user getPerformer() {
        return Performer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPerformer(user performer) {
        Performer = performer;
    }
}
