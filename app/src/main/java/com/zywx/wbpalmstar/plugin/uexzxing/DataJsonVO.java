package com.zywx.wbpalmstar.plugin.uexzxing;

import java.io.Serializable;

/**
 * Created by Neusoft on 2016/4/19.
 */
public class DataJsonVO implements Serializable {

    private static final long serialVersionUID = 8256431627503271706L;
    private String lineImg = null;//扫描时移动的光线
    private String pickBgImg = null;//扫描区域边框图片
    private String tipLabel = null;//扫描区下部提示语
    private String title = null;//头部中间文字

    public String getLineImg() {
        return lineImg;
    }

    public void setLineImg(String lineImg) {
        this.lineImg = lineImg;
    }

    public String getPickBgImg() {
        return pickBgImg;
    }

    public void setPickBgImg(String pickBgImg) {
        this.pickBgImg = pickBgImg;
    }

    public String getTipLabel() {
        return tipLabel;
    }

    public void setTipLabel(String tipLabel) {
        this.tipLabel = tipLabel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
