package com.holo.holo.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holo.holo.helper.OrientationHelper;
import com.holo.holo.manager.HoloPlayerManager;
import com.holo.holo.player.HoloVideoPlayer;
import com.holo.holo.utils.CommonUtils;

import java.util.Iterator;
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

    private Boolean mHasCutout;

    private int mCutoutHeight;

    // 是否开始刷新进度
    private boolean mIsStartProgress;

    // 开头任务是否需要执行
    private boolean mStartExtraNeed = false;

    protected LinkedHashMap<IControlComponent,Boolean> mControlComponents = new LinkedHashMap<>();

    private Animation mShowAnim;
    private Animation mHideAnim;

    private int mOrientation = 0;

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

    /**
     * 添加控制组件
     * @param component
     */
    public void addControlComponent(IControlComponent... component) {
        for (IControlComponent item : component) {
            addControlComponent(item,false);
        }
    }

    /**
     * 添加控制组件
     * @param controlComponent
     * @param isPrivate 如果是独有的，则不添加
     */
    public void addControlComponent(IControlComponent controlComponent,boolean isPrivate) {
        mControlComponents.put(controlComponent,isPrivate);
        if (controlWrapper != null) {
            controlComponent.attach(controlWrapper);
        }
        View view = controlComponent.getView();
        if (view != null && !isPrivate) {
            addView(view,0);
        }
    }

    public void removeControlComponent(IControlComponent controlComponent) {
        removeView(controlComponent.getView());
        mControlComponents.remove(controlComponent);
    }

    public void removeAllControlComponent() {
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            removeView(next.getKey().getView());
        }
        mControlComponents.clear();
    }

    public void removeAllPrivateComponents() {
        Iterator<Map.Entry<IControlComponent,Boolean>> it = mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<IControlComponent,Boolean> next = it.next();
            if (next.getValue()) {
                it.remove();
            }
        }
    }

    @CallSuper
    public void setPlayerState(int playerState) {
        handlePlayerStateChanged(playerState);
    }

    @CallSuper
    public void setPlayState(int playState) {
        handlePlayStateChanged(playState);
    }

    public void setDismissTimeout(int timeout) {
        if (timeout > 0) {
            mDefaultTimeout = timeout;
        }
    }

    @Override
    public void hide() {
        if (mShowing) {
            stopFadeOut();
            handleVisibilityChanged(false,mHideAnim);
            mShowing = false;
        }
    }

    @Override
    public void show() {
        if (!mShowing) {
            handleVisibilityChanged(true,mShowAnim);
            startFadeOut();
            mShowing = true;
        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    // 计时隐藏控制UI
    @Override
    public void startFadeOut() {
        stopFadeOut();
        postDelayed(mFadeOut,mDefaultTimeout);
    }

    @Override
    public void stopFadeOut() {
        removeCallbacks(mFadeOut);
    }

    @Override
    public void setLocked(boolean isLocked) {
        mIsLocking = isLocked;
        handleLockStateChanged(isLocked);
    }

    @Override
    public boolean isLocked() {
        return mIsLocking;
    }

    @Override
    public void startProgress() {
        if (mIsStartProgress) return;
        post(mShowProgress);
        mIsStartProgress = true;
    }

    @Override
    public void stopProgress() {
        if (!mIsStartProgress) return;
        removeCallbacks(mShowProgress);
        mIsStartProgress = false;
    }

    public void setAdaptCutout(boolean adaptCutout) {
        mAdaptCutout = adaptCutout;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkCutout();
    }

    private void checkCutout() {
        if (!mAdaptCutout) return;
        if (mActivity != null && mHasCutout == null) {
            mHasCutout = CommonUtils.allowDisplayToCutout(mActivity);
            if (mHasCutout) {
                mCutoutHeight = (int) CommonUtils.getStatusBarHeight(mActivity);
            }
        }
    }

    @Override
    public boolean hasCutout() {
        return mHasCutout != null && mHasCutout;
    }

    @Override
    public int getCutoutHeight() {
        return mCutoutHeight;
    }

    // 是否显示网络播放提示，在是移动网络且移动网络不让播放时候显示
    public boolean showNetWarning() {
        return CommonUtils.getNetworkType(getContext()) == CommonUtils.NETWORK_MOBILE && !HoloPlayerManager.getInstance().isPlayOnMobileNetwork();
    }

    /**
     * 开头额外处理？
     * @return
     */
    public boolean showStartExtra() {
        return mStartExtraNeed;
    }

    public void setStartExtraNeed(boolean done) {
        mStartExtraNeed = done;
    }


    protected void togglePlay() {
        controlWrapper.togglePlay();
    }

    protected void toggleFullScreen() {
        controlWrapper.toggleFullScreen(mActivity);
    }

    /**
     * 子类使用该方法进入全屏
     * @return
     */
    protected boolean startFullScreen() {
        if (mActivity == null || mActivity.isFinishing()) return false;
        setPlayerState(HoloVideoPlayer.PLAYER_FULL_SCREEN);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        controlWrapper.startFullScreen();
        return true;
    }

    protected boolean stopFullScreen() {
        if (mActivity == null || mActivity.isFinishing()) return false;
        controlWrapper.setPlayerState(HoloVideoPlayer.PLAYER_PENDING_NORMAL);
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        controlWrapper.stopFullScreen();
        return true;
    }

    public boolean onBackPressed() {
        return false;
    }

    /**
     * 用户焦点改变调用
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (controlWrapper.isPlaying() || controlWrapper.isFullScreen()){
            if (hasWindowFocus) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        orientationHelper.enable();
                    }
                },800);
            } else {
                orientationHelper.disable();
            }
        }
    }

    public void setEnableOrientation(boolean enableOrientation) {
        mEnableOrientation = enableOrientation;
    }

    @CallSuper
    @Override
    public void onOrientationChanged(int orientation) {
        if (mActivity == null || mActivity.isFinishing()) return;

        // 记录上一次放置的位置
        int lastOrientation = mOrientation;

        // 检测不到方向（手机平放，重置原始位置）
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            mOrientation = -1;
            return;
        }

        if (orientation > 350 || orientation < 10) {
            int o = mActivity.getRequestedOrientation();
            if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && lastOrientation == 0) return;
            if (mOrientation == 0) return;
            mOrientation = 0;
            // 手机回正
            onOrientationPortrait(mActivity);
        } else if (orientation > 80 && orientation < 100) {
            int o = mActivity.getRequestedOrientation();

            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 90) return;
            if (mOrientation == 90) return;
            mOrientation = 90;
            // 手机右横屏，反转横屏
            onOrientationReverseLandscape(mActivity);
        } else if (orientation > 260 && orientation < 280) {
            int o = mActivity.getRequestedOrientation();
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 270) return;
            if (mOrientation == 270) return;
            mOrientation = 270;
            // 手机左横屏，横屏
            mOrientation = 270;
            onOrientationLandScape(mActivity);
        }
    }

    /**
     * 竖屏
     * @param activity
     */
    protected void onOrientationPortrait(Activity activity) {
        if (mIsLocking) return;
        if (!mEnableOrientation) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        controlWrapper.stopFullScreen();
    }

    protected void onOrientationLandScape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (controlWrapper.isFullScreen()) {
            handlePlayerStateChanged(HoloVideoPlayer.PLAYER_FULL_SCREEN);
        } else {
            controlWrapper.startFullScreen();
        }
    }

    protected void onOrientationReverseLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        if (controlWrapper.isFullScreen()) {
            handlePlayerStateChanged(HoloVideoPlayer.PLAYER_FULL_SCREEN);
        } else {
            controlWrapper.startFullScreen();
        }
    }

    protected Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (controlWrapper.isPlaying()) {
                postDelayed(this,(long) ((100 - pos % 1000) / controlWrapper.getSpeed()));
            } else {
                mIsStartProgress = false;
            }
        }
    };

    private int setProgress() {
        int position = (int) controlWrapper.getCurrentPosition();
        int duration = (int) controlWrapper.getDuration();
        handleSetProgress(duration,position);
        return position;
    }

    private void handleSetProgress(int duration,int position) {
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent controlComponent = next.getKey();
            controlComponent.setProgress(duration,position);
        }
        setProgress(duration,position);
    }

    /**
     * 子类监听进度刷新
     * @param duration
     * @param position
     */
    protected void setProgress(int duration,int position) {

    }

    private void handleLockStateChanged(boolean isLocking) {
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent controlComponent = next.getKey();
            controlComponent.onLockStateChanged(isLocking);
        }
        onLockStateChanged(isLocking);
    }

    /**
     * 子类重写监听锁定状态改变的方法
     * @param isLocking
     */
    protected void onLockStateChanged(boolean isLocking) {

    }

    protected final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private void handleVisibilityChanged(boolean isVisible, Animation animation) {
        if (!mIsLocking) {
            for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
                IControlComponent controlComponent = next.getKey();
                controlComponent.onVisibilityChanged(isVisible,animation);
            }
        }
        onVisibilityChanged(isVisible,animation);
    }

    /**
     * 子类应该重写该方法监听显示和隐藏
     * @param isVisible
     * @param animation
     */
    protected void onVisibilityChanged(boolean isVisible,Animation animation) {

    }

    private void handlePlayStateChanged(int playState) {
        onPlayStateChanged(playState);
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent controlComponent = next.getKey();
            controlComponent.onPlayStateChanged(playState);
        }
    }

    @CallSuper
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case HoloVideoPlayer.STATE_IDLE:
                orientationHelper.disable();
                mOrientation = 0;
                mIsLocking = false;
                mShowing = false;
                break;
            case HoloVideoPlayer.STATE_PLAYBACK_COMPLETED:
                mIsLocking = false;
                mShowing = false;
                break;
            case HoloVideoPlayer.STATE_ERROR:
                mShowing = false;
                break;
        }
    }

    private void handlePlayerStateChanged(int playerState) {
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent controlComponent = next.getKey();
            controlComponent.onPlayerStateChanged(playerState);
        }
        onPlayerStateChanged(playerState);
    }

    @CallSuper
    protected void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case HoloVideoPlayer.PLAYER_NORMAL:
                if (mEnableOrientation) {
                    orientationHelper.enable();
                } else {
                    orientationHelper.disable();
                }
                if (hasCutout()) {
                    CommonUtils.adaptCutoutAboveAndroidP(getContext(),false);
                }
                break;
            case HoloVideoPlayer.PLAYER_FULL_SCREEN:
                orientationHelper.enable();
                if (hasCutout()) {
                    CommonUtils.adaptCutoutAboveAndroidP(getContext(),true);
                }
                break;
            case HoloVideoPlayer.PLAYER_TINY_SCREEN:
                orientationHelper.disable();
                break;
        }
    }
}
