package com.holo.holo.player;

import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Map;

/**
 * @Author: a10943
 * @Date: 2020/9/22
 * @Desc: 播放器抽象
 */
public abstract class AbstractPlayer {

    /**
     * 开始渲染视频画面
     */
    public static final int MEDIA_INFO_VIDEO_RENDERING_START = 1;

    /**
     * 缓冲开始
     */
    public static final int MEDIA_INFO_BUFFERING_START = 101;

    /**
     * 缓冲结束
     */
    public static final int MEDIA_INFO_BUFFERING_END = 102;

    /**
     * 视频旋转信息
     */
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;

    /**
     * 播放器状态监听
     */
    public PlayerEventListener mPlayerEventListener;
    /**
     * 初始化播放器
     */
    public abstract void initPlayer();

    /**
     * 设置播放地址
     * @param url  地址链接
     * @param headers 播放请求头
     */
    public abstract void setDataSource(String url, Map<String,String> headers);

    /**
     * 设置播放地址
     * @param url 地址链接
     */
    public abstract void setDataSource(String url);

    /**
     * 设置播放地址
     * @param afd 从本地中拿到source
     */
    public abstract void setDataSource(AssetFileDescriptor afd);

    /**
     * 开始播放
     */
    public abstract void start();

    /**
     * 暂停播放
     */
    public abstract void pause();

    /**
     * 停止播放
     */
    public abstract void stop();

    /**
     * 准备开始播放
     */
    public abstract void prepareAsync();

    /**
     * 重置播放器
     */
    public abstract void reset();

    /**
     * 释放播放器
     */
    public abstract void release();

    /**
     * 是否正在播放
     * @return
     */
    public abstract boolean isPlaying();

    /**
     * 进度条控制
     * @param time 进度条跳转的位置
     */
    public abstract void seekTo(long time);

    /**
     * 获取当前播放位置
     * @return
     */
    public abstract long getCurrentPosition();

    /**
     * 获取总时长
     * @return
     */
    public abstract long getDuration();

    /**
     * 获取缓冲百分比
     * @return
     */
    public abstract int getBufferedPercentage();

    /**
     * 设置渲染的view(设置textureView)
     * @param surface
     */
    public abstract void setTextureView(Surface surface);

    /**
     * 设置渲染的view(设置surfaceView)
     * @param surfaceHolder
     */
    public abstract void setSurfaceView(SurfaceHolder surfaceHolder);

    /**
     * 设置音量
     * @param v1
     * @param v2
     */
    public abstract void setVolume(float v1,float v2);

    /**
     * 设置是否循环播放
     * @param isLooping
     */
    public abstract void setLooping(boolean isLooping);

    /**
     * 设置播放器配置
     */
    public abstract void setOptions();

    /**
     * 设置播放速度
     * @param speed
     */
    public abstract void setPlaySpeed(float speed);

    /**
     * 获得播放速度
     * @return
     */
    public abstract float getPlaySpeed();

    /**
     * 获取当前的网速
     * @return
     */
    public abstract long getNetSpeed();

    /**
     * 绑定播放器状态监听
     * @param playerEventListener
     */
    public void setPlayerEventListener(PlayerEventListener playerEventListener){
        this.mPlayerEventListener = playerEventListener;
    }

    public interface PlayerEventListener{
        void onError();
        void onCompletion();
        void onInfo(int what,int extra);
        void onPrepared();
        void onSizeChanged(int width,int height);
    }
}
