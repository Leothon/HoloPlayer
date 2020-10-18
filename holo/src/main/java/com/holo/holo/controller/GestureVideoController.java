package com.holo.holo.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holo.holo.manager.HoloPlayerManager;
import com.holo.holo.player.HoloVideoPlayer;
import com.holo.holo.utils.CommonUtils;

import java.util.Map;

/**
 * Author: wangchengge
 * Date: 2020/10/18
 * Version: 1.0.0
 * Description:手势操作控制器
 */
public abstract class GestureVideoController extends BaseVideoController implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener, View.OnTouchListener {

    // 手势检测器
    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;
    private boolean mIsGestureEnabled = true;
    private int mStreamVolume;
    private float mBrightness;
    private int mSeekPosition;
    private boolean mFirstTouch;
    private boolean mChangePosition;
    private boolean mChangeBrightness;
    private boolean mChangeVolume;

    private boolean mCanChangePosition = true;

    private boolean mEnableInNormal;

    private boolean mCanSlide;

    private int mCurPlayState;


    public GestureVideoController(@NonNull Context context) {
        super(context);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(getContext(),this);
        setOnTouchListener(this);
    }

    /**
     * 设置是否可以滑动调节进度
     * @param canChangePosition
     */
    public void setCanChangePosition(boolean canChangePosition) {
        mCanChangePosition = canChangePosition;
    }

    /**
     * 在竖屏模式下是否开始手势控制，默认关闭
     * @param enableInNormal
     */
    public void setEnableInNormal(boolean enableInNormal) {
        mEnableInNormal = enableInNormal;
    }

    /**
     * 是否开启手势控制
     * @param gestureEnabled
     */
    public void setGestureEnabled(boolean gestureEnabled) {
        mIsGestureEnabled = gestureEnabled;
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        if (playerState == HoloVideoPlayer.PLAYER_NORMAL) {
            mCanSlide = mEnableInNormal;
        } else if (playerState == HoloVideoPlayer.PLAYER_FULL_SCREEN) {
            mCanSlide = true;
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        mCurPlayState = playState;
    }

    /**
     * 是否播放中
     * @return
     */
    private boolean isInPlaybackState() {
        return controlWrapper != null
                && mCurPlayState != HoloVideoPlayer.STATE_ERROR
                && mCurPlayState != HoloVideoPlayer.STATE_IDLE
                && mCurPlayState != HoloVideoPlayer.STATE_PREPARING
                && mCurPlayState != HoloVideoPlayer.STATE_PREPARED
                && mCurPlayState != HoloVideoPlayer.STATE_START_ABORT
                && mCurPlayState != HoloVideoPlayer.STATE_PLAYBACK_COMPLETED;
    }

    /**
     * touch事件由手势控制接管
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        if (!isInPlaybackState()                               // 未播放状态
                   || !mIsGestureEnabled                       // 关闭了手势
                   || CommonUtils.isTouchEdge(getContext(),motionEvent)) { // 处于边沿
            return true;
        }
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Activity activity = CommonUtils.scanForActivity(getContext());
        if (activity == null) {
            mBrightness = 0;
        } else {
            mBrightness = activity.getWindow().getAttributes().screenBrightness;
        }
        mFirstTouch = true;
        mChangePosition = false;
        mChangeBrightness = false;
        mChangeVolume = false;
        return true;
    }

    /**
     * 单击，由controlWrapper来处理来显示UI
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (isInPlaybackState()) {
            controlWrapper.toggleShowState();
        }
        return true;
    }

    /**
     * 双击处理播放暂停
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        if (!isLocked() && isInPlaybackState()) togglePlay();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEventStart, MotionEvent motionEventEnd, float distanceX, float distanceY) {
        if (!isInPlaybackState() || !mIsGestureEnabled || !mCanSlide || isLocked() || CommonUtils.isTouchEdge(getContext(),motionEventStart)) {
            return true;
        }
        float deltaX = motionEventStart.getX() - motionEventEnd.getX();
        float deltaY = motionEventStart.getY() - motionEventEnd.getY();
        if (mFirstTouch) {
            // X > Y 判定为竖向滑动
            mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
            if (!mChangePosition) {
                // 滑动结束的X位置在右半屏，则调节声音，否则调节音量
                int halfScreenWidth = CommonUtils.getScreenWidth(getContext(),true) / 2;
                if (motionEventEnd.getX() > halfScreenWidth) {
                    mChangeVolume = true;
                } else {
                    mChangeBrightness = true;
                }
            }

            if (mChangePosition) {
                mChangePosition = mCanChangePosition;
            }

            if (mChangePosition || mChangeBrightness || mChangeVolume) {
                for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
                    IControlComponent controlComponent = next.getKey();
                    if (controlComponent instanceof IGestureComponent) {
                        // 滑动控件改变
                        ((IGestureComponent) controlComponent).onStartSlide();
                    }
                }
            }
            mFirstTouch = false;
        }

        if (mChangePosition) {
            slideToChangePosition(deltaX);
        } else if (mChangeBrightness) {
            slideToChangeBrightness(deltaY);
        } else if (mChangeVolume) {
            slideToChangeVolume(deltaY);
        }
        return true;
    }

    protected void slideToChangePosition(float deltaX) {
        deltaX = -deltaX;
        int width = getMeasuredWidth();
        int duration = (int) controlWrapper.getDuration();
        int currentPosition = (int) controlWrapper.getCurrentPosition();
        int position = (int) (deltaX / width * 120000 + currentPosition);
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent controlComponent = next.getKey();
            if (controlComponent instanceof IGestureComponent) {
                ((IGestureComponent) controlComponent).onPositionChange(position,currentPosition,duration);
            }
        }
    }

    protected void slideToChangeBrightness(float deltaY) {
        Activity activity = CommonUtils.scanForActivity(getContext());
        if (activity == null) return;
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int height = getMeasuredHeight();
        if (mBrightness == -1.0f) mBrightness = 0.5f;
        float brightness = deltaY * 2 / height * 1.0f + mBrightness;
        if (brightness < 0) {
            brightness = 0f;
        }
        if (brightness > 1.0f) brightness = 1.0f;
        int percent = (int) (brightness * 100);
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent controlComponent = next.getKey();
            if (controlComponent instanceof IGestureComponent) {
                ((IGestureComponent) controlComponent).onBrightnessChange(percent);
            }
        }
    }

    protected void slideToChangeVolume(float deltaY) {
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mStreamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) index = 0;
        int percent = (int) (index / streamMaxVolume * 100);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(int) index,0);
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent controlComponent = next.getKey();
            if (controlComponent instanceof IGestureComponent) {
                ((IGestureComponent) controlComponent).onVolumeChange(percent);
            }
        }
    }

    /**
     * 滑动结束事件处理
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mGestureDetector.onTouchEvent(event)) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    stopSlide();
                    if (mSeekPosition > 0) {
                        controlWrapper.seekTo(mSeekPosition);
                        mSeekPosition = 0;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    stopSlide();
                    mSeekPosition = 0;
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void stopSlide() {
        for (Map.Entry<IControlComponent,Boolean> next : mControlComponents.entrySet()) {
            IControlComponent controlComponent = next.getKey();
            if (controlComponent instanceof IGestureComponent) {
                ((IGestureComponent) controlComponent).onStopSlide();
            }
        }
    }
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }
}
