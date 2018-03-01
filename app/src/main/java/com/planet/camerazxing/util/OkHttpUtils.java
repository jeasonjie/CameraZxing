package com.planet.camerazxing.util;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.planet.camerazxing.app.MyApplication;
import com.planet.camerazxing.cnst.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Neusoft on 2016/5/27.
 */
public class OkHttpUtils {
    private static final String TAG = "OkHttpUtils";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static MediaType MULTI = MediaType.parse("application/octet-stream");
    private static String BASE_URL = "http://api.fir.im/apps/latest";//测试版本fir更新地址

    private OkHttpClient mOkHttpClient;
    private static OkHttpUtils mInstance;
    private Gson mGson;

    private OkHttpUtils() {
        if (mOkHttpClient == null)
            mOkHttpClient = new OkHttpClient.Builder()
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .build();
        if (mGson == null)
            mGson = new Gson();
    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null)
                    mInstance = new OkHttpUtils();
            }
        }
        return mInstance;
    }

    /**
     * 异步的get请求,更新版本用
     *
     */
    public static void get(String actionUrl, HashMap<String, String> paramsMap, final Handler handler) {
        StringBuilder tempParams = new StringBuilder();
        OkHttpClient mHttpClient = new OkHttpClient();
        try {
            //处理参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s/%s?%s", BASE_URL, actionUrl, tempParams.toString());
            Log.e("get=========url: ", requestUrl);
            //创建一个请求
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .build();
            //创建一个Call
            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    Log.e("get======onFailure", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message msg = new Message();
                    msg.obj = response.body().string();
                    Log.e("get===result", msg.obj.toString());
                    msg.what = Const.REQUEST_SERVER_SUCCESS;
                    handler.sendMessage(msg);
                }

            });

        } catch (Exception e) {
            Log.e("ex==========", e.toString());
        }
    }

    /**
     * 异步的下载请求
     *
     * @param url
     */
    public static void post(String url, Handler handler) {
        getInstance()._post(url, handler);
    }

    private void _post(String url, final Handler handler) {
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("moer", "onFailure: ");;
            }

            @Override
            public void onResponse(Call call, Response response) {
                Message msg = new Message();
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    //拿到字节流
                    is = response.body().byteStream();

                    int len = 0;
                    String path = Environment.getExternalStorageDirectory().getPath() + File.separator + MyApplication.getAppConfig().packageName;
                    File f = new File(path);
                    if (!f.exists())
                        f.mkdirs();
                    File file = new File(f, "goods_" + System.currentTimeMillis() + ".apk");
                    if (!file.exists())
                        file.createNewFile();
                    fos = new FileOutputStream(file);
                    byte[] buf = new byte[1024];

                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }

                    fos.flush();

                    msg.obj = file.getPath();
                    msg.what = Const.REQUEST_SERVER_SUCCESS;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = Const.REQUEST_SERVER_FAUILUE;
                } finally {

                    //关闭流
                    try {
                        if(fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        msg.what = Const.REQUEST_SERVER_FAUILUE;
                    }
                    try {
                        if(is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        msg.what = Const.REQUEST_SERVER_FAUILUE;
                    }
                }

                handler.sendMessage(msg);
            }
        });

    }

    /**
     * 异步的post请求，传model类型
     *
     * @param url
     * @param obj
     */
    public static void post(String url, Object obj, Handler handler) {
        getInstance()._post(url, obj, handler);
    }

    private void _post(String url, Object obj, Handler handler) {

        Request request = buildPostRequest(url, obj);
        //发送请求获取响应
        deliveryResult(request, handler);

    }

    private Request buildPostRequest(String url, Object obj) {
        String json = mGson.toJson(obj);
        Log.e(TAG, "url: " + url);
        Log.e(TAG, "post: " + json);
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        return new Request.Builder()
                .url(url)
                .post(requestBody)

                .build();
    }


    /**
     * 异步的post请求，传map类型
     *
     * @param url
     * @param map
     */
    public static void post(String url, Map map, Handler handler) {
        getInstance()._post(url, map, handler);
    }

    private void _post(String url, Map map, Handler handler) {

        Request request = buildPostRequest(url, map);
        //发送请求获取响应
        deliveryResult(request, handler);

    }

    private Request buildPostRequest(String url, Map<String, Object> map) {
        String json = mGson.toJson(map);
        Log.e(TAG, "url: " + url);
        Log.e(TAG, "post: " + json);
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * 异步的post请求，传json String类型
     * @param url
     * @param json
     * @param handler
     */
    public static void post(String url, String json, Handler handler) {
        getInstance()._post(url, json, handler);
    }

    private void _post(String url, String json, Handler handler) {

        Request request = buildPostRequest(url, json);
        //发送请求获取响应
        deliveryResult(request, handler);

    }

    private Request buildPostRequest(String url, String json) {
        Log.e(TAG, "url: " + url);
        Log.e(TAG, "post: " + json);
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    private void deliveryResult(Request request, final Handler handler) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "post onFailure: " + e);
                handler.sendEmptyMessage(Const.REQUEST_SERVER_FAUILUE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.e(TAG, "post onResponse: " + str);
                Message msg = new Message();
                msg.obj = str;
                msg.what = Const.REQUEST_SERVER_SUCCESS;
                handler.sendMessage(msg);
            }

        });
    }
}
