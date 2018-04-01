package com.news.android.newssystem.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.android.newssystem.R;
import com.news.android.newssystem.model.RecordBean;

/**
 * Created by Alan on 2018-03-19.
 * 历史记录和收藏视图的ViewHolder
 */

public class RecordViewHolder extends RecyclerView.ViewHolder {

    private TextView newsTitle = null;  //新闻标题
    private CheckBox deleteBtn = null;   //删除按钮
    private LinearLayout recordItem = null;    //记录子项

    /**
     * 构造函数
     * @param itemView 子项视图
     */
    public RecordViewHolder(View itemView) {
        super(itemView);
        //获取UI控件
        newsTitle = (TextView) itemView.findViewById(R.id.news_title);
        deleteBtn = (CheckBox) itemView.findViewById(R.id.delete_btn);
        recordItem = (LinearLayout) itemView.findViewById(R.id.record_item);
    }

    /**
     * 绑定数据
     * @param bean 数据
     */
    public void bindHolder(RecordBean bean) {
        if (bean != null) {
            newsTitle.setText(bean.getTitle());
        }
    }

    /**
     * 返回删除按钮
     * @return 删除按钮
     */
    public CheckBox getDeleteBtn() {
        return deleteBtn;
    }

    /**
     * 返回当前记录项
     * @return LinearLayout
     */
    public LinearLayout getRecordItem() {
        return recordItem;
    }
}
