package com.holo.holo.controller;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * Author: wangchengge
 * Date: 2020/10/14
 * Version: 1.0.0
 * Description:设备方向监听（横竖屏切换)
 * 每隔300毫秒检测一次，如果方向改变，就通过回调接口发送
 */
public class OrientationHelper extends OrientationEventListener {

    private long mLastTime;

    private OnOrientationChangeListener mOnOrientationChangeListener;
    public OrientationHelper(Context context) {
        super(context);
    }

    @Override
    public void onOrientationChanged(int i) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastTime < 300) return;
        if (mOnOrientationChangeListener != null) {
            mOnOrientationChangeListener.onOrientationChanged(i);
        }
        mLastTime = currentTime;
    }

    public interface OnOrientationChangeListener {
        void onOrientationChanged(int orientation);
    }

    public void setOnOrientationChangeListener(OnOrientationChangeListener onOrientationChangeListener) {
        mOnOrientationChangeListener = onOrientationChangeListener;
    }
}
