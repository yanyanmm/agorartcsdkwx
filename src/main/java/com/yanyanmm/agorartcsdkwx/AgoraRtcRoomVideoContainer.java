package com.yanyanmm.agorartcsdkwx;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class AgoraRtcRoomVideoContainer extends FrameLayout {

    private SparseArray<SurfaceView> mVideoViews;
    private SparseArray<LayoutParams> mLayoutParams;
    private List<Integer> mUids;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mRightMargin;
    private int mBottomMargin;

    public AgoraRtcRoomVideoContainer(Context context) {
        this(context, null);
    }

    public AgoraRtcRoomVideoContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgoraRtcRoomVideoContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mVideoViews = new SparseArray<>();
        mLayoutParams = new SparseArray<>();
        mUids = new ArrayList<>();

        mVideoWidth = dp2px(150);
        mVideoHeight = dp2px(200);
        mRightMargin = dp2px(10);
        mBottomMargin = dp2px(10);
    }

    public void addVideoSurface(SurfaceView surface, Integer uid) {
        if (surface != null) {
            removeVideoSurface(uid);
            if (uid == 0) {
                mUids.add(0, uid);
            } else {
                mUids.add(uid);
            }
            mVideoViews.append(uid, surface);
            addView(surface);

            requestLayoutViews();
        }
    }

    public void removeVideoSurface(Integer uid) {
        removeVideoSurface(uid, true);
    }

    public void clearAllVideo() {
        removeAllViews();
        mUids.clear();
        mVideoViews.clear();
        mVideoViews.clear();
    }

    public List<Integer> getUids() {
        return mUids;
    }

    public void setVideoFrame(int right, int bottom, int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
        mRightMargin = right;
        mBottomMargin = bottom;
    }

    public void setVideoFrame(Integer uid, int right, int bottom, int width, int height) {
        if (mUids.contains(uid)) {
            LayoutParams params = mLayoutParams.get(uid);
            if (params != null) {
                params.width = width;
                params.height = height;
                params.rightMargin = right;
                params.bottomMargin = bottom;
                SurfaceView videoView = mVideoViews.get(uid);
                if (videoView != null) {
                    videoView.setLayoutParams(params);
                }
            }
        }
    }

    private void removeVideoSurface(Integer uid, boolean layout) {
        if (mUids.contains(uid)) {
            SurfaceView videoView = mVideoViews.get(uid);
            if (videoView != null) {
                removeView(videoView);
            }
            mUids.remove(uid);
            mVideoViews.remove(uid);
            mLayoutParams.remove(uid);
        }

        if (layout) {
            requestLayoutViews();
        }
    }

    private void requestLayoutViews() {
        for (int i = 0; i < mUids.size(); i++) {
            Integer uid = mUids.get(i);
            SurfaceView videoView = mVideoViews.get(uid);
            if (videoView != null) {
                LayoutParams params = mLayoutParams.get(uid);
                if (params == null) {
                    if (uid == 0) {
                        params = new LayoutParams(-1, -1);
                    } else {
                        params = new LayoutParams(mVideoWidth, mVideoHeight);
                        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                        params.rightMargin = mRightMargin;
                        params.bottomMargin = mBottomMargin + (i - 1) * (mBottomMargin + mBottomMargin);
                        videoView.setZOrderOnTop(true);
                    }
                    mLayoutParams.append(uid, params);
                }
                videoView.setLayoutParams(params);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAllVideo();
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(1, dp, getResources().getDisplayMetrics());
    }
}
