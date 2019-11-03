package com.yanyanmm.agorartcsdkwx;

import android.app.Application;

import io.dcloud.weex.AppHookProxy;

public class AgoraRtcAppProxy implements AppHookProxy {

    @Override
    public void onCreate(Application application) {
        //可写初始化触发逻辑
        AgoraRtcPlugin.initPlugin(application);

    }
}
