package com.yanyanmm.agorartcsdkwx;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.BeautyOptions;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class AgoraRtcRoomLayout extends RelativeLayout {

    private static final String TAG = AgoraRtcRoomLayout.class.getSimpleName();

    private RtcEngine mRtcEngine = null;

    private OnEventListener mEventListener = null;

    private WeakReference<Activity> mActivity = null;

    private AgoraRtcRoomVideoContainer mVideoContainer = null;

    public AgoraRtcRoomLayout(Context context) {
        this(context, null);
    }

    public AgoraRtcRoomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgoraRtcRoomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mVideoContainer = new AgoraRtcRoomVideoContainer(context);
        mVideoContainer.setLayoutParams(new LayoutParams(-1, -1));
        this.addView(mVideoContainer);
    }

    public void addEventListener(OnEventListener eventListener) {
        this.mEventListener = eventListener;
    }

    /**
     * 设置视频默认大小
     * @param right
     * @param bottom
     * @param width
     * @param height
     */
    public void setVideoFrame(int right, int bottom, int width, int height) {
        mVideoContainer.setVideoFrame(right, bottom, width, height);
    }

    /**
     * 设置某个主播视频大小
     * @param uid
     * @param right
     * @param bottom
     * @param width
     * @param height
     */
    public void setVideoFrame(int uid, int right, int bottom, int width, int height) {
        mVideoContainer.setVideoFrame(uid, right, bottom, width, height);
    }

    /**
     * 初始化
     * @param activity
     * @param appid
     */
    public boolean init(Activity activity, String appid) {
        if (mRtcEngine == null) {
            mActivity = new WeakReference<>(activity);
            try {
                mRtcEngine = RtcEngine.create(getContext(), appid, mRtcEventHandler);
                mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
                mRtcEngine.enableVideo();
                mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                        new VideoEncoderConfiguration.VideoDimensions(),
                        VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                        VideoEncoderConfiguration.STANDARD_BITRATE,
                        VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
                ));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 在进入频道前启动本地视频预览
     */
    public void startPreview() {
        if (mRtcEngine != null) {
            mRtcEngine.startPreview();
        }
    }

    /**
     * 停止视频预览
     */
    public void stopPreview() {
        if (mRtcEngine != null) {
            mRtcEngine.stopPreview();
        }
    }

    /**
     * 打开直播
     * @param roomId
     * @param token
     * @param uid
     */
    public void openLive(String roomId, String token, int uid) {

        //加入频道
        joinChannel(roomId, token, uid);

        //上麦
        startBroadcast();
    }

    /**
     * 加入频道
     * @param roomId
     * @param token
     * @param uid
     */
    public void joinChannel(String roomId, String token, int uid) {
        if (mRtcEngine != null) {
            mRtcEngine.joinChannel(token, roomId, "", uid);
        }
    }

    /**
     * 离开频道
     */
    public void leaveChannel() {
        if (mRtcEngine != null) {
            List<Integer> uids = mVideoContainer.getUids();
            if (uids != null && uids.size() > 0) {
                for (int i = 0; i < uids.size(); i++) {
                    int uid = uids.get(i);
                    this.stopRtcVideo(uid);
                }
                mVideoContainer.clearAllVideo();
            }
            mRtcEngine.leaveChannel();
        }
    }

    /**
     * 设置美颜
     * @param enabled
     * @param contrastLevel
     * @param lightening
     * @param smoothness
     * @param redness
     */
    public void setBeauty(boolean enabled, int contrastLevel, int lightening, int smoothness, int redness) {
        if (mRtcEngine != null) {
            int level = contrastLevel >= 0 && contrastLevel <= 2 ? contrastLevel : 1;
            float light = (lightening >= 0 && lightening <= 100 ? lightening : 75) / 100.0f;
            float smooth = (smoothness >= 0 && smoothness <= 100 ? smoothness : 50) / 100.0f;
            float red = (redness >= 0 && redness <= 100 ? redness : 10) / 100.0f;
            mRtcEngine.setBeautyEffectOptions(enabled, new BeautyOptions(level, light, smooth, red));
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mRtcEngine != null) {
            mRtcEngine.switchCamera();
        }
    }

    /**
     * 停止/恢复发送本地音频流
     * @param muted
     */
    public void muteLocalAudioStream(boolean muted) {
        if (mRtcEngine != null) {
            mRtcEngine.muteLocalAudioStream(muted);
        }
    }

    /**
     * 停止/恢复接收指定音频流。
     * @param uid
     * @param muted
     */
    public void muteRemoteAudioStream(int uid, boolean muted) {
        if (mRtcEngine != null) {
            mRtcEngine.muteRemoteAudioStream(uid, muted);
        }
    }

    /**
     * 停止/恢复接收所有音频流
     * @param muted
     */
    public void muteAllRemoteAudioStreams(boolean muted) {
        if (mRtcEngine != null) {
            mRtcEngine.muteAllRemoteAudioStreams(muted);
        }
    }

    /**
     * 停止/恢复发送本地视频流
     * @param muted
     */
    public void muteLocalVideoStream(boolean muted) {
        if (mRtcEngine != null) {
            mRtcEngine.muteLocalVideoStream(muted);
        }
    }

    /**
     * 停止/恢复接收指定视频流。
     * @param uid
     * @param muted
     */
    public void muteRemoteVideoStream(int uid, boolean muted) {
        if (mRtcEngine != null) {
            mRtcEngine.muteRemoteVideoStream(uid, muted);
        }
    }

    /**
     * 停止/恢复接收所有视频流
     * @param muted
     */
    public void muteAllRemoteVideoStreams(boolean muted) {
        if (mRtcEngine != null) {
            mRtcEngine.muteAllRemoteVideoStreams(muted);
        }
    }

    /**
     * 上麦
     */
    public void startBroadcast() {
        if (mRtcEngine != null) {
            mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            openRtcVideo(0);
        }
    }

    /**
     * 下麦
     */
    public void stopBroadcast() {
        if (mRtcEngine != null) {
            mRtcEngine.setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
            stopRtcVideo(0);
        }
    }

    /**
     * 销毁
     */
    public void destory() {

        RtcEngine.destroy();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        destory();
    }

    protected void openRtcVideo(int uid) {
        SurfaceView surface = RtcEngine.CreateRendererView(getContext());
        if (uid == 0) {
            mRtcEngine.setupLocalVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        } else {
            mRtcEngine.setupRemoteVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
        mVideoContainer.addVideoSurface(surface, uid);
    }

    protected void stopRtcVideo(int uid) {
        if (uid == 0) {
            mRtcEngine.setupLocalVideo(null);
        } else {
            mRtcEngine.setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
        mVideoContainer.removeVideoSurface(uid);
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // 注册 onJoinChannelSuccess 回调。
        // 本地用户成功加入频道时，会触发该回调。
        public void onJoinChannelSuccess(final String channel, final int uid, final int elapsed) {
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mEventListener != null) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("channel", channel);
                            params.put("uid", uid);
                            params.put("elapsed", elapsed);
                            mEventListener.onEvent("onJoinChannelSuccess", params);
                        }
                    }
                });
            }
        }

        @Override
        public void onLeaveChannel(final IRtcEngineEventHandler.RtcStats stats) {
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mEventListener != null) {
                            Map params = JSONObject.parseObject(JSONObject.toJSONString(stats), Map.class);
                            mEventListener.onEvent("onLeaveChannel", params);
                        }
                    }
                });
            }
        }

        @Override
        // SDK 接收到第一帧远端视频并成功解码时，会触发该回调。
        // 可以在该回调中调用 setupRemoteVideo 方法设置远端视图。
        public void onRemoteVideoStateChanged(final int uid, final int state, final int reason, final int elapsed) {
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (state == Constants.REMOTE_VIDEO_STATE_STARTING) {
                            //设置远程视图
                            openRtcVideo(uid);
                        } else if (state == Constants.REMOTE_VIDEO_STATE_STOPPED) {
                            //移除远端视图
                            stopRtcVideo(uid);
                        }
                        if (mEventListener != null) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("state", state);
                            params.put("uid", uid);
                            params.put("reason", reason);
                            params.put("elapsed", elapsed);
                            mEventListener.onEvent("onRemoteVideoStateChanged", params);
                        }
                    }
                });
            }
        }

        @Override
        public void onUserJoined(final int uid, final int elapsed) {
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mEventListener != null) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("uid", uid);
                            params.put("elapsed", elapsed);
                            mEventListener.onEvent("onUserJoined", params);
                        }
                    }
                });
            }
        }

        @Override
        // 注册 onUserOffline 回调。
        // 远端主播离开频道或掉线时，会触发该回调。
        public void onUserOffline(final int uid, final int reason) {
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //移除远端视图
                        stopRtcVideo(uid);
                        if (mEventListener != null) {
                            Map<String, Object> params = new HashMap<>();
                            params.put("uid", uid);
                            params.put("reason", reason);
                            mEventListener.onEvent("onUserOffline", params);
                        }
                    }
                });
            }
        }

    };

}
