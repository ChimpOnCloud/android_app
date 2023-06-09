package com.example.frontend.Filter;

import com.example.frontend.Filter.Flow.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

public class FilterDataUtils {
    public static List<FilterGrop> getFilterData() {
        List<FilterGrop> filterGrops = new ArrayList<>();

        FilterGrop filterGrop = new FilterGrop();
        filterGrop.gropName = "仅显示关注者";
        filterGrop.key="follow";
        filterGrop.filterType = TagFlowLayout.TAG_MODE_SINGLE;
        filterGrop.filters = new ArrayList<>();
        filterGrop.filters.add(new FilterBean(FilterBean.UNLIMITED+"0", "不限"));
        filterGrop.filters.add(new FilterBean("1_1", "开启"));
        filterGrops.add(filterGrop);

        FilterGrop filterGrop1 = new FilterGrop();
        filterGrop1.gropName = "排序方式";
        filterGrop1.key="sort";
        filterGrop1.filterType = TagFlowLayout.TAG_MODE_SINGLE;
        filterGrop1.filters = new ArrayList<>();
        filterGrop1.filters.add(new FilterBean(FilterBean.UNLIMITED+"1", "不限"));
        filterGrop1.filters.add(new FilterBean("2_1", "按时间排序"));
        filterGrop1.filters.add(new FilterBean("2_2", "按点赞量排序"));
        filterGrop1.filters.add(new FilterBean("2_3", "按评论量排序"));
        filterGrops.add(filterGrop1);

        FilterGrop filterGrop2 = new FilterGrop();
        filterGrop2.gropName = "标签";
        filterGrop2.key="tag";
        filterGrop2.filterType=TagFlowLayout.TAG_MODE_SINGLE;
        filterGrop2.filters = new ArrayList<>();
        filterGrop2.filters.add(new FilterBean(FilterBean.UNLIMITED+"2", "不限"));
        filterGrop2.filters.add(new FilterBean("3_1", "#默认话题"));
        filterGrop2.filters.add(new FilterBean("3_2", "#校园资讯"));
        filterGrop2.filters.add(new FilterBean("3_3", "#二手交易"));
        filterGrop2.filters.add(new FilterBean("3_4", "#思绪随笔"));
        filterGrop2.filters.add(new FilterBean("3_5", "#吐槽盘点"));
        filterGrops.add(filterGrop2);

        FilterGrop filterGrop3 = new FilterGrop();
        filterGrop3.gropName = "屏蔽设置";
        filterGrop3.key="tag";
        filterGrop3.filterType=TagFlowLayout.TAG_MODE_SINGLE;
        filterGrop3.filters = new ArrayList<>();
        filterGrop3.filters.add(new FilterBean(FilterBean.UNLIMITED+"2", "不限"));
        filterGrop3.filters.add(new FilterBean("4_1", "屏蔽"));
        filterGrops.add(filterGrop3);
        return filterGrops;
    }
}