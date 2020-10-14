package com.holo.holo.controller;

import android.graphics.Bitmap;

/**
 * Author: wangchengge
 * Date: 2020/10/14
 * Version: 1.0.0
 * Description:媒体播放器控制接口
 */
public interface MediaPlayerControl {
    void start();

    void pause();

    long getDuration();

    long getDurationPosition();

    void seekTo(long pos);

    boolean isPlaying();

    int getBufferedPercentage();

    void startFullScreen();

    void stopFullScreen();

    boolean isFullScreen();

    void setMute(boolean isMute);

    boolean isMute();

    void setScreenScaleType(int screenScaleType);

    void setSpeed(float speed);

    float getSpeed();

    long getTcpSpeed();

    void replay(boolean resetPosition);

    void setMirrorRotation(boolean enable);

    Bitmap doScreenShot();

    int[] getVideoSize();

    void setRotation(float rotation);

    void startTinyScreen();

    void stopTinyScreen();

    boolean isTinyScreen();
}
