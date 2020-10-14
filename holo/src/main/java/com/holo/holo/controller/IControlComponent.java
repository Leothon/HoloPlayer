package com.holo.holo.controller;

import android.view.View;
import android.view.animation.Animation;

/**
 * Author: wangchengge
 * Date: 2020/10/14
 * Version: 1.0.0
 * Description:控制组件接口
 */
public interface IControlComponent {

    // 绑定控制类
    void attach(ControlWrapper controlWrapper);

    View getView();

    // 可见性改变
    void onVisibilityChanged(boolean isVisible, Animation anim);

    // 播放状态改变
    void onPlayStateChanged(int playState);

    // 播放器状态改变
    void onPlayerStateChanged(int playerState);

    // 设置进度
    void setProgress(int duration,int position);

    // 锁定状态变化
    void onLockStateChanged(boolean isLocked);
}
