package com.planet.camerazxing.cnst;

import com.planet.camerazxing.BuildConfig;

/**
 * Created by Administrator on 2018/2/8.
 */

public class Const {

    public static final int REQUEST_SERVER_SUCCESS = 01;
    public static final int REQUEST_SERVER_FAUILUE = 00;

    public static String WEB_update = "";

    static {
        if (BuildConfig.DEBUG) {
            //测试版本app更新地址
            WEB_update = "";
        } else {
            //正式版地址
            WEB_update = "";
        }
    }
}
