package com.google.zxing.client.android.result;

import com.ace.zxing.Result;
import com.ace.zxing.client.result.ParsedResult;
import com.ace.zxing.client.result.ResultParser;
import com.google.zxing.client.android.CaptureActivity;

/**
 * Created by Neusoft on 2016/4/19.
 */
public class ResultHandlerFactory {

    private ResultHandlerFactory() {
    }

    public static ResultHandler makeResultHandler(CaptureActivity activity, Result rawResult) {
        ParsedResult result = parseResult(rawResult);
        switch (result.getType()) {
            default:
                return new TextResultHandler(activity, result, rawResult);
        }
    }

    private static ParsedResult parseResult(Result rawResult) {
        return ResultParser.parseResult(rawResult);
    }
}
