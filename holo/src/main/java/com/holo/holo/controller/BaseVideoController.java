package com.holo.holo.controller;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holo.holo.manager.HoloPlayerManager;
import com.holo.holo.utils.CommonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author: wangchengge
 * Date: 2020/10/14
 * Version: 1.0.0
 * Description:基础视频控制单元，实现基础的控制器接口，方向监控等
 */
public abstract class BaseVideoController extends FrameLayout implements  IVideoController, OrientationHelper.OnOrientationChangeListener {

    protected ControlWrapper controlWrapper;

    private Activity mActivity;

    protected boolean mShowing;

    protected boolean mIsLocking;

    // 播放视图隐藏超时
    protected int mDefaultTimeout = 4000;

    private boolean mEnableOrientation;

    protected OrientationHelper orientationHelper;

    private boolean mAdaptCutout;

    private boolean mHasCutout;

    private int mCutoutHeight;

    // 是否开始刷新进度
    private boolean mIsStartProgress;

    // 开头任务是否需要执行
    private boolean mStartExtraNeed = false;

    protected LinkedHashMap<IControlComponent,Boolean> mControlComponents = new LinkedHashMap<>();

    private Animation mShowAnim;
    private Animation mHideAnim;


    public BaseVideoController(@NonNull Context context) {
        super(context,null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        if (getLayoutId() != 0) {
            LayoutInflater.from(getContext()).inflate(getLayoutId(),this,true);
        }
        orientationHelper = new OrientationHelper(getContext().getApplicationContext());
        mEnableOrientation = HoloPlayerManager.getConfig().mEnableOrientation;
        mAdaptCutout = HoloPlayerManager.getConfig().mAdaptCutout;

        mShowAnim = new AlphaAnimation(0f,1f);
        mShowAnim.setDuration(300);
        mHideAnim = new AlphaAnimation(1f,0f);
        mHideAnim.setDuration(300);

        mActivity = CommonUtils.scanForActivity(getContext());
    }

    // 设置控制器的布局文件
    protected abstract int getLayoutId();

    /**
     * 将播放器和控制器绑定
     * @param mediaPlayer
     */
    @CallSuper
    public void setMediaPlayer(MediaPlayerControl mediaPlayer) {
        controlWrapper = new ControlWrapper(mediaPlayer,this);
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.attach(controlWrapper);
        }
        orientationHelper.setOnOrientationChangeListener(this);
    }

    
}
