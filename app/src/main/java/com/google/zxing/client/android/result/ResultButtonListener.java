package com.google.zxing.client.android.result;

import android.view.View;

/**
 * Created by Neusoft on 2016/4/19.
 */
public final class ResultButtonListener implements View.OnClickListener {

    private final ResultHandler resultHandler;
    private final int index;

    public ResultButtonListener(ResultHandler resultHandler, int index) {
        this.resultHandler = resultHandler;
        this.index = index;
    }

    @Override
    public void onClick(View view) {
        resultHandler.handleButtonPress(index);
    }
}
