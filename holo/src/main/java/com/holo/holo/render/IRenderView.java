package com.holo.holo.render;

import android.graphics.Bitmap;
import android.view.View;

import com.holo.holo.player.AbstractPlayer;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 渲染view的相关接口
 */
public interface IRenderView {

    /**
     * 渲染view和播放器绑定
     * @param abstractPlayer 播放器
     */
    void attachToPlayer(AbstractPlayer abstractPlayer);

    /**
     * 设置视频尺寸
     * @param videoWidth 视频宽度
     * @param videoHeight 视频高度
     */
    void setVideoSize(int videoWidth,int videoHeight);

    /**
     * 设置视频旋转角度
     * @param degree 旋转角度
     */
    void setVideoRotation(int degree);

    /**
     * 设置缩放类型
     * @param scaleType 缩放类型
     */
    void setScaleType(int scaleType);

    /**
     * 获得view实例
     * @return 返回渲染view实例
     */
    View getView();

    /**
     * 截图
     * @return 返回截图的bitmap
     */
    Bitmap doScreenShot();

    /**
     * 释放资源
     */
    void release();

}
