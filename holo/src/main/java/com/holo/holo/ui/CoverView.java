package com.holo.holo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.holo.holo.controller.ControlWrapper;
import com.holo.holo.controller.IControlComponent;
import com.holo.holo.player.HoloVideoPlayer;
import com.holo.holo.utils.ImageUtils;


/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 封面ui
 */
public class CoverView extends SimpleDraweeView implements IControlComponent {

    private int mScreenWidth;

    public CoverView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init(context);
    }

    public CoverView(Context context) {
        super(context);
        init(context);
    }

    public CoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CoverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CoverView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    public void setCover(String cover) {
        ImageUtils.loadImage(this,cover,mScreenWidth,mScreenWidth * 9 / 16);
    }

    @Override
    public void attach(ControlWrapper controlWrapper) {

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
        if (playState == HoloVideoPlayer.STATE_IDLE || playState == HoloVideoPlayer.STATE_PREPARING) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
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
