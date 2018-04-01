package com.news.android.newssystem.model;

public class NewsBean {

    private int id; //新闻编号
    private String title;   //新闻标题
    private String content;	//新闻内容
    private int contentsCount;   //评论数量
    private String publisher;  //新闻作者
    private String icon; //新闻图标地址
    private String publishTime;    //新闻发布时间
    private String type;	//新闻类型

    /**
     * 构造函数
     */
    public NewsBean() {

    }

    /**
     * 构造函数
     * @param title 标题
     * @param publisher 发布者
     * @param publishTime 发布时间
     * @param contentsCount 评论数量
     */
    public NewsBean(String title, String publisher, String publishTime, int contentsCount) {
        this.title = title;
        this.publisher = publisher;
        this.publishTime = publishTime;
        this.contentsCount = contentsCount;
    }

    /**
     * 构造函数
     * @param title 标题
     * @param publisher 发布者
     * @param publishTime 发布时间
     */
    public NewsBean(String title, String publisher, String publishTime) {
        this.title = title;
        this.publisher = publisher;
        this.publishTime = publishTime;
    }

    /**
     * 构造函数
     * @param title 标题
     * @param publisher 发布者
     */
    public NewsBean(String title, String publisher) {
        this.title = title;
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getContentsCount() {
        return contentsCount;
    }

    public void setContentsCount(int contentsCount) {
        this.contentsCount = contentsCount;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}