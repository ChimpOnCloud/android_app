package com.example.frontend;

import java.util.ArrayList;

public class chatList {
    private ArrayList<chat> cList=new ArrayList<>();
    private int count=0;
    public void insert(chat c){
        cList.add(c);
        count++;
    }
    public chat get(int index){
        return cList.get(index);
    }
    public int getCount(){return count;}
}

class chat{
    private user opposite;
    private ArrayList<message> chatContent=new ArrayList<>();

    public chat(user u,ArrayList<message> c){
        opposite=u;
        chatContent=c;
    }

    public ArrayList<message> getChatContent() {
        return chatContent;
    }

    public user getOpposite() {
        return opposite;
    }
}

class message{
    private String messageString;
    private user From;

    public message(String m,user f){
        messageString=m;
        From=f;
    }

    public String getMessageString() {
        return messageString;
    }

    public user getFrom() {
        return From;
    }
}