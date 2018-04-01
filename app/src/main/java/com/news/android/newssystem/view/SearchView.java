package com.news.android.newssystem.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.news.android.newssystem.R;

public class SearchView extends LinearLayout {

    private ImageView searchIcon = null;    //搜索图标
    private EditText searchText = null; //搜索文本框
    private Button clearBtn = null;    //清除文本按钮
    private TextWatcher textChangeListener = null;  //搜索文本监听对象
    private ClearButtonClickListener clearButtonClickListener = null;   //搜索文本清除监听对象
    private OnFocusChangeListener focusChangeListener = null;   //焦点变换监听对象

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //加载布局文件
        LayoutInflater.from(context).inflate(R.layout.search_view, this, true);
        //获取相应控件
        searchIcon = (ImageView) findViewById(R.id.search_icon);
        searchText = (EditText) findViewById(R.id.search_text);
        clearBtn = (Button) findViewById(R.id.clear_btn);
        //获取属性
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SearchView);
        //设置属性
        if (attributes != null) {
            //处理搜索图标
            int icon = attributes.getResourceId(R.styleable.SearchView_search_icon, -1);
            if(icon != -1) {
                //图标不为空，则设置图标
                searchIcon.setBackgroundResource(icon);
            } else {
                //搜索图标为定义，使用默认图标
                searchIcon.setBackgroundResource(R.mipmap.search);
            }

            //处理搜索文本框
            //查询提示
            String queryHint = attributes.getString(R.styleable.SearchView_query_hint);
            if (!TextUtils.isEmpty(queryHint)) {
                searchText.setHint(queryHint);
            }
            //处理搜索框是否可编辑
            boolean editable = attributes.getBoolean(R.styleable.SearchView_editable, true);
            searchText.setFocusable(editable);
            searchText.setFocusableInTouchMode(editable);
            //处理搜索框文本
            String text = attributes.getString(R.styleable.SearchView_text);
            if (!TextUtils.isEmpty(text)) {
                searchText.setText(text);
            }
            //处理搜索框字体大小
            float textSize = attributes.getDimensionPixelSize(R.styleable.SearchView_text_size, 16);
            searchText.setTextSize(textSize);

            attributes.recycle();

            //处理清除按钮显示
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(searchText.getText())) {
                        clearBtn.setVisibility(VISIBLE);
                    } else {
                        clearBtn.setVisibility(GONE);
                    }
                    //回调监听函数
                    if (textChangeListener != null) {
                        textChangeListener.afterTextChanged(editable);
                    }
                }
            });
            //获取焦点事件
            searchText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (focusChangeListener != null) {
                        focusChangeListener.onFocusChange(view, b);
                    }
                }
            });
            //处理清除按钮清空搜索内容
            clearBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchText.setText("");
                    if (clearButtonClickListener != null) {
                        clearButtonClickListener.onClick();
                    }
                }
            });
        }

        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
    }

    /**
     * 返回搜索框中的文本
     * @return 文本
     */
    public Editable getText() {
        return searchText.getText();
    }

    /**
     * 设置搜索框文本
     * @param text 文本
     */
    public void setText(String text) {
        searchText.setText(text);
        searchText.setSelection(searchText.getText().length());
    }

    /**
     * 设置搜索文本监听对象
     * @param textChangeListener 监听对象
     */
    public void setTextChangeListener(TextWatcher textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    /**
     * 清除按钮监听类
     */
    public interface ClearButtonClickListener {
        /**
         * 清除监听事件
         */
        void onClick();
    }


    /**
     * 设置清除按钮点击监听对象
     * @param clearButtonClickListener 监听对象
     */
    public void setClearButtonClickListener(ClearButtonClickListener clearButtonClickListener) {
        this.clearButtonClickListener = clearButtonClickListener;
    }

    /**
     * 设置焦点监听对象
     * @param focusChangeListener 监听对象
     */
    public void setFocusChangeListener(OnFocusChangeListener focusChangeListener) {
        this.focusChangeListener = focusChangeListener;
    }

    /**
     * 失去焦点
     */
    @Override
    public void clearFocus() {
        super.clearFocus();
        searchText.clearFocus();
    }

    /**
     * 获取焦点
     */
    public void requestSearchFocus() {
        searchText.requestFocus();
    }
}
