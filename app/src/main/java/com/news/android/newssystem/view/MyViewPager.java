package com.news.android.newssystem.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.news.android.newssystem.R;

import java.util.List;

/**
 * Created by Alan on 2018-03-10.
 * 自定义的带有tab导航栏的ViewPager控件
 */

public class MyViewPager extends LinearLayout {

    private ViewPagerIndicator indicator;   //导航栏
    private ViewPager viewPager;    //页面控件
    private HorizontalScrollView scrollView;    //滑动视图

    private FragmentPagerAdapter fragmentPagerAdapter;    //设配器
    private List<String> titles;    //主题集合

    public  MyViewPager(Context context) {
        this(context, null);
    }

    public MyViewPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //加载布局文件
        LayoutInflater.from(context).inflate(R.layout.my_view_pager, this, true);
        //获取控件
        indicator = (ViewPagerIndicator) findViewById(R.id.my_indicator);
        viewPager = (ViewPager) findViewById(R.id.my_view_pager);
        scrollView = (HorizontalScrollView) findViewById(R.id.scroll_view);
        //加载属性资源文件
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.MyViewPager);
        if (attributes != null) {
            //处理导航栏属性
            //先处理背景色
            int indicatorBGC = attributes.getColor(R.styleable.MyViewPager_indicator_background_color, Color.WHITE);
            indicator.setBackgroundColor(indicatorBGC);
            //处理背景
            int indicatorBG = attributes.getResourceId(R.styleable.MyViewPager_indicator_background, -1);
            if(indicatorBG != -1) {
                indicator.setBackgroundResource(indicatorBG);
            }
            //处理指示器的颜色
            int indicatorColor = attributes.getColor(R.styleable.MyViewPager_indicator_color, -1);
            if (indicatorColor != -1) {
                indicator.setIndicatorColor(indicatorColor);
            }
            //处理显示tab的数量
            int tabVisibleCount = attributes.getInteger(R.styleable.MyViewPager_tab_visible_count, -1);
            if (tabVisibleCount != -1) {
                indicator.setTabVisibleCount(tabVisibleCount);
            }
            //处理tab文本默认颜色
            int textNormalColor = attributes.getColor(R.styleable.MyViewPager_text_normal_color, -1);
            if (textNormalColor != -1) {
                indicator.setTextNormalColor(textNormalColor);
            }
            //处理tab文本高亮颜色
            int textLightColor = attributes.getColor(R.styleable.MyViewPager_text_light_color, -1);
            if (textLightColor != -1) {
                indicator.setTextLightColor(textLightColor);
            }
            //处理tab文本字体大小
            int textSize = attributes.getDimensionPixelSize(R.styleable.MyViewPager_text_size, -1);
            if (textSize != -1) {
                indicator.setTextSize(textSize);
            }

            //回收资源
            attributes.recycle();
        }
        //设置垂直布局
        this.setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //添加tab
        indicator.setTabItemTitles(titles);
        //设置适配器
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(titles.size());
        //设置页面控件
        indicator.setViewPager(viewPager, 0);
        //设置滑动视图
        indicator.setHorizontalScrollView(scrollView);
    }

    /**
     * 设置MyViewPager的主题集合
     * @param titles 主题集合
     */
    public void setTabItemTitles(List<String> titles) {
        this.titles = titles;
        //初始化数据
        initData();
    }

    /**
     * 设置fragment适配器
     * @param fragmentPagerAdapter 适配器
     */
    public void setFragmentPagerAdapter(FragmentPagerAdapter fragmentPagerAdapter) {
        this.fragmentPagerAdapter = fragmentPagerAdapter;
    }

    /**
     * 添加一个tab
     * @param title tab标题
     */
    public void addTabItem(String title) {
        indicator.addTabItem(title);
    }

    /**
     * 设置页面滑动变化监听器
     * @param listener 监听器
     */
    public void addOnPageChangeListener(ViewPagerIndicator.OnPageChangeListener listener) {
        indicator.addOnPageChangeListener(listener);
    }

    /**
     * 移除指定页面变化监听器
     * @param listener 监听器
     */
    public void removeOnPageChangeListener(ViewPagerIndicator.OnPageChangeListener listener) {
        indicator.removeOnPageChangeListener(listener);
    }

    /**
     * 清除所有页面滑动变化监听器
     */
    public void clearOnPageChangeListener() {
        indicator.clearOnPageChangeListener();
    }

}
