package com.news.android.newssystem.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Alan on 2018-03-09.
 * ViewPagerIndicator分页控件中的页面
 */

public class ViewPagerFragment extends Fragment {

    private String title;   //页面标题
    /**
     * 设置bundle的key，用于获取用户传递过来的值
     */
    private static final String BUNDLE_TITLE = "title";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //获取bundle中的参数
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(BUNDLE_TITLE);
        }
        //显示title在fragment上
        TextView tv = new TextView(getActivity());
        tv.setText(title);
        tv.setTextColor(Color.GRAY);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    /**
     * 创建ViewPagerFragment实例
     * @param title 参数
     * @return ViewPagerFragment
     */
    public static ViewPagerFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        ViewPagerFragment fragment = new ViewPagerFragment();
        /*
         * 官方推荐Fragment.setArguments(Bundle bundle)这种方式来传递参数，而不推荐通过构造方法直接来传递参数
         * 这是因为假如Activity重新创建（横竖屏切换）时，会重新构建它所管理的Fragment，原先的Fragment的字段值将会全
         * 部丢失，但是通过Fragment.setArguments(Bundle bundle)方法设置的bundle会保留下来。所以尽量使用
         * Fragment.setArguments(Bundle bundle)方式来传递参数
         */
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 返回主题
     * @return String 主题
     */
    public String getTitle() {
        return title;
    }

}
