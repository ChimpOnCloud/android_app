package com.example.frontend.Filter;

import com.example.frontend.Filter.Flow.TagFlowLayout;

import java.util.List;

public class FilterGrop {
    public String gropName;
    public String key;//请求数据的key
    public int filterType = TagFlowLayout.TAG_MODE_MULTIPLE;
    public List<FilterBean> filters;


    @Override
    public String toString() {
        return "FilterGrop{" +
                "gropName='" + gropName + '\'' +
                ", filters=" + filters +
                '}';
    }
}