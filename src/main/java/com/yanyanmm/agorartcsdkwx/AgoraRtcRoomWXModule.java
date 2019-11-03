package com.yanyanmm.agorartcsdkwx;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AgoraRtcRoomWXModule extends WXSDKEngine.DestroyableModule {

    private static final int PERMISSION_REQ_ID = 32;

    //js回调函数
    private JSCallback mJsCallback = null;

    @JSMethod(uiThread = true)
    public void setCallback(JSCallback jsCallback) {
        mJsCallback = jsCallback;
    }

    @JSMethod(uiThread = true)
    public void joinTRTCRoom(JSONObject options) {
        if (checkPermission()) {
            getContext().startActivity(new Intent(getContext(), AgoraRtcRoomActivity.class));
        }
    }

    @JSMethod(uiThread = true)
    public void exitTRTCRoom(JSCallback jsCallback) {

    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityPause() {

    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Activity activity = getActivity();
            if (activity != null) {
                List<String> permissions = new ArrayList<>();
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)) {
                    permissions.add(Manifest.permission.CAMERA);
                }
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)) {
                    permissions.add(Manifest.permission.RECORD_AUDIO);
                }
                if (permissions.size() != 0) {
                    ActivityCompat.requestPermissions(activity, (String[]) permissions.toArray(new String[0]), PERMISSION_REQ_ID);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_ID:
                for (int ret : grantResults) {
                    if (PackageManager.PERMISSION_GRANTED != ret) {
                        showToast("用户没有允许需要的权限，使用可能会受到限制！");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void destroy() {

    }

    /**
     * 获取当前activity
     * @return
     */
    private Activity getActivity() {
        if (mWXSDKInstance.getContext() instanceof Activity) {
            return (Activity) mWXSDKInstance.getContext();
        }
        return null;
    }

    /**
     * 获取当前上下文
     * @return
     */
    private Context getContext() {
        return mWXSDKInstance.getContext();
    }

    /**
     * 冒泡提示
     * @param msg
     */
    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
