package com.news.android.newssystem.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.news.android.newssystem.R;
import com.news.android.newssystem.model.RecordBean;

/**
 * Created by Alan on 2018-03-19.
 * 时间视图的ViewHolder
 */

public class TimeViewHolder extends RecyclerView.ViewHolder {

    private TextView timeView = null;  //时间信息
    private String recordType;  //记录类型

    /**
     * 构造函数
     * @param itemView 时间视图
     */
    public TimeViewHolder(View itemView) {
        super(itemView);
        //获取UI控件
        timeView = itemView.findViewById(R.id.time_view);
    }

    /**
     * 设置记录类型
     * @param recordType 记录类型
     */
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    /**
     * 绑定数据
     * @param bean 数据对象
     */
    public void bindHolder(RecordBean bean) {
        if (bean != null) {
            if ("history".equals(recordType)) {
                timeView.setText(bean.getTime() + " 阅读了" + bean.getCount() + "条新闻");
            } else if ("collection".equals(recordType)) {
                timeView.setText(bean.getTime() + " 收藏了" + bean.getCount() + "条新闻");
            }
        }
    }
}
