package com.yanyanmm.agorartcsdkwx;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.ui.action.BasicComponentData;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgoraRtcRoomComponent extends WXComponent<AgoraRtcRoomLayout> {

    private static final int PERMISSION_REQ_ID = 32;

    private static final String Event_OnInit   = "onInit";

    private JSCallback mJsCallback = null;

    public AgoraRtcRoomComponent(WXSDKInstance instance, WXVContainer parent, BasicComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    @Override
    public AgoraRtcRoomLayout initComponentHostView(@NonNull Context context) {
        AgoraRtcRoomLayout roomLayout = new AgoraRtcRoomLayout(context);
        roomLayout.setLayoutParams(new AgoraRtcRoomLayout.LayoutParams(-1, -1));
        roomLayout.addEventListener(new OnEventListener() {
            @Override
            public void onEvent(String event, Map<String, Object> params) {
                if (mJsCallback != null) {
                    JSONObject data = new JSONObject();
                    data.put("type", event);
                    data.put("data", params);
                    mJsCallback.invokeAndKeepAlive(data);
                } else {
                    fireEvent(event, params);
                }
            }
        });
        return roomLayout;
    }

    @WXComponentProp(name = "appid")
    public void setAppid(String appid) {
        if (getHostView().init(getActivity(), appid)) {
            fireEvent(Event_OnInit);
        }
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
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults != null && grantResults.length > 0) {
                for (int ret : grantResults) {
                    if (PackageManager.PERMISSION_GRANTED != ret) {
                        showToast("用户没有允许需要的权限，使用可能会受到限制！");
                    }
                }
            }
        }
    }

    @JSMethod
    public void setJsCallback(JSCallback jsCallback) {
        mJsCallback = jsCallback;
    }

    @JSMethod
    public void startPreview() {
        getHostView().startPreview();
    }

    @JSMethod
    public void stopPreview() {
        getHostView().stopPreview();
    }

    @JSMethod
    public void joinChannel(String roomId, String token, int uid) {
        getHostView().joinChannel(roomId, token, uid);
    }

    @JSMethod
    public void startBroadcast(JSCallback jsCallback) {
        boolean ret = checkPermission();
        if (ret) {
            getHostView().startBroadcast();
        }
        if (jsCallback != null) {
            jsCallback.invoke(ret ? "1" : "0");
        }
    }

    @JSMethod
    public void stopBroadcast() {
        getHostView().stopBroadcast();
    }

    @JSMethod
    public void leaveChannel() {
        getHostView().leaveChannel();
    }

    @JSMethod
    public void setUserLinkVideoFrame(int uid, int right, int bottom, int width, int height) {
        getHostView().setVideoFrame(uid, right, bottom, width, height);
    }

    @JSMethod
    public void setBeauty(boolean enabled, int contrastLevel, int lightening, int smoothness, int redness) {
        getHostView().setBeauty(enabled, contrastLevel, lightening, smoothness, redness);
    }

    @JSMethod
    public void switchCamera() {
        getHostView().switchCamera();
    }

    @JSMethod
    public void muteLocalAudioStream(boolean muted) {
        getHostView().muteLocalAudioStream(muted);
    }

    @JSMethod
    public void muteRemoteAudioStream(int uid, boolean muted) {
        getHostView().muteRemoteAudioStream(uid, muted);
    }

    @JSMethod
    public void muteAllRemoteAudioStreams(boolean muted) {
        getHostView().muteAllRemoteAudioStreams(muted);
    }

    @JSMethod
    public void muteLocalVideoStream(boolean muted) {
        getHostView().muteLocalVideoStream(muted);
    }

    @JSMethod
    public void muteRemoteVideoStream(int uid, boolean muted) {
        getHostView().muteRemoteVideoStream(uid, muted);
    }

    @JSMethod
    public void muteAllRemoteVideoStreams(boolean muted) {
        getHostView().muteAllRemoteVideoStreams(muted);
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        if (mJsCallback != null) {
            mJsCallback.invoke(0);
            mJsCallback = null;
        }
        getHostView().leaveChannel();
        getHostView().destory();
    }

    /**
     * 获取当前activity
     * @return
     */
    private Activity getActivity() {
        if (getInstance().getContext() instanceof Activity) {
            return (Activity)getInstance().getContext();
        }
        return null;
    }

    /**
     * 冒泡提示
     * @param msg
     */
    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
