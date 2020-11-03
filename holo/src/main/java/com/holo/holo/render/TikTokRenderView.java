package com.holo.holo.render;

import android.graphics.Bitmap;
import android.view.View;

import com.holo.holo.player.AbstractPlayer;
import com.holo.holo.player.HoloVideoPlayer;

/**
 * Author: wangchengge
 * Date: 2020/10/20
 * Version: 1.0.0
 * Description:抖音renderView，横屏视频默认显示，竖屏视频居中裁剪
 */
public class TikTokRenderView implements IRenderView{

    private IRenderView mProxyRenderView;

    TikTokRenderView(IRenderView renderView) {
        this.mProxyRenderView = renderView;
    }

    @Override
    public void attachToPlayer(AbstractPlayer abstractPlayer) {
        mProxyRenderView.attachToPlayer(abstractPlayer);
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoHeight > 0 && videoWidth > 0) {
            mProxyRenderView.setVideoSize(videoWidth,videoHeight);
            if (videoHeight > videoWidth) {
                mProxyRenderView.setScaleType(HoloVideoPlayer.SCREEN_SCALE_CENTER_CROP);
            } else {
                mProxyRenderView.setScaleType(HoloVideoPlayer.SCREEN_SCALE_DEFAULT);
            }
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        mProxyRenderView.setVideoRotation(degree);
    }

    @Override
    public void setScaleType(int scaleType) {

    }

    @Override
    public View getView() {
        return mProxyRenderView.getView();
    }

    @Override
    public Bitmap doScreenShot() {
        return mProxyRenderView.doScreenShot();
    }

    @Override
    public void release() {
        mProxyRenderView.release();
    }
}
