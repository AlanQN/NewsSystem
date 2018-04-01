package com.news.android.newssystem.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.news.android.newssystem.R;
import com.news.android.newssystem.holder.RecordViewHolder;
import com.news.android.newssystem.holder.TimeViewHolder;
import com.news.android.newssystem.model.RecordBean;
import com.news.android.newssystem.util.HttpClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Alan on 2018-03-19.
 * 阅读历史记录RecyclerView的数据适配器
 */

public class RecordViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater = null;   //布局加载
    private List<RecordBean> data = new ArrayList<>(); //加工后数据
    private List<RecordBean> records = new ArrayList<>();   //记录集合
    private String recordType;  //记录类型
    private Map<Integer, CheckBox> deleteBtnMap = new HashMap<Integer, CheckBox>(); //删除按钮集合
    private OnSelectedChangeListener selectedChangeListener;    //删除按钮选中状态监听对象
    private OnItemClickListener itemClickListener;  //记录项点击监听对象
    private int selectedCount = 0;  //删除按钮选中的个数
    private List<RecordBean> selectedItems = new ArrayList<RecordBean>(); //要删除的记录项

    private static final int TIME_ITEM = 1;    //标题子项
    private static final int RECORD_ITEM = 2;   //记录子项
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");   //日期格式化对象

    public RecordViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 设置记录数据
     * @param records 记录
     */
    public void setData(List<RecordBean> records) {
        //接收数据
        this.records = records;
        //对数据进行加工
        data = this.processData(records);
        //更新视图
        notifyDataSetChanged();
    }

    /**
     * 设置记录类型
     * @param recordType 记录类型
     */
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //当前子项View对象
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        //根据不同的类型加载不同的布局
        switch (viewType) {
            case TIME_ITEM:
                view = layoutInflater.inflate(R.layout.time_item, parent, false);
                viewHolder = new TimeViewHolder(view);
                //设置记录类型
                ((TimeViewHolder) viewHolder).setRecordType(recordType);
                break;
            case RECORD_ITEM:
                view = layoutInflater.inflate(R.layout.record_item, parent, false);
                viewHolder = new RecordViewHolder(view);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final int cPosition = position; //当前位置
        //获取Holder的类型
        int type = getItemViewType(position);
        //根据不同的类型执行不同操作
        switch (type) {
            case TIME_ITEM:
                //绑定数据
                ((TimeViewHolder) holder).bindHolder(data.get(position));
                break;
            case RECORD_ITEM:
                //绑定数据
                ((RecordViewHolder) holder).bindHolder(data.get(position));
                //默认未选中
                ((RecordViewHolder) holder).getDeleteBtn().setChecked(false);
                //将删除按钮加入集合
                deleteBtnMap.put(position, ((RecordViewHolder) holder).getDeleteBtn());
                //为删除按钮设置事件
                ((RecordViewHolder) holder).getDeleteBtn().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (((RecordViewHolder) holder).getDeleteBtn().isChecked()) {
                            //加入到集合
                            selectedItems.add(data.get(cPosition));
                            //更新选中个数
                            selectedCount = selectedItems.size();
                        } else {
                            //从选中集合中移除
                            selectedItems.remove(data.get(cPosition));
                            //更新选中个数
                            selectedCount = selectedItems.size();
                        }
                        if (selectedChangeListener != null) {
                            selectedChangeListener.onSelectedChange(selectedCount);
                        }
                    }
                });
                //为记录项设置点击事件
                ((RecordViewHolder) holder).getRecordItem().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemClickListener != null) {
                            itemClickListener.onItemClick(data.get(cPosition).getNewsId());
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getTitle() == null) {
            return TIME_ITEM;
        } else {
            return RECORD_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 隐藏所有删除按钮
     */
    public void hideAllDeleteBtn() {
        //获取键
        Iterator<Integer> keys = deleteBtnMap.keySet().iterator();
        //隐藏所有按钮并设置未选中
        while (keys.hasNext()) {
            Integer key = keys.next();
            CheckBox deleteBtn = deleteBtnMap.get(key);
            deleteBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 显示所有删除按钮
     */
    public void showAllDeleteBtn() {
        //获取键
        Iterator<Integer> keys = deleteBtnMap.keySet().iterator();
        //显示所有按钮
        while (keys.hasNext()) {
            Integer key = keys.next();
            CheckBox deleteBtn = deleteBtnMap.get(key);
            deleteBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 所有删除按钮为选中
     */
    public void setAllDeleteBtnSelected() {
        Iterator<Integer> keys = deleteBtnMap.keySet().iterator();
        while (keys.hasNext()) {
            Integer key = keys.next();
            CheckBox deleteBtn = deleteBtnMap.get(key);
            deleteBtn.setChecked(true);
        }
    }

    /**
     * 所有删除按钮选中
     */
    public void setAllDeleteBtnUnselected() {
        Iterator<Integer> keys = deleteBtnMap.keySet().iterator();
        while (keys.hasNext()) {
            Integer key = keys.next();
            CheckBox deleteBtn = deleteBtnMap.get(key);
            deleteBtn.setChecked(false);
        }
    }

    /**
     * 删除按钮选中状态监听接口
     */
    public interface OnSelectedChangeListener {
        /**
         * 选中状态变化监听处理事件
         * @param selectedCount 选中个数
         */
        void onSelectedChange(int selectedCount);
    }

    /**
     * 设置删除按钮选中状态监听对象
     * @param selectedChangeListener 监听对象
     */
    public void setOnSelectedChangeListener(OnSelectedChangeListener selectedChangeListener) {
        this.selectedChangeListener = selectedChangeListener;
    }

    /**
     * 当前记录项点击监听类
     */
    public interface OnItemClickListener {
        /**
         * 记录项点击事件
         */
        void onItemClick(int newsId);
    }

    /**
     * 设置记录子项监听对象
     * @param itemClickLisener 监听对象
     */
    public void setOnItemClickLisener(OnItemClickListener itemClickLisener) {
        this.itemClickListener = itemClickLisener;
    }

    /**
     * 删除选中的记录
     * @param handler 异步处理对象
     */
    public void deleteSelectedRecord(Handler handler) {
        if (selectedItems.size() > 0) {
            List<Integer> newsIdList = new ArrayList<Integer>();    //要删除记录的新闻编号
            Integer userId = selectedItems.get(0).getUserId(); //用户编号
            //获取要删除记录的新闻编号，并从数据集合中移除要删除的记录
            for (int i = 0; i < selectedItems.size(); i++) {
                newsIdList.add(selectedItems.get(i).getNewsId());
                records.remove(selectedItems.get(i));
            }
            //重新生成数据
            data = this.processData(records);
            //清空选中项
            selectedItems.clear();
            deleteBtnMap.clear();
            //置零
            selectedCount = 0;
            //更新视图
            this.notifyDataSetChanged();
            //提交删除请求到数据库
            HttpClient.deleteNewsRecord(newsIdList, userId, recordType, handler);
        }
    }

    /**
     * 对记录数据进行加工，获取需要的数据集合
     * @param records 记录数据
     * @return List<RecordBean>
     */
    private List<RecordBean> processData(List<RecordBean> records) {
        List<RecordBean> data = new ArrayList<>();  //加工后的数据集合
        Date currentDate = getCurrentDate(); //当前时间
        if (records != null) {
            for (int i = 0; i < records.size(); i++) {
                RecordBean time = new RecordBean(); //时间戳
                RecordBean bean = records.get(i);   //当前记录
                data.add(time); //时间戳加入到集合中
                int count = 0;  //记录个数
                int diffDays = 0;   //相差天数
                //计算时间差
                try {
                    Date date = df.parse(bean.getTime());
                    diffDays = getDifferentDays(date, currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d("debug", "processData: 解析时间错误");
                }
                //获取与当前记录同一天的记录总数
                while (i < records.size()) {
                    if (bean.getTime().equals(records.get(i).getTime())) {
                        //日期相同，加入到集合中，个数加一
                        data.add(records.get(i));
                        count++;
                        i++;
                    } else {
                        //日期不同，回退，进行下一轮操作
                        i--;
                        break;
                    }
                }
                //设置属性
                time.setCount(count);
                if (diffDays == 0) {
                    time.setTime("今天");
                } else if (diffDays == 1) {
                    time.setTime("昨天");
                } else {
                    time.setTime(bean.getTime());
                }
            }
        }
        return data;
    }

    /**
     * 获取当前日期时间
     * @return Date
     */
    private Date getCurrentDate() {
        Date date = null; //当前日期
        // 获取时间
        try {
            date = df.parse(df.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("debug", "getCurrentDate: 解析时间错误");
        }
        //返回结果
        return date;
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    private int getDifferentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //不同年
        {
            return day2-day1;
        }
    }

}
