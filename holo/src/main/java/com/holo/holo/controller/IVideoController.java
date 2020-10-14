package com.holo.holo.controller;

/**
 * Author: wangchengge
 * Date: 2020/10/14
 * Version: 1.0.0
 * Description:视频控制器的接口（基础）
 */
public interface IVideoController {

    /**
     * 开始控制视图自动隐藏倒计时
     */
    void startFadeOut();

    /**
     * 取消控制视图自动隐藏倒计时
     */
    void stopFadeOut();

    /**
     * 控制视图是否可见
     * @return
     */
    boolean isShowing();

    /**
     * 设置锁定状态
     */
    void setLocked(boolean isLocked);

    /**
     * 是否处于锁定状态
     * @return
     */
    boolean isLocked();

    /**
     * 开始刷新进度
     */
    void startProgress();

    /**
     * 停止刷新进度
     */
    void stopProgress();

    /**
     * 隐藏控制视图
     */
    void hide();

    /**
     * 显示控制视图
     */
    void show();

    /**
     * 是否需要适配刘海
     * @return
     */
    boolean hasCutout();

    /**
     * 获取刘海的高度
     * @return
     */
    int getCutoutHeight();
}
