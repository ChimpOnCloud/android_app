package com.example.frontend.Filter;

import java.io.Serializable;

public class FilterBean implements Serializable {
    public final static String UNLIMITED = "-1";//不限，这里与后台定义一致

    public String id;
    public String name;

    public FilterBean() {
    }

    public FilterBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FilterBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}