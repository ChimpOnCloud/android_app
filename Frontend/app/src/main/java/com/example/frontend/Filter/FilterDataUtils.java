package com.example.frontend.Filter;

import com.example.frontend.Filter.Flow.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class FilterDataUtils {
    public static List<FilterGrop> getFilterData() {
        List<FilterGrop> filterGrops = new ArrayList<>();

        //学历要求
        FilterGrop filterGrop = new FilterGrop();
        filterGrop.gropName = "仅显示关注者";
        filterGrop.key="follow";
        filterGrop.filterType = TagFlowLayout.TAG_MODE_SINGLE;
        filterGrop.filters = new ArrayList<>();
        filterGrop.filters.add(new FilterBean(FilterBean.UNLIMITED, "不限"));
        filterGrop.filters.add(new FilterBean("1_1", "开启"));
        filterGrops.add(filterGrop);


        //薪资待遇
        FilterGrop filterGrop1 = new FilterGrop();
        filterGrop1.gropName = "排序方式";
        filterGrop1.key="sort";
        filterGrop1.filterType = TagFlowLayout.TAG_MODE_SINGLE;
        filterGrop1.filters = new ArrayList<>();
        filterGrop1.filters.add(new FilterBean(FilterBean.UNLIMITED, "不限"));
        filterGrop1.filters.add(new FilterBean("2_1", "按时间排序"));
        filterGrop1.filters.add(new FilterBean("2_2", "按热度排序"));
        filterGrops.add(filterGrop1);


        //经验要求
        FilterGrop filterGrop2 = new FilterGrop();
        filterGrop2.gropName = "标签";
        filterGrop2.key="tag";
        filterGrop2.filters = new ArrayList<>();
        filterGrop2.filters.add(new FilterBean(FilterBean.UNLIMITED, "不限"));
        filterGrop2.filters.add(new FilterBean("3_1", "#默认话题"));
        filterGrop2.filters.add(new FilterBean("3_2", "#校园资讯"));
        filterGrop2.filters.add(new FilterBean("3_3", "#二手交易"));
        filterGrop2.filters.add(new FilterBean("3_4", "#思绪随笔"));
        filterGrop2.filters.add(new FilterBean("3_5", "#吐槽盘点"));
        filterGrops.add(filterGrop2);
        return filterGrops;

    }
}