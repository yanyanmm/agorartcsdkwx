package com.yanyanmm.agorartcsdkwx;

import java.util.Map;

public interface OnEventListener {

    void onEvent(String event, Map<String, Object> params);
}
