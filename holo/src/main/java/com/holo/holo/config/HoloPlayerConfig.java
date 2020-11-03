package com.holo.holo.config;

import com.holo.holo.controller.OnVideoPlayListener;
import com.holo.holo.factory.PlayerFactory;
import com.holo.holo.factory.RenderViewFactory;
import com.holo.holo.manager.PlayerProgressManager;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 播放器配置
 */
public class HoloPlayerConfig {

    public static Builder newBuilder() {
        return new Builder();
    }

    public final boolean mPlayOnMobileNetwork;

    public final boolean mEnableOrientation;

    public final boolean mEnableAudioFocus;

    public final boolean mIsEnableLog;

    public final PlayerProgressManager mPlayerProgressManager;

    public final PlayerFactory mPlayerFactory;

    public final int mScreenScaleType;

    public final RenderViewFactory mRenderViewFactory;

    public final boolean mAdaptCutout;

    public OnVideoPlayListener mOnVideoPlayListener;

    private HoloPlayerConfig(Builder builder) {
        mPlayerFactory = builder.mPlayFactory;
        mPlayOnMobileNetwork = builder.mPlayOnMobileNetwork;
        mEnableAudioFocus = builder.mEnableAudioFocus;
        mEnableOrientation = builder.mEnableOrientation;
        mIsEnableLog = builder.mIsEnableLog;
        mPlayerProgressManager = builder.mPlayerProgressManager;
        mScreenScaleType = builder.mScreenScaleType;
        // TODO 需要切换不同的渲染view，SurfaceView还是TextureView
        mRenderViewFactory = builder.mRenderViewFactory;
        mAdaptCutout = builder.mAdaptCutout;
        mOnVideoPlayListener = builder.mOnVideoPlayListener;
    }


    public final static class Builder {
        private boolean mPlayOnMobileNetwork;
        private boolean mEnableOrientation;
        private boolean mEnableAudioFocus = true;
        private boolean mIsEnableLog;
        private PlayerProgressManager mPlayerProgressManager;
        private PlayerFactory mPlayFactory;
        private int mScreenScaleType;
        private RenderViewFactory mRenderViewFactory;
        private boolean mAdaptCutout = true;
        private OnVideoPlayListener mOnVideoPlayListener;

        /**
         * 是否监听横竖屏切换，默认监听
         * @param mEnableOrientation
         * @return
         */
        public Builder setEnableOrientation(boolean mEnableOrientation) {
            this.mEnableOrientation = mEnableOrientation;
            return this;
        }

        /**
         * 移动网络下是否播放，默认不播放
         * @param mPlayOnMobileNetwork
         */
        public Builder setPlayOnMobileNetwork(boolean mPlayOnMobileNetwork) {
            this.mPlayOnMobileNetwork = mPlayOnMobileNetwork;
            return this;
        }

        /**
         * 设置是否开启audiofocus，默认开启
         * @param mEnableAudioFocus
         * @return
         */
        public Builder setEnableAudioFocus(boolean mEnableAudioFocus) {
            this.mEnableAudioFocus = mEnableAudioFocus;
            return this;
        }

        /**
         * 设置进度管理器，用来保存和获取进度
         * @param mPlayerProgressManager
         * @return
         */
        public Builder setPlayerProgressManager(PlayerProgressManager mPlayerProgressManager) {
            this.mPlayerProgressManager = mPlayerProgressManager;
            return this;
        }

        /**
         * 是否打印日志
         * @param mIsEnableLog
         * @return
         */
        public Builder setIsEnableLog(boolean mIsEnableLog) {
            this.mIsEnableLog = mIsEnableLog;
            return this;
        }

        /**
         * 设置播放器核心
         * @param mPlayFactory
         * @return
         */
        public Builder setPlayFactory(PlayerFactory mPlayFactory) {
            this.mPlayFactory = mPlayFactory;
            return this;
        }

        /**
         * 设置播放器屏幕比例
         * @param mScreenScaleType
         * @return
         */
        public Builder setScreenScaleType(int mScreenScaleType) {
            this.mScreenScaleType = mScreenScaleType;
            return this;
        }

        /**
         * 设置渲染view
         * @param mRenderViewFactory
         * @return
         */
        public Builder setRenderViewFactory(RenderViewFactory mRenderViewFactory) {
            this.mRenderViewFactory = mRenderViewFactory;
            return this;
        }

        /**
         * 设置是否适配刘海屏
         * @param mAdaptCutout
         * @return
         */
        public Builder setAdaptCutout(boolean mAdaptCutout) {
            this.mAdaptCutout = mAdaptCutout;
            return this;
        }

        public Builder setOnVideoPlayListener(OnVideoPlayListener mOnVideoPlayListener) {
            this.mOnVideoPlayListener = mOnVideoPlayListener;
            return this;
        }

        public HoloPlayerConfig build() {
            return new HoloPlayerConfig(this);
        }
    }
}
