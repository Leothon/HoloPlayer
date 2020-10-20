package com.holo.holo.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.holo.holo.R;
import com.holo.holo.controller.ControlWrapper;
import com.holo.holo.controller.IControlComponent;
import com.holo.holo.manager.HoloPlayerManager;
import com.holo.holo.player.HoloVideoPlayer;

/**
 * Author: wangchengge
 * Date: 2020/10/20
 * Version: 1.0.0
 * Description:播放出错视图
 */
public class ErrorView extends LinearLayout implements IControlComponent {

    private ControlWrapper controlWrapper;

    private TextView mMessage;
    private TextView mStatusBtn;
    private ImageView mIcon;

    private int mPlayState;

    private OnErrorActionListener mOnErrorActonListener;

    public ErrorView(Context context) {
        super(context);
        init();
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setVisibility(GONE);
        setBackgroundColor(Color.BLACK);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_error_view,this,true);
        mStatusBtn = findViewById(R.id.error_retry_btn);
        mMessage = findViewById(R.id.error_message);
        mIcon = findViewById(R.id.error_icon);
        mStatusBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(GONE);
                if (mPlayState == HoloVideoPlayer.STATE_START_ABORT) {
                    HoloPlayerManager.getInstance().setPlayOnMobileNetwork(true);
                    controlWrapper.start();
                } else if (mPlayState == HoloVideoPlayer.STATE_ERROR) {
                    if (mOnErrorActonListener != null) {
                        mOnErrorActonListener.onRetryClick();
                    }
                }
            }
        });
        setClickable(true);
    }

    public ErrorView setOnErrorRetryClickListener(OnErrorActionListener listener) {
        mOnErrorActonListener = listener;
        return this;
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
        mPlayState = playState;
        if (playState == HoloVideoPlayer.STATE_START_ABORT) {
            setVisibility(VISIBLE);
            mMessage.setText("当前网络为移动网络");
            mStatusBtn.setText("继续播放");
            // 设置移动网络警告符号
            //mIcon.setImageResource();
        } else if (playState == HoloVideoPlayer.STATE_ERROR) {
            setVisibility(VISIBLE);
            mMessage.setText("播放出错，请重试");
            mStatusBtn.setText("重试");
            // 设置出错符号
            //mIcon.setImageResource();
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

    public interface OnErrorActionListener {
        void onRetryClick();
    }
}
