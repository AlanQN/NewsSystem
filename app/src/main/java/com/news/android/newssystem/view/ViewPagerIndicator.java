package com.news.android.newssystem.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.android.newssystem.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2018-03-09.
 * 分页控件
 */

public class ViewPagerIndicator extends LinearLayout {

    private Paint paint;    //绘制矩形的画笔
    private Path path;  //用于绘制矩形的边
    private int rectWidth;  //矩形的宽
    private int rectHeight; //矩形的高
    private int translationX;   //指示器（矩形）的偏移位置
    private int initTranslationX;   //初始偏移位置
    private int indicatorColor;  //矩形指示器的颜色
    private int tabVisibleCount;    //显示的Tab的数量
    private int textSize;   //tab文本大小
    private int textNormalColor;  //tab文本默认颜色
    private int textLightColor;   //tab文本高亮颜色
    private ViewPager viewPager;    //页面控件
    private HorizontalScrollView horizontalScrollView;    //滑动视图
    private List<OnPageChangeListener> listeners;  //页面滑动变化监听器集合

    private static final int TRANSLATION_RATE = 8;  //偏移位置占指示器宽的比重
    private static final String INDICATOR_DEFAULT_COLOR = "#FF8247";    //指示器默认颜色
    private static final int TEXT_NORMAL_COLOR = Color.GRAY;    //tab文本的默认颜色
    private static final int TEXT_LIGHT_COLOR = Color.BLACK;    //tab文本高亮的颜色
    private static final int TEXT_DEFAULT_SIZE = 15;    //tab文本的默认大小
    private static final int TAB_DEFAULT_COUNT = 4;   //默认tab的数量

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //加载属性文件
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        if(attributes != null) {
            //获取显示tab的数量
            tabVisibleCount = attributes.getInteger(R.styleable.ViewPagerIndicator_tab_visible_count, TAB_DEFAULT_COUNT);
            if (tabVisibleCount <= 0) {
                tabVisibleCount = TAB_DEFAULT_COUNT;
            }
            //获取指示器颜色
            indicatorColor = attributes.getColor(R.styleable.ViewPagerIndicator_indicator_color, Color.parseColor(INDICATOR_DEFAULT_COLOR));
            //获取矩形指示器的高度
            rectHeight = attributes.getInteger(R.styleable.ViewPagerIndicator_indicator_height, 6);
            //获取默认tab文本颜色
            textNormalColor = attributes.getColor(R.styleable.ViewPagerIndicator_text_normal_color, TEXT_NORMAL_COLOR);
            //获取tab高亮颜色
            textLightColor = attributes.getColor(R.styleable.ViewPagerIndicator_text_light_color, TEXT_LIGHT_COLOR);
            //获取tab文本大小
            textSize = attributes.getDimensionPixelSize(R.styleable.ViewPagerIndicator_text_size, TEXT_DEFAULT_SIZE);

            attributes.recycle();
        }
        //初始化画笔
        initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取矩形的宽
        rectWidth = getScreenWidth() / tabVisibleCount;
        //偏移位置
        initTranslationX = rectWidth / TRANSLATION_RATE;
        //设置矩形指示器的边
        initRect();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);
        //设置画笔颜色
        paint.setColor(indicatorColor);
        //设置全填充
        paint.setStyle(Paint.Style.FILL);
        //设置矩形边角平滑
        paint.setPathEffect(new CornerPathEffect(3));
    }

    /**
     * 初始化矩形指示器
     */
    private void initRect() {
        path = new Path();
        //设置起点
        path.moveTo(0, 0);
        //设置矩形的各个点
        path.lineTo(rectWidth - 2 * initTranslationX, 0);
        path.lineTo(rectWidth - 2 * initTranslationX, -rectHeight);
        path.lineTo(0, -rectHeight);
        //闭合
        path.close();
    }

    /**
     * 绘制矩形指示器
     * @param canvas 画布
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        /*
         * save：用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
         *
         * restore：用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。
         *
         * save和restore要配对使用（restore可以比save少，但不能多），如果restore调用次数比save多，会引发Error。
         */
        canvas.save();
        //偏移
        canvas.translate(initTranslationX+translationX, getHeight());
        //绘制矩形
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    /**
     * 页面滑动处理事件
     * @param position 位置
     * @param positionOffset 偏移量
     */
    public void scroll(int position, float positionOffset) {
        //获取宽度
        int tabWidth = getScreenWidth() / tabVisibleCount;
        //获取指示器需要偏移的位置
        translationX = (int) (tabWidth * (position + positionOffset));
        //移动indicator（当指示器的位置在倒数第二个tab时）
        /*if ((position >= (tabVisibleCount - 2)) && positionOffset > 0 && getChildCount() > tabVisibleCount) {
            if (tabVisibleCount != 1) {
                if (position != getChildCount()-2) {
                    int offset =  (position - (tabVisibleCount - 2)) * tabWidth + (int) (tabWidth * positionOffset);
                    //当移动到界面可见数的最后一个时,直接移动到0   不然会出现第一个显示不全的bug
                    if (position == tabVisibleCount-2) {
                        offset = 0;
                    }
                    this.scrollTo(offset, 0);
                }
            } else {
                int offset = position * tabWidth +  (int) (positionOffset * tabWidth);
                this.scrollTo(offset, 0);
            }
        }*/
        //移动滑动视图
        if ((position >= 1) && positionOffset > 0 && getChildCount() > tabVisibleCount) {
            if (tabVisibleCount != 1) {
                if (position != getChildCount()-2) {
                    int offset =  (position - (tabVisibleCount - 2)) * tabWidth + (int) (tabWidth * positionOffset);
                    //当移动到界面可见数的最后一个时,直接移动到0   不然会出现第一个显示不全的bug
                    if (position == tabVisibleCount-2) {
                        offset = 0;
                    }
                    horizontalScrollView.smoothScrollTo(offset, 0);
                }
            } else {
                int offset = position * tabWidth +  (int) (positionOffset * tabWidth);
                horizontalScrollView.smoothScrollTo(offset, 0);
            }
        }
        //重绘
        invalidate();
    }

    /**
     * 页面滑动变化监听器
     */
    interface OnPageChangeListener {

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);

    }

    /**
     * 设置页面滑动变化监听器
     * @param listener 监听器
     */
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    /**
     * 移除指定页面变化监听器
     * @param listener 监听器
     */
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * 清除所有页面滑动变化监听器
     */
    public void clearOnPageChangeListener() {
        if (listeners != null) {
            listeners.clear();
        }
    }

    /**
     * 设置页面控件
     * @param viewPager 页面控件
     */
    public void setViewPager(final ViewPager viewPager, int position) {
        //接收参数
        this.viewPager = viewPager;
        //设置页面滑动事件
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //设置指示器移动
                scroll(position, positionOffset);
                //回调用户处理函数
                if (listeners != null && listeners.size() > 0) {
                    for (OnPageChangeListener listener : listeners) {
                        listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                //设置选中的tab样式
                setSelectedTabItemStyle(position);

                //回调用户处理函数
                if (listeners != null && listeners.size() > 0) {
                    for (OnPageChangeListener listener : listeners) {
                        listener.onPageSelected(position);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //回调用户处理函数
                if (listeners != null && listeners.size() > 0) {
                    for (OnPageChangeListener listener : listeners) {
                        listener.onPageScrollStateChanged(state);
                    }
                }
            }
        });
        //设置当前显示位置
        viewPager.setCurrentItem(position);
        //设置当前选中tab的样式
        setSelectedTabItemStyle(position);
    }

    /**
     * 获取屏幕的宽度
     * @return int
     */
    private int getScreenWidth() {
        int width = 0;
        //得到WindowManager
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            Display display = manager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            //返回屏幕宽度
            width =  metrics.widthPixels;
        }
        return width;
    }

    /**
     * 重新设置tab的宽度，已达到控制显示的tab数量
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取tab的数量
        int tabCount = getChildCount();
        if (tabCount <= 0) {
            return;
        }
        //设置tab的宽度
        for (int i = 0; i < tabCount; i++) {
            View tab = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) tab.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / tabVisibleCount;
            tab.setLayoutParams(lp);
        }
        //设置tab点击事件
        setTabItemClickEvent();
    }

    /**
     * 设置tab标题，动态添加tab
     * @param titles 标题
     */
    public void setTabItemTitles(List<String> titles) {
        if (titles != null && titles.size() > 0) {
            //移除所有子控件
            this.removeAllViews();
            for (int i = 0; i < titles.size(); i++) {
                //添加tab
                addTabItem(titles.get(i));
            }
        }
        //设置tab点击事件
        setTabItemClickEvent();
    }

    /**
     * 添加一个tab
     * @param title tab标题
     */
    public void addTabItem(String title) {
        TextView tab = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.weight = 0;
        if (tabVisibleCount <= 0) {
            lp.width = getScreenWidth() / TAB_DEFAULT_COUNT;
        } else {
            lp.width = getScreenWidth() / tabVisibleCount;
        }
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        tab.setTextColor(textNormalColor);
        tab.setLayoutParams(lp);
        tab.setSingleLine();
        addView(tab);
    }

    /**
     * 设置显示的tab的数量
     * @param count 显示的tab数量
     */
    public void setTabVisibleCount(int count) {
        tabVisibleCount = count;
    }

    /**
     * 设置tab点击事件
     */
    private void setTabItemClickEvent() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final int position = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(position);
                }
            });
        }
    }

    /**
     * 设置选中tab的样式
     * @param position 选中位置
     */
    private void setSelectedTabItemStyle(int position) {
        //首先重置所有的tab样式
        resetTabItemStyle();
        //为选中的tab设置选中样式
        View view = getChildAt(position);
        if (view instanceof TextView) {
            TextView tab = (TextView) view;
            tab.setTextColor(textLightColor);
        }
    }

    /**
     * 重置tab样式
     */
    private void resetTabItemStyle() {
        int tabCount = getChildCount();
        for (int i = 0; i < tabCount; i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                TextView tab = (TextView) view;
                tab.setTextColor(textNormalColor);
            }
        }
    }

    /**
     * 设置指示器的背景颜色
     * @param indicatorColor 背景颜色
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    /**
     * 设置tab文本默认颜色
     * @param textNormalColor 默认颜色
     */
    public void setTextNormalColor(int textNormalColor) {
        this.textNormalColor = textNormalColor;
    }

    /**
     * 设置tab文本高亮颜色
     * @param textLightColor 高亮颜色
     */
    public void setTextLightColor(int textLightColor) {
        this.textLightColor = textLightColor;
    }

    /**
     * 设置tab文本字体大小
     * @param textSize 字体大小
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置水平滑动视图
     * @param horizontalScrollView 滑动视图
     */
    public void setHorizontalScrollView(HorizontalScrollView horizontalScrollView) {
        this.horizontalScrollView = horizontalScrollView;
    }

}
