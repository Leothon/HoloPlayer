package com.holo.holo.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holo.holo.R;
import com.holo.holo.controller.ControlWrapper;
import com.holo.holo.controller.IGestureComponent;
import com.holo.holo.player.HoloVideoPlayer;

import static com.holo.holo.utils.CommonUtils.stringForTime;

/**
 * Author: wangchengge
 * Date: 2020/10/22
 * Version: 1.0.0
 * Description:手势控制视图
 */
public class GestureView extends FrameLayout implements IGestureComponent {


    public GestureView(@NonNull Context context) {
        super(context);
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ControlWrapper controlWrapper;

    private ImageView mIcon;
    private ProgressBar progressBar;
    private TextView textPercent;

    private LinearLayout centerContainer;

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_gesture_control_view,this,true);
        mIcon = findViewById(R.id.iv_icon);
        progressBar = findViewById(R.id.pro_percent);
        textPercent = findViewById(R.id.tv_percent);
        centerContainer = findViewById(R.id.center_container);
    }

    @Override
    public void onStartSlide() {
        controlWrapper.hide();
        centerContainer.setVisibility(VISIBLE);
        centerContainer.setAlpha(1f);
    }

    @Override
    public void onStopSlide() {
        centerContainer.animate()
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        centerContainer.setVisibility(GONE);
                    }
                }).start();
    }

    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {
        progressBar.setVisibility(GONE);
        if (slidePosition > currentPosition) {
            mIcon.setImageResource(R.drawable.ic_action_fast_forward);
        } else {
            mIcon.setImageResource(R.drawable.ic_action_fast_rewind);
        }
        textPercent.setText(String.format("%s/%s",stringForTime(slidePosition),stringForTime(duration)));
    }


    @Override
    public void onBrightnessChange(int percent) {
        progressBar.setVisibility(VISIBLE);
        mIcon.setImageResource(R.drawable.ic_action_brightness);
        textPercent.setText(percent + "%");
        progressBar.setProgress(percent);
    }



    @Override
    public void onVolumeChange(int percent) {
        progressBar.setVisibility(VISIBLE);
        if (percent <= 0) {
//            mIcon.setImageResource(R.drawable.);
        } else {
//            mIcon.setImageResource(R.drawable.);
        }
        textPercent.setText(percent + "%");
        progressBar.setProgress(percent);
    }

    @Override
    public void attach(ControlWrapper controlWrapper) {
        this.controlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        if (playState == HoloVideoPlayer.STATE_IDLE
                || playState == HoloVideoPlayer.STATE_START_ABORT
                || playState == HoloVideoPlayer.STATE_PREPARING
                || playState == HoloVideoPlayer.STATE_PREPARED
                || playState == HoloVideoPlayer.STATE_ERROR
                || playState == HoloVideoPlayer.STATE_PLAYBACK_COMPLETED
                || playState == HoloVideoPlayer.STATE_START_EXTRA) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void setProgress(int duration, int position) {

    }

    @Override
    public void onLockStateChanged(boolean isLocked) {

    }
}
