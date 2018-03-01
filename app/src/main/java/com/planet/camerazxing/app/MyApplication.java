package com.planet.camerazxing.app;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.planet.camerazxing.bean.AppInfo;
import com.zywx.wbpalmstar.engine.universalex.EUExUtil;

/**
 * Created by planet on 2018/2/8.
 */

public class MyApplication extends Application {

    static AppInfo appConfig = null;

    @Override
    public void onCreate() {
        super.onCreate();
        EUExUtil.init(getApplicationContext());
        appConfig = new AppInfo();
        initAppInfo(appConfig);
    }

    /* 初始化软件信息 */
    private void initAppInfo(AppInfo app) {
        PackageManager pkManager = getPackageManager();
        app.packageName = getPackageName();
        PackageInfo info = null;
        if (pkManager != null) {
            try {
                info = pkManager.getPackageInfo(app.packageName, PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }

        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            app.name = appInfo.loadLabel(pkManager).toString();// 软件名称在这里赋值
            app.verCode = info.versionCode;
            app.verName = info.versionName;
        }
        // app.APP_IP = Util.getLocalHostIp();
    }

    public static AppInfo getAppConfig() {
        return appConfig;
    }
}
