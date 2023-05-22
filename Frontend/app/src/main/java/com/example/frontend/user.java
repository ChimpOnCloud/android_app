package com.example.frontend;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class user implements Serializable {
    private int ID;
    private String username;
    private String password;
    private String nickname;
    private String introduction;
    public user(int id,String u,String p,String n,String i){
        ID=id;
        username=u;
        password=p;
        nickname=n;
        introduction=i;
    }

    public user(){
        ID=1;
        username="testuser";
        password="password";
        nickname="nickname";
        introduction="intro";
    }

    public boolean equals(user u) {
        if(u.ID==ID && u.username==username) return true;
        return false;
    }
    public int getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getIntroduction() {
        return introduction;
    }
}
