package com.news.android.newssystem.model;

/**
 * Created by Alan on 2018-03-19.
 * 历史记录的数据对象
 */

public class RecordBean {

    private String title;   //标题
    private String time;    //时间
    private int newsId;	//新闻编号
    private int userId; //用户编号
    private int count;  //数量

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
