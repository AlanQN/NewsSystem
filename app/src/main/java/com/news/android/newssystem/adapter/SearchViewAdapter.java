package com.news.android.newssystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.android.newssystem.R;
import com.news.android.newssystem.model.SearchHistoryBean;
import com.news.android.newssystem.util.SQLiteDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2018-03-22.
 * 搜索视图中ListView适配器
 */

public class SearchViewAdapter extends BaseAdapter {

    private List<SearchHistoryBean> history = new ArrayList<SearchHistoryBean>(); //搜索历史
    private LayoutInflater layoutInflater = null;   //布局加载器
    private SQLiteDAO dao;  //数据库操作对象
    private OnItemDeleteListener itemDeleteListener;    //搜索历史子项删除监听对象

    /**
     * 构造函数
     * @param context 上下文
     */
    public SearchViewAdapter(Context context) {
        //初始化布局加载器
        layoutInflater = LayoutInflater.from(context);
        //获取数据库操作对象
        dao = SQLiteDAO.newInstance(context);
    }

    /**
     * 设置历史记录
     * @param history 历史记录
     */
    public void setHistory(List<SearchHistoryBean> history) {
        //接收数据
        this.history = history;
        //刷新视图
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return history == null ? 0 : history.size();
    }

    @Override
    public Object getItem(int i) {
        return history.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i; //当前位置
        SearchViewHolder viewHolder = null;
        if (view == null) {
            //创建控件暂存对象
            viewHolder = new SearchViewHolder();
            //加载布局
            view = layoutInflater.inflate(R.layout.search_history_item, viewGroup, false);
            //设置控件
            viewHolder.historyText = (TextView) view.findViewById(R.id.history_text);
            viewHolder.deleteBtn = (ImageView) view.findViewById(R.id.delete_history_btn);
            //设置暂存对象
            view.setTag(viewHolder);
        } else {
            //直接取出控件暂存对象
            viewHolder = (SearchViewHolder) view.getTag();
        }
        //绑定数据
        viewHolder.historyText.setText(history.get(position).getContent());
        //设置事件
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //从数据库中删除历史数据
                dao.deleteSearchHistory(history.get(position).getId());
                //删除当前搜索历史item
                history.remove(position);
                //更新视图
                notifyDataSetChanged();
                //回调子项删除监听函数
                if (itemDeleteListener != null) {
                    itemDeleteListener.onItemDelete(history.size());
                }
            }
        });
        //返回视图
        return view;
    }

    /**
     * 删除所有数据
     */
    public void deleteData() {
        history.clear();
        this.notifyDataSetChanged();
    }

    /**
     * 获取指定位置的搜索历史
     * @param position 搜索历史
     * @return
     */
    public String getHistoryAt(int position) {
        return history.get(position).getContent();
    }

    /**
     * 控件暂存
     */
    class SearchViewHolder {

        private TextView historyText;  //历史文本
        private ImageView deleteBtn; //删除按钮

    }

    /**
     * 删除搜索历史监听类
     */
    public interface OnItemDeleteListener {
        /**
         * 删除子项事件
         * @param itemCount 子项个数
         */
        void onItemDelete(int itemCount);
    }

    /**
     * 设置子项删除监听对象
     * @param itemDeleteListener 监听对象
     */
    public void setOnItemDeleteListener(OnItemDeleteListener itemDeleteListener) {
        this.itemDeleteListener = itemDeleteListener;
    }

}
