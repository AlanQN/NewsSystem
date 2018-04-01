package com.news.android.newssystem.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Alan on 2018-03-22.
 * 显示搜索历史记录的ListView
 */

public class HistoryListView extends ListView {

    public HistoryListView(Context context) {
        super(context);
    }

    public HistoryListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoryListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 通过复写onMeasure方法解决ListView与ScrollView适配问题
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
