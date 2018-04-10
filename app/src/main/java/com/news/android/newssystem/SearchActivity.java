package com.news.android.newssystem;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.android.newssystem.adapter.NewsViewAdapter;
import com.news.android.newssystem.adapter.SearchViewAdapter;
import com.news.android.newssystem.model.NewsBean;
import com.news.android.newssystem.model.SearchHistoryBean;
import com.news.android.newssystem.util.HttpClient;
import com.news.android.newssystem.util.SQLiteDAO;
import com.news.android.newssystem.view.HistoryListView;
import com.news.android.newssystem.view.SearchView;

import java.lang.ref.WeakReference;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backBtn;  //返回按钮
    private SearchView searchBox;  //搜索框
    private TextView searchBtn; //搜索按钮
    private HistoryListView historyView;  //搜索历史视图
    private SearchViewAdapter searchViewAdapter;  //历史视图适配器对象
    private RecyclerView newsView;  //新闻视图
    private NewsViewAdapter newsViewAdapter;    //新闻视图适配器对象
    private SQLiteDAO dao;  //数据库操作对象
    private TextView clearBtn;  //清除历史按钮
    private LinearLayout historyBox;    //搜索历史框
    private final Handler handler = new SearchHandler(this);    //异步处理对象

    public static final int NEWS_VIEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Window window = this.getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setContentView(R.layout.activity_search);
        //初始化UI控件
        initUI();
        //初始化数据
        initData();
        //设置事件
        addEvent();
    }

    /**
     * 初始化UI控件
     */
    private void initUI() {
        //获取UI控件
        backBtn = (ImageView) findViewById(R.id.back_btn);
        searchBox = (SearchView) findViewById(R.id.search_box);
        searchBtn = (TextView) findViewById(R.id.search_btn);
        historyView = (HistoryListView) findViewById(R.id.history_view);
        clearBtn = (TextView) findViewById(R.id.clear_history_btn);
        historyBox = (LinearLayout) findViewById(R.id.history_box);
        newsView = (RecyclerView) findViewById(R.id.news_view);
        //搜索框获取焦点
        searchBox.requestSearchFocus();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化数据库操作对象
        dao = SQLiteDAO.newInstance(this);
        //初始化搜索视图适配器
        searchViewAdapter = new SearchViewAdapter(this);
        //查询搜索历史
        List<SearchHistoryBean> historyBeans = dao.querySearchHistory();
        //设置搜索历史栏显示状态
        if (historyBeans.size() > 0) {
            historyBox.setVisibility(View.VISIBLE);
        } else {
            historyBox.setVisibility(View.INVISIBLE);
        }
        //为适配器设置内容
        searchViewAdapter.setHistory(historyBeans);
        //为搜索历史视图设置适配器
        historyView.setAdapter(searchViewAdapter);
        //初始化新闻视图适配器
        newsViewAdapter = new NewsViewAdapter(this);
        //初始化新闻视图布局管理器
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //为新闻视图设置适配器和布局管理器
        newsView.setLayoutManager(layoutManager);
        newsView.setAdapter(newsViewAdapter);
        //隐藏新闻视图
        newsView.setVisibility(View.GONE);
    }

    /**
     * 设置事件
     */
    private void addEvent() {
        //为UI控件设置点击事件
        backBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        //设置适配器子项删除监听事件
        searchViewAdapter.setOnItemDeleteListener(new SearchViewAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int itemCount) {
                //改变搜索历史栏显示状态
                if (itemCount > 0) {
                    historyBox.setVisibility(View.VISIBLE);
                } else {
                    historyBox.setVisibility(View.INVISIBLE);
                }
            }
        });
        //设置历史项选中填充搜索文本事件
        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchBox.setText(searchViewAdapter.getHistoryAt(i));
            }
        });
        //设置搜索文本推荐事件
        searchBox.setTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim();    //搜索文本
                List<SearchHistoryBean> historyBeans = null;    //相似的搜索历史
                if (TextUtils.isEmpty(searchText)) {
                    //搜索内容为空，获取所有搜索历史
                    historyBeans = dao.querySearchHistory();
                } else {
                    //获取相似的搜索历史
                    historyBeans = dao.querySearchHistory(searchText);
                }
                //为适配器设置数据
                searchViewAdapter.setHistory(historyBeans);
                //控制搜索历史框显示
                if (historyBeans.size() > 0) {
                    historyBox.setVisibility(View.VISIBLE);
                } else {
                    historyBox.setVisibility(View.INVISIBLE);
                }
            }
        });
        //搜索框清除按钮点击事件
        searchBox.setClearButtonClickListener(new SearchView.ClearButtonClickListener() {
            @Override
            public void onClick() {
                //隐藏新闻视图
                newsView.setVisibility(View.GONE);
                //显示搜索历史视图
                historyView.setVisibility(View.VISIBLE);
            }
        });
        //新闻子项点击事件
        newsViewAdapter.setOnItemClickListener(new NewsViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //跳转到详细信息界面
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("news_id", newsViewAdapter.getData().get(position).getId());
                startActivity(intent);
            }
        });
        //设置搜索框焦点监听对象
        searchBox.setFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.isFocused()) {
                    //隐藏新闻视图
                    newsView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 设置点击事件
     * @param view 被点击的视图
     */
    @Override
    public void onClick(View view) {
        //点击不同视图执行不同事件
        switch (view.getId()) {
            case R.id.back_btn:
                //返回上一个页面
                SearchActivity.this.finish();
                break;
            case R.id.search_btn:
                //获取搜索框中的文本
                String searchText = searchBox.getText().toString().trim();
                //隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchBtn.getWindowToken(), 0);
                }
                //搜索框失去焦点
                searchBox.clearFocus();
                //如果文本不为空，执行搜索
                if (!TextUtils.isEmpty(searchText)) {
                    //向数据库中添加搜索历史
                    dao.insertSearchHistory(searchText);
                    //搜索含有关键字的新闻
                    HttpClient.searchNewsData(searchText, handler);
                    //隐藏搜索历史视图
                    historyBox.setVisibility(View.GONE);
                    //显示新闻视图
                    newsView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.clear_history_btn:
                //删除所有搜索历史
                dao.deleteSearchHistory();
                searchViewAdapter.deleteData();
                //隐藏搜索历史栏
                historyBox.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 搜索异步处理类
     */
    private static class SearchHandler extends Handler {

        private WeakReference<SearchActivity> activityWeakReference = null; //Activity的弱引用

        /**
         * 构造函数
         * @param activity 当前活动对象
         */
        public SearchHandler(SearchActivity activity) {
            activityWeakReference = new WeakReference<SearchActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchActivity activity = activityWeakReference.get();  //当前活动对象
            //根据不同的消息，进行不同处理
            switch (msg.what) {
                case NEWS_VIEW:
                    //获取新闻数据
                    List<NewsBean> newsBeans = (List<NewsBean>) msg.obj;
                    //设置新闻视图数据
                    activity.newsViewAdapter.setData(newsBeans);
                    break;
                default:
                    break;
            }
        }
    }

}
