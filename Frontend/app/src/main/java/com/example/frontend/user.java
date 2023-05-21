package com.example.frontend;

public class user {
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
