package com.news.android.newssystem.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.news.android.newssystem.R;
import com.news.android.newssystem.holder.NewsViewHolder;
import com.news.android.newssystem.model.NewsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2018-03-11.
 * RecyclerView的适配器
 */

public class NewsViewAdapter extends Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;  //布局加载器
    private List<NewsBean> newsBeans = new ArrayList<NewsBean>();    //新闻信息数据
    private OnItemClickListener listener;   //点击监听对象

    /**
     * 构造函数
     * @param context 上下文
     */
    public NewsViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 设置数据并更新视图
     * @param data 更新后的数据
     */
    public void setData(List<NewsBean> data) {
        //接收数据
        this.newsBeans = data;
        //通知视图更新
        notifyDataSetChanged();
    }

    /**
     * 添加新数据
     * @param newData 新数据
     */
    public void addData(List<NewsBean> newData) {
        //添加数据
        if (newData != null && newData.size() > 0) {
            for (int i = newData.size()-1; i >= 0; i--) {
                newsBeans.add(0, newData.get(i));
            }
        }
        //通知视图更新
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //实例化展示的View
        View view = layoutInflater.inflate(R.layout.news_item, parent, false);
        //实例化ViewHolder
        RecyclerView.ViewHolder viewHolder = new NewsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NewsViewHolder newsViewHolder = (NewsViewHolder) holder;    //强制转换
        newsViewHolder.bindHolder(newsBeans.get(position));  //绑定数据
        final int cPosition = position;  //当前位置
        //为删除按钮设置事件
        newsViewHolder.getDeleteBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //移除数据
                newsBeans.remove(newsBeans.get(cPosition));
                //更新视图
                notifyDataSetChanged();
            }
        });
        //设置Item点击事件
        newsViewHolder.getNewsItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(cPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsBeans == null ? 0 : newsBeans.size();
    }

    /**
     * 获取数据
     * @return List<NewsBean>
     */
    public List<NewsBean> getData() {
        return newsBeans;
    }

    /**
     * 获取当前新闻数据的最新发布时间
     * @return String
     */
    public String getLastPublishTime() {
        if (newsBeans != null && newsBeans.size() > 0) {
            return newsBeans.get(0).getPublishTime();
        } else {
            return null;
        }
    }

    /**
     * Item点击监听类
     */
    public interface OnItemClickListener {

        void onItemClick(int position);

    }

    /**
     * 设置Item点击监听对象
     * @param listener 监听对象
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
