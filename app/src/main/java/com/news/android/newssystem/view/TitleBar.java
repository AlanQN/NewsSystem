package com.news.android.newssystem.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.android.newssystem.R;

public class TitleBar extends RelativeLayout {

    private TextView leftBtn = null; //左按钮
    private TextView title = null;  //标题
    private TextView rightBtn = null;    //右按钮

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //加载布局文件
        LayoutInflater.from(context).inflate(R.layout.title_bar, this, true);
        //获取对应控件
        leftBtn = (TextView) findViewById(R.id.left_btn);
        title = (TextView) findViewById(R.id.bar_title);
        rightBtn = (TextView) findViewById(R.id.right_btn);

        //获取属性列表
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        //处理属性设置
        if(attributes != null) {
            //处理背景色
            int backgroundColor = attributes.getColor(R.styleable.TitleBar_background_color, Color.WHITE);
            setBackgroundColor(backgroundColor);

            //处理左按钮
            //处理左按钮文本
            String leftText = attributes.getString(R.styleable.TitleBar_left_text);
            if(!TextUtils.isEmpty(leftText)) {
                leftBtn.setText(leftText);
                //文本颜色
                int leftTextColor = attributes.getColor(R.styleable.TitleBar_left_text_color, Color.GRAY);
                leftBtn.setTextColor(leftTextColor);
            } else {
                //当文字为空时，设置按钮图标
                int leftIcon = attributes.getResourceId(R.styleable.TitleBar_left_icon, R.mipmap.back);
                leftBtn.setBackgroundResource(leftIcon);
            }
            //处理左按钮显示
            boolean leftVisible = attributes.getBoolean(R.styleable.TitleBar_left_visible, true);
            if (leftVisible) {
                leftBtn.setVisibility(View.VISIBLE);
            } else {
                leftBtn.setVisibility(View.INVISIBLE);
            }

            //处理标题
            int titleDrawable = attributes.getResourceId(R.styleable.TitleBar_title_icon, -1);
            if (titleDrawable != -1) {
                //设置图标
                title.setBackgroundResource(titleDrawable);
            } else {
                //设置文本
                String titleText = attributes.getString(R.styleable.TitleBar_title_text);
                title.setText(titleText);
                //设置文本颜色
                int titleTextColor = attributes.getColor(R.styleable.TitleBar_title_text_color, Color.BLACK);
                title.setTextColor(titleTextColor);
            }

            //处理右按钮
            String rightText = attributes.getString(R.styleable.TitleBar_right_text);
            if (!TextUtils.isEmpty(rightText)) {
                //如果文本不为空，则设置文本
                rightBtn.setText(rightText);
                //设置文本颜色
                int rightTextColor = attributes.getColor(R.styleable.TitleBar_right_text_color, Color.GRAY);
                rightBtn.setTextColor(rightTextColor);
            } else {
                //如果文本为空，则为设置图标
                int rightIcon = attributes.getResourceId(R.styleable.TitleBar_right_icon, R.mipmap.uncollect);
                rightBtn.setBackgroundResource(rightIcon);
            }
            //处理是否显示
            boolean rightVisible = attributes.getBoolean(R.styleable.TitleBar_right_visible, true);
            if (rightVisible) {
                rightBtn.setVisibility(View.VISIBLE);
            } else {
                rightBtn.setVisibility(View.INVISIBLE);
            }
            attributes.recycle();
        }
    }

    /**
     * 获取标题栏的左按钮
     * @return Button
     */
    public TextView getTitleBarLeftBtn() {
        return leftBtn;
    }

    /**
     * 为左按钮设置监听对象
     * @param leftListener 监听对象
     */
    public void setLeftBtnClickListener(OnClickListener leftListener) {
        if (leftListener != null) {
            leftBtn.setOnClickListener(leftListener);
        }
    }

    /**
     * 获取标题栏的右按钮
     * @return Button
     */
    public TextView getTitleBarRightBtn() {
        return rightBtn;
    }

    /**
     * 为右按钮设置监听对象
     * @param rightListener 监听对象
     */
    public void setRightBtnClickListener(OnClickListener rightListener) {
        if (rightListener != null) {
            rightBtn.setOnClickListener(rightListener);
        }
    }

    /**
     * 设置标题内容
     * @param title 标题内容
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }

}
