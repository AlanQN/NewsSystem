package com.news.android.newssystem.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alan on 2018-03-22.
 * 数据库创建类
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;    //数据库版本
    private static final String DB_NAME = "NewsDB"; //数据库名称
    public static final String TABLE_NAME = "search_history";  //表名

    /**
     * 构造函数
     * @param context 上下文
     */
    public SQLiteDBHelper(Context context) {
        //创建数据库
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建表的SQL语句
        String sql = "create table if not exists " + TABLE_NAME + "(id integer primary key autoincrement, content text);";
        //执行建表语句
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //更新数据库
        String sql = "drop table if exists " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
}
