package com.news.android.newssystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.news.android.newssystem.util.HttpClient;
import com.news.android.newssystem.view.MyViewPager;
import com.news.android.newssystem.view.NewsFragment;
import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int INIT_DATA = 1;
    public final MainHandler mainHandler = new MainHandler(this);

    private List<String> types = new ArrayList<>(); //新闻类型
    private List<NewsFragment> contents;   //页面
    private FragmentPagerAdapter fragmentPagerAdapter;  //fragment适配器
    private MyViewPager myViewPager;    //自定义分页控件
    private LinearLayout searchBox; //搜索框
    private ImageView userInfo; //用户信息
    private NavigationView userView;   //用户信息视图
    private DrawerLayout drawerLayout;  //总布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏页面栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        //初始化UI控件
        initUI();
        //从数据库中获取新闻数据
        getDataFromDB();
    }

    /**
     * 从数据库中读取新闻数据
     */
    private void getDataFromDB() {
        //获取新闻类型
        HttpClient.getNewsTypes(mainHandler);
    }

    /**
     * 初始化UI控件
     */
    private void initUI() {
        //获取UI控件
        myViewPager = (MyViewPager) findViewById(R.id.aa_view_pager);
        searchBox = (LinearLayout) findViewById(R.id.search_box);
        userInfo = (ImageView) findViewById(R.id.user_info);
        userView = (NavigationView) findViewById(R.id.user_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final SimpleDraweeView headIcon = (SimpleDraweeView) userView.getHeaderView(0).findViewById(R.id.imageView);
        //设置点击事件
        searchBox.setOnClickListener(this);
        userInfo.setOnClickListener(this);
        userView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                switch(item.getItemId()) {
                    case R.id.user_history:
                        //跳转到阅读记录页面
                        intent = new Intent(MainActivity.this, RecordActivity.class);
                        intent.putExtra("record_type", "history");
                        startActivity(intent);
                        break;
                    case R.id.user_collection:
                        //跳转到收藏界面
                        intent = new Intent(MainActivity.this, RecordActivity.class);
                        intent.putExtra("record_type", "collection");
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                item.setCheckable(true);
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //初始化页面容器
        contents = new ArrayList<>();
        //添加页面
        if (types != null && types.size() > 0) {
            for (String title : types) {
                NewsFragment fragment = NewsFragment.newInstance(title);
                //添加到contents
                contents.add(fragment);
            }
        }
        //创建fragment适配器
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        //设置fragment适配器
        myViewPager.setFragmentPagerAdapter(fragmentPagerAdapter);
        //设置主题集合
        myViewPager.setTabItemTitles(types);
    }

    /**
     * 点击事件
     * @param view 被点击的视图
     */
    @Override
    public void onClick(View view) {
        //根据点击不同的视图，执行不同的动作
        switch (view.getId()) {
            case R.id.search_box:
                //跳转到搜索界面
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.user_info:
                //显示、隐藏NavigationView
                if (drawerLayout.isDrawerOpen(userView)) {
                    drawerLayout.closeDrawer(userView);
                } else {
                    drawerLayout.openDrawer(userView);
                }
            default:
                break;
        }
    }

    /**
     * fragment适配器类
     */
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return contents.get(position);
        }

        @Override
        public int getCount() {
            return contents.size();
        }
    }

    /**
     * 显示异常信息
     */
    private void outputErrorInfo() {
        Toast.makeText(this, "通信异常，请稍后重试！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 用于异步消息处理的Handler
     */
    private static class MainHandler extends Handler {
        private final WeakReference<MainActivity> activityWeakReference; //当前活动对象的弱引用

        public MainHandler(MainActivity activity) {
            this.activityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            //获取活动对象
            MainActivity activity = activityWeakReference.get();
            if (activity == null) {
                super.handleMessage(msg);
                return;
            }
            switch (msg.what) {
                case INIT_DATA:
                    //获取新闻类型
                    activity.types = (List<String>) msg.obj;
                    //初始化数据
                    activity.initData();
                    break;
                case HttpClient.CONNECT_ERROR:
                    //打印失败信息
                    activity.outputErrorInfo();
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}
