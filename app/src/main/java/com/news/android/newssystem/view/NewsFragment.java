package com.news.android.newssystem.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.news.android.newssystem.DetailActivity;
import com.news.android.newssystem.R;
import com.news.android.newssystem.adapter.NewsViewAdapter;
import com.news.android.newssystem.model.NewsBean;
import com.news.android.newssystem.util.HttpClient;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Alan on 2018-03-11.
 * 用于显示新闻页面的Fragment
 */

public class NewsFragment extends Fragment {

    private NewsHandler handler = new NewsHandler(this);
    public static final int REFRESH_VIEW = 1;

    private String newsType;   //新闻类型
    private RecyclerView newsView;  //显示新闻信息的视图
    private NewsViewAdapter adapter;   //适配器
    private RecyclerView.LayoutManager layoutManager;   //布局管理器
    private boolean isVisible = false;  //是否可见

    private static final String BUNDLE_KEY = "type";    //获取参数的key

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //获取Bundle对象
        Bundle bundle = getArguments();
        //获取新闻类型
        if (bundle != null) {
            newsType = bundle.getString(BUNDLE_KEY);
        }
        Log.d("debug", "onCreateView: " + "type = " + newsType + "\tvisible = " + isVisible);
        //加载XML布局文件
        View view = inflater.inflate(R.layout.news_fragment, null);
        //获取RecyclerView对象
        newsView = (RecyclerView) view.findViewById(R.id.news_view);
        //初始化数据
        initData();
        //为RecyclerView对象设置布局管理器
        newsView.setLayoutManager(layoutManager);
        //添加默认的分割线
        newsView.addItemDecoration(new DividerItemDecoration(newsView.getContext(), DividerItemDecoration.VERTICAL));
        //为RecyclerView对象设置适配器
        newsView.setAdapter(adapter);
        //返回View
        return view;
    }

    /**
     * 获取NewsFragment实例对象
     * @param type 新闻类型
     * @return NewsFragment
     */
    public static NewsFragment newInstance(String type) {
        //保存新闻类型
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY, type);
        //创建NewsFragment
        NewsFragment fragment = new NewsFragment();
        //设置参数
        fragment.setArguments(bundle);
        //返回
        return fragment;
    }

    /**
     * 返回新闻类型
     * @return 新闻类型
     */
    public String getNewsType() {
        return newsType;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化布局管理器
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //初始化适配器
        adapter = new NewsViewAdapter(getActivity());
        //设置点击事件
        adapter.setOnItemClickListener(new NewsItemClickListener());
        if (isVisible)
            //获取新闻数据
            getNewsData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible) {
            getNewsData();
        }
    }

    /**
     * 获取新闻数据
     */
    public void getNewsData() {
        //获取新闻数据
        if (newsType != null) {
            HttpClient.getNewsData(newsType, adapter.getLastPublishTime(), handler);
        } else {
        }
    }

    /**
     * 显示异常信息
     */
    private void outputErrorInfo() {
        Toast.makeText(getContext(), "通信异常，请稍后重试！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 用于异步消息处理的Handler
     */
    private static class NewsHandler extends Handler {

        private final WeakReference<NewsFragment> activityWeakReference; //当前活动对象的弱引用

        public NewsHandler(NewsFragment activity) {
            this.activityWeakReference = new WeakReference<NewsFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            //获取活动对象
            NewsFragment activity = activityWeakReference.get();
            if (activity == null) {
                super.handleMessage(msg);
                return;
            }
            switch (msg.what) {
                case REFRESH_VIEW:
                    //添加数据
                    List<NewsBean> data = (List<NewsBean>) msg.obj;
                    activity.adapter.addData(data);
                    break;
                case HttpClient.CONNECT_ERROR:
                    //显示失败信息
                    activity.outputErrorInfo();
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    /**
     * 新闻子项点击事件监听器
     */
    class NewsItemClickListener implements NewsViewAdapter.OnItemClickListener {

        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(NewsFragment.this.getContext(), DetailActivity.class);
            intent.putExtra("news_id", adapter.getData().get(position).getId());
            startActivity(intent);
        }
    }

}
