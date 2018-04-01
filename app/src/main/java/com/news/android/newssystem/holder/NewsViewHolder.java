package com.news.android.newssystem.holder;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.android.newssystem.R;
import com.news.android.newssystem.model.NewsBean;

/**
 * Created by Alan on 2018-03-19.
 * 新闻概要视图子项Item的ViewHolder
 */

public class NewsViewHolder extends RecyclerView.ViewHolder {

    private ImageView newsIcon; //新闻图标
    private TextView newsTitle; //新闻辩题
    private TextView newsPublisher; //新闻作者
    private TextView contentsCount; //评论数量
    private Button deleteBtn;    //删除按钮
    private LinearLayout newsItem;  //新闻项布局

    /**
     * 构造函数
     * @param itemView 当前子项的View
     */
    public NewsViewHolder(View itemView) {
        super(itemView);
        //获取UI控件
        newsIcon = (ImageView) itemView.findViewById(R.id.news_icon);
        newsTitle = (TextView) itemView.findViewById(R.id.news_title);
        newsPublisher = (TextView) itemView.findViewById(R.id.news_publisher);
        contentsCount = (TextView) itemView.findViewById(R.id.contents_count);
        deleteBtn = (Button) itemView.findViewById(R.id.delete_btn);
        newsItem = (LinearLayout) itemView.findViewById(R.id.news_item);
    }

    /**
     * 绑定数据
     * @param news 数据对象
     */
    public void bindHolder(NewsBean news) {
        if (news != null) {
            //绑定新闻标题
            if (news.getTitle() != null)
            {
                newsTitle.setText(news.getTitle());
            } else {
                newsTitle.setText("");
            }
            //绑定新闻作者
            if (news.getPublisher() != null)
            {
                newsPublisher.setText(news.getPublisher());
            } else {
                newsPublisher.setText("");
            }
            //绑定新闻图标
            if (news.getIcon() != null)
            {
                Uri uri = Uri.parse(news.getIcon());
                Log.d("debug", "onBindViewHolder: URI = " + uri);
                newsIcon.setImageURI(uri);
            }
            //绑定评论次数
            if (news.getContentsCount() > 0) {
                String conrentsCount = news.getContentsCount() + "评";
                contentsCount.setText(conrentsCount);
            } else {
                contentsCount.setText("");
            }
        }
    }

    /**
     * 返回删除按钮
     * @return 删除按钮
     */
    public Button getDeleteBtn() {
        return deleteBtn;
    }

    /**
     * 返回当前子项
     * @return 当前子项
     */
    public LinearLayout getNewsItem() {
        return newsItem;
    }
}
