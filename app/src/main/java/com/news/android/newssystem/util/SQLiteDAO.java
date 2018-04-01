package com.news.android.newssystem.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.news.android.newssystem.model.SearchHistoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alan on 2018-03-22.
 * 数据库操作工具类
 */

public class SQLiteDAO {

    private static SQLiteDAO instance = null; //单例
    private Context context;    //上下文
    private SQLiteDBHelper dbHelper = null;    //创建数据库对象
    private static SQLiteDatabase sqLiteDatabase = null;    //数据库操作对象

    /**
     * 构造函数
     * @param context 上下文
     */
    private SQLiteDAO(Context context) {
        //接收参数
        this.context = context;
        //实例化DBHelper
        dbHelper = new SQLiteDBHelper(context);
    }

    /**
     * 获取实例
     * @param context 上下文
     * @return SQLiteDAO
     */
    public static SQLiteDAO newInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteDAO(context);
        }
        return instance;
    }

    /**
     * 插入一条搜索历史
     * @param history 搜索历史
     */
    public void insertSearchHistory(String history) {
        boolean exist = isSearchHistoryExist(history);
        Log.d("debug", "insertSearchHistory: exist = " + exist);
        //当搜索历史在数据库中不存在时，执行添加操作
        if (!exist) {
            sqLiteDatabase = dbHelper.getWritableDatabase();    //获取数据库对象
            sqLiteDatabase.beginTransaction();
            sqLiteDatabase.setTransactionSuccessful();
            //创建SQL语句
            String sql = "insert into " + SQLiteDBHelper.TABLE_NAME + "(content) values(?);";
            sqLiteDatabase.execSQL(sql, new String[]{history});
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
            Log.d("debug", "insertSearchHistory: " + history);
        }
    }

    /**
     * 插入搜索历史
     * @param historyList 搜索历史
     */
    public void insertSearchHistory(List<String> historyList) {
        if (historyList != null) {
            for (int i = 0; i < historyList.size(); i++) {
                insertSearchHistory(historyList.get(i));
            }
        }
    }

    /**
     * 删除一条搜索历史
     * @param historyId 搜索历史编号
     */
    public void deleteSearchHistory(Integer historyId) {
        sqLiteDatabase = dbHelper.getWritableDatabase();    //获取数据库对象
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.setTransactionSuccessful();
        //创建SQL语句
        String sql = "delete from " + SQLiteDBHelper.TABLE_NAME + " where id = ?;";
        sqLiteDatabase.execSQL(sql, new Integer[]{historyId});
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
        Log.d("debug", "deleteSearchHistory: " + historyId);
    }

    /**
     * 删除搜索历史
     * @param historyIdList 搜索历史编号
     */
    public void deleteSearchHistory(List<Integer> historyIdList) {
        if (historyIdList != null) {
            for (int i = 0; i < historyIdList.size(); i++) {
                deleteSearchHistory(historyIdList.get(i));
            }
        }
    }

    /**
     * 删除所有搜索历史
     */
    public void deleteSearchHistory() {
        sqLiteDatabase = dbHelper.getWritableDatabase();    //获取数据库对象
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.setTransactionSuccessful();
        //创建SQL语句
        String sql = "delete from " + SQLiteDBHelper.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
        Log.d("debug", "deleteSearchHistory: ");
    }

    /**
     * 查询所有搜索历史
     * @return List<SearchHistoryBean>
     */
    public List<SearchHistoryBean> querySearchHistory() {
        sqLiteDatabase = dbHelper.getReadableDatabase();    //获取数据库对象
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.setTransactionSuccessful();
        List<SearchHistoryBean> historyBeans = new ArrayList<SearchHistoryBean>();  //搜索历史集合
        //SQL语句
        String sql = "select * from " + SQLiteDBHelper.TABLE_NAME + " order by id desc;";
        //执行语句
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        //获取搜索历史
        while (cursor.moveToNext()) {
            //新建搜索历史对象
            SearchHistoryBean bean = new SearchHistoryBean();
            //设置属性
            bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            bean.setContent(cursor.getString(cursor.getColumnIndex("content")));
            Log.d("debug", "querySearchHistory: " + bean.getContent());
            //添加到集合中
            historyBeans.add(bean);
        }
        cursor.close();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
        //返回
        return historyBeans;
    }

    /**
     * 获取与搜索内容相似的搜索历史
     * @param searchText 搜索内容
     * @return List<SearchHistory>
     */
    public List<SearchHistoryBean> querySearchHistory(String searchText) {
        sqLiteDatabase = dbHelper.getReadableDatabase();    //获取数据库对象
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.setTransactionSuccessful();
        List<SearchHistoryBean> historyBeans = new ArrayList<SearchHistoryBean>();  //搜索历史集合
        //SQL语句
        String sql = "select * from " + SQLiteDBHelper.TABLE_NAME + " where content like '%" + searchText + "%' order by id desc;";
        //执行查询
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        //获取搜索历史
        while (cursor.moveToNext()) {
            //创建历史对象
            SearchHistoryBean bean = new SearchHistoryBean();
            //设置属性值
            bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
            bean.setContent(cursor.getString(cursor.getColumnIndex("content")));
            //添加到集合
            historyBeans.add(bean);
        }
        cursor.close();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
        //返回结果
        return historyBeans;
    }

    /**
     * 判断搜索历史是否已经存在
     * @param history 搜索历史
     * @return boolean
     */
    private boolean isSearchHistoryExist(String history) {
        sqLiteDatabase = dbHelper.getReadableDatabase();    //获取数据库对象
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.setTransactionSuccessful();
        boolean exist = false;  //是否存在
        String sql = "select * from " + SQLiteDBHelper.TABLE_NAME + " where content=?;";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[]{history});
        if (cursor.moveToNext()) {
            exist = true;
        }
        cursor.close();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
        //返回结果
        return exist;
    }

}
