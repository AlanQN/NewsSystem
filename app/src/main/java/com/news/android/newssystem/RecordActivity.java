package com.news.android.newssystem;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.news.android.newssystem.adapter.RecordViewAdapter;
import com.news.android.newssystem.model.RecordBean;
import com.news.android.newssystem.util.HttpClient;
import com.news.android.newssystem.view.TitleBar;

import java.lang.ref.WeakReference;
import java.util.List;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;  //标题栏
    private Button allSelectBtn;    //全选按钮
    private Button deleteSelectBtn; //删除选中项按钮
    private LinearLayout deleteBox;   //删除视图
    private String recordType;  //记录类型
    private RecyclerView recordView; //记录视图
    private RecordViewAdapter adapter;   //适配器
    private final Handler recordHandler = new RecordHandler(this);   //异步处理Handler对象

    public static final int REFRESH_VIEW = 1;   //更新视图
    public static final int DELETE_RECORD = 2;  //删除记录

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
        //设置布局文件
        setContentView(R.layout.activity_record);
        //获取创建的活动类型
        Intent intent = getIntent();
        recordType = intent.getStringExtra("record_type");
        //初始化UI控件
        initUI();
        //初始化数据
        initData();
        //设置事件
        setEvent();
    }

    /**
     * 初始化UI控件
     */
    private void initUI() {
        //获取控件
        titleBar = (TitleBar) findViewById(R.id.title_bar);
        recordView = (RecyclerView) findViewById(R.id.record_view);
        allSelectBtn = (Button) findViewById(R.id.all_select_btn);
        deleteSelectBtn = (Button) findViewById(R.id.delete_select_btn);
        deleteBox = (LinearLayout) findViewById(R.id.delete_box);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //设置标题
        if ("history".equals(recordType)) {
            titleBar.setTitle("阅读记录");
        } else if ("collection".equals(recordType)){
            titleBar.setTitle("我的收藏");
        }
        //初始化布局管理器
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //初始化适配器
        adapter = new RecordViewAdapter(this);
        //获取数据
        HttpClient.getNewsRecords(1111, recordType, recordHandler);
        //设置记录类型
        adapter.setRecordType(recordType);
        //为视图设置布局管理器和适配器
        recordView.setLayoutManager(layoutManager);
        recordView.setAdapter(adapter);
    }


    /**
     * 设置事件
     */
    private void setEvent() {
        //设置返回按钮事件
        titleBar.setLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回上一层
                RecordActivity.this.finish();
            }
        });
        //设置编辑事件
        titleBar.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("编辑".equals(titleBar.getTitleBarRightBtn().getText().toString())) {
                    clickEditBtn();
                } else if ("取消".equals(titleBar.getTitleBarRightBtn().getText().toString())){
                    resumeEditBtn();
                }
            }
        });
        //设置选项变化监听事件
        adapter.setOnSelectedChangeListener(new RecordViewAdapter.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(int selectedCount) {
                if (selectedCount == 0) {
                    resumeDeleteSelectedBtn();
                } else {
                    clickDeleteSelectedBtn(selectedCount);
                }
            }
        });
        //设置记录项点击事件
        adapter.setOnItemClickLisener(new RecordViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int newsId) {
                //前往新闻详细信息Activity
                Intent intent = new Intent(RecordActivity.this, DetailActivity.class);
                //设置参数
                intent.putExtra("news_id", newsId);
                //跳转
                startActivity(intent);
            }
        });
        allSelectBtn.setOnClickListener(this);
        deleteSelectBtn.setOnClickListener(this);
    }

    /**
     * 点击事件
     * @param view 视图
     */
    @Override
    public void onClick(View view) {
        //为不同视图执行不同点击事件处理
        switch (view.getId()) {
            case R.id.all_select_btn:
                if ("全选".equals(allSelectBtn.getText().toString())) {
                    clickAllSelectedBtn();
                } else if ("取消全选".equals(allSelectBtn.getText().toString())) {
                    resumeAllSelectedBtn();
                }
                break;
            case R.id.delete_select_btn:
                Log.d("debug", "onClick: 删除选中记录");
                //删除选中的记录
                adapter.deleteSelectedRecord(recordHandler);
                break;
            default:
                break;
        }
    }

    /**
     * 点击编辑按钮事件
     */
    private void clickEditBtn() {
        //显示所有按钮，并且修改按钮内容
        adapter.showAllDeleteBtn();
        titleBar.getTitleBarRightBtn().setText("取消");
        //设置其他控件的显示与隐藏
        titleBar.getTitleBarLeftBtn().setVisibility(View.INVISIBLE);
        deleteBox.setVisibility(View.VISIBLE);
    }

    /**
     * 回复编辑按钮状态
     */
    private void resumeEditBtn() {
        //回复删除按钮状态
        resumeDeleteSelectedBtn();
        //回复全选按钮状态
        resumeAllSelectedBtn();
        //隐藏所有按钮，并且修改按钮内容
        adapter.hideAllDeleteBtn();
        titleBar.getTitleBarRightBtn().setText("编辑");
        //设置其他控件的显示与隐藏
        titleBar.getTitleBarLeftBtn().setVisibility(View.VISIBLE);
        deleteBox.setVisibility(View.GONE);
    }

    /**
     * 点击全选按钮事件
     */
    private void clickAllSelectedBtn() {
        //选中所有删除按钮
        adapter.setAllDeleteBtnSelected();
        //改变文本内容
        allSelectBtn.setText("取消全选");
    }

    /**
     * 回复全选按钮状态
     */
    private void resumeAllSelectedBtn() {
        //设置所有删除按钮未选中
        adapter.setAllDeleteBtnUnselected();
        //改变文本内容
        allSelectBtn.setText("全选");
    }

    /**
     * 点击删除按钮事件
     */
    private void resumeDeleteSelectedBtn() {
        deleteSelectBtn.setText("删除");
        deleteSelectBtn.setTextColor(getResources().getColor(R.color.fontColor));
    }

    /**
     * 回复删除按钮状态
     */
    private void clickDeleteSelectedBtn(int selectedCount) {
        deleteSelectBtn.setText("删除(" + selectedCount + ")");
        deleteSelectBtn.setTextColor(getResources().getColor(R.color.colorRed));
    }

    /**
     * 显示异常信息
     */
    private void outputErrorInfo() {
        Toast.makeText(this, "通信异常，请稍后重试！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示删除成功信息
     */
    private void outputDeleteInfo() {
        Toast.makeText(this, "成功删除选中纪录", Toast.LENGTH_SHORT).show();
    }

    /**
     * 异步处理RecordActivity界面更新的Handler类
     */
    private static class RecordHandler extends Handler {

        private WeakReference<RecordActivity> activityWeakReference; //绑定的Activity的弱引用

        /**
         * 构造函数
         * @param activity 当前活动
         */
        public RecordHandler(RecordActivity activity) {
            activityWeakReference = new WeakReference<RecordActivity>(activity);
        }

        /**
         * 消息处理函数
         * @param msg 消息
         */
        @Override
        public void handleMessage(Message msg) {
            //获取活动对象
            RecordActivity activity = activityWeakReference.get();
            //处理消息
            switch (msg.what) {
                case REFRESH_VIEW:
                    //获取返回的数据
                    List<RecordBean> records = (List<RecordBean>) msg.obj;
                    //设置数据
                    activity.adapter.setData(records);
                    break;
                case DELETE_RECORD:
                    //显示删除成功的信息
                    activity.outputDeleteInfo();
                    //回复控件状态
                    activity.resumeEditBtn();
                    break;
                case HttpClient.CONNECT_ERROR:
                    //显示异常信息
                    activity.outputErrorInfo();
                    break;
                default:
                    break;
            }
        }
    }

}
