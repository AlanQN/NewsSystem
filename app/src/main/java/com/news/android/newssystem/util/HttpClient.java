package com.news.android.newssystem.util;

import android.os.Handler;
import android.os.Message;

import com.news.android.newssystem.DetailActivity;
import com.news.android.newssystem.MainActivity;
import com.news.android.newssystem.RecordActivity;
import com.news.android.newssystem.SearchActivity;
import com.news.android.newssystem.model.NewsBean;
import com.news.android.newssystem.model.RecordBean;
import com.news.android.newssystem.view.NewsFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Alan on 2018-03-15.
 * 网络资源获取类
 */

public class HttpClient {

    public static final int CONNECT_ERROR = 404;
    public static final String BASE_URL= "http://192.168.191.1:9634/NewsSystem";/*"http://172.16.30.120:9634/NewsSystem";*/

    /**
     * 获取所有新闻类型
     */
    public static void getNewsTypes(final Handler handler) {
        String url = BASE_URL + "/queryTypeServlet";    //访问资源地址
        //创建连接对象
        OkHttpClient client = new OkHttpClient();
        //创建请求
        Request request = new Request.Builder().url(url).build();
        //异步请求处理对象
        Call call = client.newCall(request);
        //执行处理
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //发送失败信息
                Message msg = Message.obtain();
                msg.what = CONNECT_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取返回的json格式数据
                String result = response.body().string();
                //将json格式数据转换为需要的格式
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<String>>(){}.getType();
                List<String> newsTypes = gson.fromJson(result, type);
                //发送消息
                Message msg = Message.obtain();
                msg.what = MainActivity.INIT_DATA;
                msg.obj = newsTypes;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取指定类型的新闻
     * @param type 新闻类型
     * @param lastTime 当前新闻数据的最新发布时间
     * @param handler 异步处理对象
     */
    public static void getNewsData(String type, String lastTime, final Handler handler) {
        String url = BASE_URL + "/SchemaServlet?news_type=" + type;    //访问资源地址
        if (lastTime != null) {
            url = url + "&last_time=" + lastTime;
        }
        //创建连接对象
        OkHttpClient client = new OkHttpClient();
        //创建请求
        Request request = new Request.Builder().url(url).build();
        //创建一部请求处理对象
        Call call = client.newCall(request);
        //执行请求处理
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //发送失败信息
                Message msg = Message.obtain();
                msg.what = CONNECT_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取json格式的返回数据
                String result = response.body().string();
                //将数据转换为需要的形式
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<NewsBean>>(){}.getType();
                List<NewsBean> newsData = gson.fromJson(result, type);
                //发送更新消息
                Message msg = Message.obtain();
                msg.what = NewsFragment.REFRESH_VIEW;
                msg.obj = newsData;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取用户的记录（历史记录或收藏记录）
     * @param userId 用户编号
     * @param recordType 记录类型
     */
    public static void getNewsRecords(Integer userId, String recordType, final Handler handler) {
        String url = BASE_URL + "/queryRecordServlet?user_id=" + userId + "&record_type=" + recordType;  //访问资源地址
        //获取Http连接
        OkHttpClient client = new OkHttpClient();
        //创建请求
        Request request = new Request.Builder().url(url).build();
        //创建异步执行对象
        Call call = client.newCall(request);
        //执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //发送失败信息
                Message msg = Message.obtain();
                msg.what = CONNECT_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取json格式的返回结果
                String result = response.body().string();
                //将数据转换为需要的形式
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<RecordBean>>(){}.getType();
                List<RecordBean> records = gson.fromJson(result, type);
                //发送消息
                Message msg = Message.obtain();
                msg.what = RecordActivity.REFRESH_VIEW;
                msg.obj = records;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 删除用户的新闻记录
     * @param newsIdList 新闻编号
     * @param userId 用户编号
     * @param recordType 记录类型
     * @param handler 异步处理对象
     */
    public static void deleteNewsRecord(List<Integer> newsIdList, Integer userId, String recordType, final Handler handler) {
        //将新闻编号数据转为Json格式
        Gson gson = new Gson();
        String data = gson.toJson(newsIdList);
        String url = BASE_URL + "/deleteRecordServlet?user_id=" + userId + "&news_id_list=" + data +
                "&record_type=" + recordType; //请求地址
        //创建连接对象
        OkHttpClient client = new OkHttpClient();
        //创建请求
        Request request = new Request.Builder().url(url).build();
        //异步处理请求对象
        Call call = client.newCall(request);
        //执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //发送失败信息
                Message msg = Message.obtain();
                msg.what = CONNECT_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if ("delete successful".equals(result)) {
                    //发送删除成功消息
                    Message msg = Message.obtain();
                    msg.what = RecordActivity.DELETE_RECORD;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    /**
     * 搜索新闻
     * @param searchText 搜索内容
     * @param handler 异步处理对象
     */
    public static void searchNewsData(String searchText, final Handler handler) {
        String url = BASE_URL + "/SearchServlet?search_text=" + searchText; //访问地址
        //创建连接对象
        OkHttpClient client = new OkHttpClient();
        //创建请求
        Request request = new Request.Builder().url(url).build();
        //获取异步执行对象
        Call call = client.newCall(request);
        //执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //发送失败消息
                Message msg = Message.obtain();
                msg.what = CONNECT_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取json格式的返回数据
                String result = response.body().string();
                //将数据转换为需要的形式
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<NewsBean>>(){}.getType();
                List<NewsBean> newsData = gson.fromJson(result, type);
                //发送更新消息
                Message msg = Message.obtain();
                msg.what = SearchActivity.NEWS_VIEW;
                msg.obj = newsData;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取收藏情况
     * @param userId 用户编号
     * @param newsId 新闻编号
     * @param handler 异步处理对象
     */
    public static void getCollectCondition(int userId, int newsId, final Handler handler) {
        String url = BASE_URL + "/collectNewsServlet?request_type=condition&user_id=" + userId + "&news_id=" + newsId;  //访问地址
        //创建连接对象
        OkHttpClient client = new OkHttpClient();
        //创建请求
        Request request = new Request.Builder().url(url).build();
        //获取异步执行对象
        Call call = client.newCall(request);
        //执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //发送失败消息
                Message msg = Message.obtain();
                msg.what = CONNECT_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取结果
                String result = response.body().string();
                //发送消息
                Message msg = Message.obtain();
                msg.what = DetailActivity.COLLECT_CONDITION;
                msg.obj = Boolean.valueOf(result);
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 处理收藏请求
     * @param userId 用户编号
     * @param newsId 新闻编号
     * @param collected 收藏情况
     * @param handler 异步处理对象
     */
    public static void collectNews(int userId, int newsId, boolean collected, final Handler handler) {
        String url = BASE_URL + "/collectNewsServlet?request_type=operation&user_id=" + userId + "&news_id="
                + newsId + "&collected="+collected;  //访问地址
        //创建连接对象
        OkHttpClient client = new OkHttpClient();
        //创建请求
        Request request = new Request.Builder().url(url).build();
        //获取异步执行对象
        Call call = client.newCall(request);
        //执行请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //发送失败消息
                Message msg = Message.obtain();
                msg.what = CONNECT_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取结果
                String result = response.body().string();
                //发送消息
                Message msg = Message.obtain();
                msg.what = DetailActivity.COLLECT_OPERATION;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
    }

}
