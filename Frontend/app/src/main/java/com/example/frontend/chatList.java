package com.example.frontend;

import java.io.Serializable;
import java.util.ArrayList;

public class chatList implements Serializable{
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

class chat implements Serializable {
    private user opposite;
    private ArrayList<message> chatContent;


    public chat(user u,ArrayList<message> c){
        opposite=u;
        chatContent=c;
    }

    public chat(user u){
        opposite=u;
        chatContent=new ArrayList<>();
    }

    public ArrayList<message> getChatContent() {
        return chatContent;
    }

    public user getOpposite() {
        return opposite;
    }
    public void insert(message m){ chatContent.add(m);}
}

class message implements Serializable{
    private String messageString;
    private user From;
    private user To;

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

    public user getTo() {
        return To;
    }
}