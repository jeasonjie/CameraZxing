package com.google.zxing.client.android;

import com.ace.zxing.ResultPoint;
import com.ace.zxing.ResultPointCallback;

/**
 * Created by Neusoft on 2016/4/19.
 */
public class ViewfinderResultPointCallback implements ResultPointCallback {

    private final ViewfinderView viewfinderView;

    ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        viewfinderView.addPossibleResultPoint(point);
    }
}
