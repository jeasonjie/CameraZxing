package com.zywx.wbpalmstar.engine;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.webkit.WebSettings.ZoomDensity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;

/**
 * Created by Neusoft on 2016/4/19.
 */
public class ESystemInfo {

    public int mHeightPixels;
    public int mViewHeight;
    public int mWidthPixels;
    public float mXdpi;
    public float mYdpi;
    public float mDensity;
    public int mDensityDpi;
    public float mScaledDensity;
    public int mStatusBarHeight;
    public int mSysVersion;
    public int mPhoneType;
    public boolean mIsDevelop;
    public DisplayMetrics mDisplayMetrics;
    public int cpuMHZ;
    public int mDefaultFontSize;
    public int mDefaultBounceHeight;
    public int mDefaultNatvieFontSize;
    public ZoomDensity mDefaultzoom;
    public boolean mScaled;
    public boolean mFinished;
    public int mSwipeRate = 1000;
    private static ESystemInfo entance;

    private ESystemInfo() {
        ;
    }

    public void init(Context context) {
        mScaled = false;
        mFinished = false;
        DisplayMetrics dispm = context.getResources().getDisplayMetrics();
        mDisplayMetrics = dispm;
        mHeightPixels = dispm.heightPixels;
        mWidthPixels = dispm.widthPixels;
        mXdpi = dispm.xdpi;
        mYdpi = dispm.ydpi;
        mDensity = dispm.density;
        mDensityDpi = dispm.densityDpi;
        mScaledDensity = dispm.scaledDensity;
        mSysVersion = Build.VERSION.SDK_INT;
        mPhoneType = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType();
        mStatusBarHeight = getStateBarHeight(context);
        mViewHeight = mHeightPixels - mStatusBarHeight;
        cpuMHZ = getCPUFrequency();
        Build bd = new Build();
        String model = bd.MODEL;
        if("KBMC-709plus".equals(model) && mDensityDpi < 240){
            //for aibeibei adapt in pad "KBMC-709plus"
            mDensityDpi = 240;
        }
        switch (mDensityDpi) {
            case DisplayMetrics.DENSITY_LOW:
            {
                mDefaultFontSize = 14;
                mDefaultNatvieFontSize = 10;
                mDefaultzoom = ZoomDensity.CLOSE;
                mDefaultBounceHeight = 40;
            }
            break;
            case DisplayMetrics.DENSITY_MEDIUM:
            {
                mDefaultFontSize = 16;
                mDefaultNatvieFontSize = 13;
                mDefaultzoom = ZoomDensity.MEDIUM;
                mDefaultBounceHeight = 50;
            }
            break;
            case DisplayMetrics.DENSITY_HIGH:
            {
                mDefaultFontSize = 24;
                mDefaultNatvieFontSize = 16;
                mDefaultzoom = ZoomDensity.FAR;
                mDefaultBounceHeight = 60;
            }
            break;
            case 213: //DisplayMetrics.DENSITY_TV from 13
            {
                mDefaultFontSize = 32;
                mDefaultNatvieFontSize = 16;
                mDefaultzoom = ZoomDensity.FAR;
                mDefaultBounceHeight = 70;
            }
            break;
            case 320: //DisplayMetrics.DENSITY_XHIGH from 9
            {
                mDefaultFontSize = 32;
                mDefaultNatvieFontSize = 16;
                mDefaultzoom = ZoomDensity.FAR;
                mDefaultBounceHeight = 70;
            }
            break;
            case 480: //DisplayMetrics.DENSITY_XXHIGH from 16
            {
                mDefaultFontSize = 48;
                mDefaultNatvieFontSize = 17;
                mDefaultzoom = ZoomDensity.FAR;
                mDefaultBounceHeight = 105;
            }
            break;
            default:
            {
                mDefaultFontSize = 48;
                mDefaultNatvieFontSize = 17;
                mDefaultzoom = ZoomDensity.FAR;
                mDefaultBounceHeight = 105;

                if (mDensity > 3) { //适配更高密度设备

                    mDefaultFontSize = (int)(16 * mDensity);

                }
            }

            break;
        }
    }

    public static ESystemInfo getIntence() {
        if (null == entance) {
            entance = new ESystemInfo();
        }
        return entance;
    }

    private int getStateBarHeight(Context context){
        Class<?> classl;
        Object dimen;
        Field field;
        int dimenH = 0, height = 0;
        try{
            classl = Class.forName("com.android.internal.R$dimen");
            dimen = classl.newInstance();
            field = classl.getField("status_bar_height");
            dimenH = Integer.parseInt(field.get(dimen).toString());
            height = context.getResources().getDimensionPixelSize(dimenH);
        }catch(Exception e){
            ;
        }
        return height;
    }

    /**
     * 获得设备CPU的频率
     *
     * @return
     */
    private int getCPUFrequency() {
        int mhz = 0;
        LineNumberReader isr = null;
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
            isr = new LineNumberReader(new InputStreamReader(pp.getInputStream()));
            String line = isr.readLine();
            if (line != null && line.length() > 0) {
                try {
                    mhz = Integer.parseInt(line.trim()) / 1000;
                } catch (Exception e) {
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mhz;
    }
}
