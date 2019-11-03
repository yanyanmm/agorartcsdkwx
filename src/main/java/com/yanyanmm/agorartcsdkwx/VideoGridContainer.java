package com.yanyanmm.agorartcsdkwx;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class VideoGridContainer extends RelativeLayout {

    private static final int MAX_USER = 4;

    private SparseArray<ViewGroup> mUserViewList;
    private List<Integer> mUidList;

    public VideoGridContainer(Context context) {
        this(context, null);
    }

    public VideoGridContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoGridContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mUserViewList = new SparseArray<>(MAX_USER);
        mUidList = new ArrayList<>(MAX_USER);
    }

    public void addUserVideoSurface(int uid, SurfaceView surface, boolean isLocal) {
        if (surface == null) {
            return;
        }

        int id = -1;
        if (isLocal) {
            if (mUidList.contains(0)) {
                mUidList.remove((Integer) 0);
                mUserViewList.remove(0);
            }

            if (mUidList.size() == MAX_USER) {
                mUidList.remove(0);
                mUserViewList.remove(0);
            }
            id = 0;
        } else {
            if (mUidList.contains(uid)) {
                mUidList.remove((Integer) uid);
                mUserViewList.remove(uid);
            }

            if (mUidList.size() < MAX_USER) {
                id = uid;
            }
        }

        if (id == 0) mUidList.add(0, uid);
        else mUidList.add(uid);

        if (id != -1) {
            mUserViewList.append(uid, createVideoView(surface));

            requestGridLayout();
        }
    }

    private ViewGroup createVideoView(SurfaceView surface) {
        RelativeLayout layout = new RelativeLayout(getContext());

        layout.setId(surface.hashCode());

        RelativeLayout.LayoutParams videoLayoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        layout.addView(surface, videoLayoutParams);
        return layout;
    }

    public void removeUserVideo(int uid, boolean isLocal) {
        if (isLocal && mUidList.contains(0)) {
            mUidList.remove((Integer) 0);
            mUserViewList.remove(0);
        } else if (mUidList.contains(uid)) {
            mUidList.remove((Integer) uid);
            mUserViewList.remove(uid);
        }

        requestGridLayout();
    }

    private void requestGridLayout() {
        removeAllViews();
        layout(mUidList.size());
    }

    private void layout(int size) {
        RelativeLayout.LayoutParams[] params = getParams(size);
        for (int i = 0; i < size; i++) {
            addView(mUserViewList.get(mUidList.get(i)), params[i]);
        }
    }

    private RelativeLayout.LayoutParams[] getParams(int size) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        RelativeLayout.LayoutParams[] array =
                new RelativeLayout.LayoutParams[size];

        for (int i = 0; i < size; i++) {
            if (i == 0) {
                array[0] = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                array[0].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[0].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 1) {
                array[1] = new RelativeLayout.LayoutParams(width, height / 2);
                array[0].height = array[1].height;
                array[1].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(0)).getId());
                array[1].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else if (i == 2) {
                array[i] = new RelativeLayout.LayoutParams(width / 2, height / 2);
                array[i - 1].width = array[i].width;
                array[i].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(i - 1)).getId());
                array[i].addRule(RelativeLayout.ALIGN_TOP, mUserViewList.get(mUidList.get(i - 1)).getId());
            } else if (i == 3) {
                array[i] = new RelativeLayout.LayoutParams(width / 2, height / 2);
                array[0].width = width / 2;
                array[1].addRule(RelativeLayout.BELOW, 0);
                array[1].addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                array[1].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(0)).getId());
                array[1].addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                array[2].addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                array[2].addRule(RelativeLayout.RIGHT_OF, 0);
                array[2].addRule(RelativeLayout.ALIGN_TOP, 0);
                array[2].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(0)).getId());
                array[3].addRule(RelativeLayout.BELOW, mUserViewList.get(mUidList.get(1)).getId());
                array[3].addRule(RelativeLayout.RIGHT_OF, mUserViewList.get(mUidList.get(2)).getId());
            }
        }

        return array;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAllVideo();
    }

    public void clearAllVideo() {
        removeAllViews();
        mUserViewList.clear();
        mUidList.clear();
    }

}
