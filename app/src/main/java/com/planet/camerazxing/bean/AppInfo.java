package com.planet.camerazxing.bean;

/**
 * Created by lyy on 2016/9/15.
 */
public class AppInfo {
    public static int isCheckUp = 0;
    public static int isReset = 0;
    public static int isCheng = 0;
    public static int isCreat = 0;

    public static int isForgot = -1;

    public static final String APP_ID = "5";
    public String APP_IP = "127.0.0.1";
    public static final String DEVICE_TYPE = "android";
    // 移动设备相关属性
    public String model = android.os.Build.MODEL;// 手机型号
    public int sdk = android.os.Build.VERSION.SDK_INT;// SDK版本
    public String release = android.os.Build.VERSION.RELEASE;// 系统版本

    // 应用相关的属性
    public String packageName = "";
    public int verCode = 0;
    public String verName = "";
    public String name = "";

    public static int b = 0;
    public static int e = 10;

    public static int iscreat = 0;
}
