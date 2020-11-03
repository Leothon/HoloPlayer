package com.holo.holo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holo.holo.controller.ControlWrapper;
import com.holo.holo.controller.IControlComponent;

/**
 * Author: wangchengge
 * Date: 2020/10/22
 * Version: 1.0.0
 * Description:准备播放页面
 */
public class PrePareView extends FrameLayout implements IControlComponent {

    public PrePareView(@NonNull Context context) {
        super(context);
    }

    public PrePareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PrePareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void attach(ControlWrapper controlWrapper) {

    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    @Override
    public void onPlayStateChanged(int playState) {

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
