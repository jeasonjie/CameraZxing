package com.google.zxing.client.android.camera;

import android.content.SharedPreferences;

import com.zywx.wbpalmstar.plugin.uexscanner.JsConst;

/**
 * Created by Neusoft on 2016/4/19.
 */
public enum FrontLightMode {

    /** Always on. */
    ON,
    /** On only when ambient light is low. */
    AUTO,
    /** Always off. */
    OFF;

    private static FrontLightMode parse(String modeString) {
        return modeString == null ? OFF : valueOf(modeString);
    }

    public static FrontLightMode readPref(SharedPreferences sharedPrefs) {
        return parse(sharedPrefs.getString(JsConst.LIGHT_MODE, OFF.toString()));
    }
}
