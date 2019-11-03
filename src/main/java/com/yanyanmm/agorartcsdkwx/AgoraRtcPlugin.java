package com.yanyanmm.agorartcsdkwx;

import android.content.Context;

import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.common.WXException;

import io.dcloud.feature.weex.WeexInstanceMgr;

public class AgoraRtcPlugin {

    public AgoraRtcPlugin() {
    }

    public static void initPlugin(Context context) {
        try {
            WXSDKEngine.registerComponent("agora_rtc_room", AgoraRtcRoomComponent.class);
            WeexInstanceMgr.addWeexPluginNameForDebug("agora_rtc_room", AgoraRtcRoomComponent.class);
        } catch (WXException var2) {
            var2.printStackTrace();
        }
    }
}
