package com.google.zxing.client.android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;

import com.ace.zxing.BarcodeFormat;
import com.ace.zxing.DecodeHintType;
import com.ace.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Neusoft on 2016/4/19.
 */
public class CaptureActivityHandler extends Handler {

    private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    private final CaptureActivity activity;
    private final DecodeThread decodeThread;
    private State state;
    private final CameraManager cameraManager;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    CaptureActivityHandler(CaptureActivity activity,
                           Collection<BarcodeFormat> decodeFormats,
                           Map<DecodeHintType,?> baseHints,
                           String characterSet,
                           CameraManager cameraManager) {
        this.activity = activity;
        decodeThread = new DecodeThread(activity, decodeFormats, baseHints, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if(message.what == EUExUtil.getResIdID("restart_preview")){
            restartPreviewAndDecode();
        }else if (message.what == EUExUtil.getResIdID("decode_succeeded")){
            state = State.SUCCESS;
            Bundle bundle = message.getData();
            Bitmap barcode = null;
            float scaleFactor = 1.0f;
            if (bundle != null) {
                byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0,
                            compressedBitmap.length, null);
                    // Mutable copy:
                    barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                }
                scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
            }
            activity.handleDecode((Result) message.obj, barcode, scaleFactor);
        }else if(message.what == EUExUtil.getResIdID("decode_failed")){
            // We're decoding as fast as possible, so when one decode fails, start another.
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(),
                    EUExUtil.getResIdID("decode"));
        }else if(message.what == EUExUtil.getResIdID("return_scan_result")){
            activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
            activity.finish();
        }else if(message.what == EUExUtil.getResIdID("launch_product_query")){
            String url = (String) message.obj;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setData(Uri.parse(url));

            ResolveInfo resolveInfo =
                    activity.getPackageManager().resolveActivity(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
            String browserPackageName = null;
            if (resolveInfo != null && resolveInfo.activityInfo != null) {
                browserPackageName = resolveInfo.activityInfo.packageName;
                Log.d(TAG, "Using browser in package " + browserPackageName);
            }

            // Needed for default Android browser / Chrome only apparently
            if ("com.android.browser".equals(browserPackageName) ||
                    "com.android.chrome".equals(browserPackageName)) {
                intent.setPackage(browserPackageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName);
            }
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
                Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
            }
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), EUExUtil.getResIdID("quit"));
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(EUExUtil.getResIdID("decode_succeeded"));
        removeMessages(EUExUtil.getResIdID("decode_failed"));
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), EUExUtil.getResIdID("decode"));
            activity.drawViewfinder();
        }
    }
}