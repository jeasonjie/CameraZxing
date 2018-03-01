package com.google.zxing.client.android.result;

import android.app.Activity;

import com.ace.zxing.Result;
import com.ace.zxing.client.result.ParsedResult;
import com.zywx.wbpalmstar.engine.universalex.EUExUtil;

/**
 * Created by Neusoft on 2016/4/19.
 */
public class TextResultHandler extends ResultHandler {

    private static final int[] buttons = {
            EUExUtil.getResStringID("button_web_search"),
            EUExUtil.getResStringID("button_share_by_email"),
            EUExUtil.getResStringID("button_share_by_sms"),
            EUExUtil.getResStringID("button_custom_product_search"),
    };

    public TextResultHandler(Activity activity, ParsedResult result, Result rawResult) {
        super(activity, result, rawResult);
    }

    @Override
    public int getButtonCount() {
        return buttons.length - 1;
    }

    @Override
    public int getButtonText(int index) {
        return buttons[index];
    }

    @Override
    public void handleButtonPress(int index) {
        String text = getResult().getDisplayResult();
        switch (index) {
            case 0:
                webSearch(text);
                break;
            case 1:
                shareByEmail(text);
                break;
            case 2:
                shareBySMS(text);
                break;
            case 3:
                openURL(null);
                break;
        }
    }

    @Override
    public int getDisplayTitle() {
        return EUExUtil.getResStringID("result_text");
    }
}
