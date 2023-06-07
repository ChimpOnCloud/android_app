package com.example.frontend.Filter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frontend.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.gyf.immersionbar.ImmersionBar;

public class FilterActivity extends AppCompatActivity {
    private FilterLayout mFilterLayout;
    private TextView mFilterResultTV;
    private TextView mFilterResultNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ImmersionBar.with(this).statusBarDarkFont(true).init();

        mFilterLayout = findViewById(R.id.filter_list);
        mFilterResultTV = findViewById(R.id.filter_result_tv);
        mFilterResultNum = findViewById(R.id.filter_result_num);
        mFilterLayout.setFilterData(FilterDataUtils.getFilterData());
        mFilterLayout.setOnFilterChangeListener(new FilterLayout.OnFilterChangeListener() {
            @Override
            public void result(Map<String, List<FilterBean>> result) {
                if (result != null) {
                    Iterator<Map.Entry<String, List<FilterBean>>> iterator = result.entrySet().iterator();
                    int num = 0;
                    while (iterator.hasNext()) {
                        List<FilterBean> value = iterator.next().getValue();
                        num += value.size();
                    }
                    mFilterResultTV.setText("筛选");
                    mFilterResultNum.setVisibility(View.GONE);
                }
            }
        });
    }

    public void reset(View view) {
        mFilterLayout.reset();
    }

    public void ok(View view) {
        List result = mFilterLayout.result();
        // Log.e("FilterActivity", "" + result.toString());
        ArrayList<FilterBean> arrayList= (ArrayList<FilterBean>) result;
        Intent intent=new Intent();
        intent.putExtra("result",arrayList);
        setResult(RESULT_OK,intent);
        finish();
    }

    public void close(View view) {
        finish();
    }
}