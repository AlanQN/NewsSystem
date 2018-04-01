package com.news.android.newssystem;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.news.android.newssystem.util.HttpClient;
import com.news.android.newssystem.view.TitleBar;

import java.lang.ref.WeakReference;

public class DetailActivity extends AppCompatActivity {

    private int userId = 1111;  //用户编号
    private TitleBar titleBar;  //标题栏
    private WebView newsContent;    //新闻内容
    private int newsId;    //新闻编号
    private TextView collectBtn;    //收藏按钮
    private boolean collected;  //标志位，是否收藏
    private final Handler handler = new DetailHandler(this);    //异步处理对象

    public static final int COLLECT_CONDITION = 1;   //获取收藏情况
    public static final int COLLECT_OPERATION = 2;  //收藏此新闻操作

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏页面栏
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
        setContentView(R.layout.activity_detail);
        //取出newsId
        Intent intent = getIntent();
        if (intent != null) {
            newsId = intent.getIntExtra("news_id", -1);
        }
        //初始化控件
        initUI();
        //初始化数据
        initData();
        //设置处理事件
        addEvent();
    }

    /**
     * 初始化控件
     */
    private void initUI() {
        newsContent = (WebView) findViewById(R.id.news_content);
        titleBar = (TitleBar) findViewById(R.id.title_bar);
        collectBtn = titleBar.getTitleBarRightBtn();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //获取收藏情况
        HttpClient.getCollectCondition(userId, newsId, handler);
        //加载新闻内容
        String url = HttpClient.BASE_URL + "/DetailServlet?user_id=" + userId + "&news_id=" + newsId;
        newsContent.loadUrl(url);
    }

    /**
     * 设置处理事件
     */
    private void addEvent() {
        //返回上一层
        titleBar.setLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailActivity.this.finish();
            }
        });
        //设置收藏按钮点击事件
        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //改变收藏情况
                collected = !collected;
                //执行收藏处理
                HttpClient.collectNews(userId, newsId, collected, handler);
                //设置图标
                setCollectIcon();
            }
        });
    }

    /**
     * 根据收藏情况设置图标
     */
    private void setCollectIcon() {
        if (collected) {
            //设置已收藏
            collectBtn.setBackgroundResource(R.mipmap.collect);
        } else {
            //设置未收藏
            collectBtn.setBackgroundResource(R.mipmap.uncollect);
        }
    }

    /**
     * 显示收藏操作信息
     */
    private void outputCollectInfo(String info) {
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示异常信息
     */
    private void outputErrorInfo() {
        Toast.makeText(this, "通信异常，请稍后重试！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 异步处理类
     */
    private static class DetailHandler extends Handler {

        private final WeakReference<DetailActivity> activityWeakReference;  //弱引用

        /**
         * 构造函数
         * @param activity 活动对象
         */
        public DetailHandler(DetailActivity activity) {
            activityWeakReference = new WeakReference<DetailActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DetailActivity activity = activityWeakReference.get();  //获取当前活动对象
            //根据不同消息，进行处理
            switch (msg.what) {
                case HttpClient.CONNECT_ERROR:
                    //输出异常信息
                    activity.outputErrorInfo();
                    break;
                case COLLECT_CONDITION:
                    //设置收藏情况
                    activity.collected = (boolean) msg.obj;
                    //设置图标
                    activity.setCollectIcon();
                    break;
                case COLLECT_OPERATION:
                    //获取反馈信息
                    String result = (String) msg.obj;
                    //输出收藏操作反馈
                    activity.outputCollectInfo(result);
                    break;
                default:
                    break;
            }
        }
    }

}
