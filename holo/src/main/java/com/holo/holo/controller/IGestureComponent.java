package com.holo.holo.controller;

/**
 * Author: wangchengge
 * Date: 2020/10/14
 * Version: 1.0.0
 * Description:手势控制接口
 */
public interface IGestureComponent extends IControlComponent{

    // 开始滑动
    void onStartSlide();

    // 停止滑动
    void onStopSlide();

    // 滑动调整进度
    void onPositionChange(int slidePosition,int currentPosition,int duration);

    // 滑动调整亮度
    void onBrightnessChange(int percent);

    // 滑动调整音量
    void onVolumeChange(int percent);
}
