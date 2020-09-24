package com.holo.holo.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.holo.holo.manager.HoloPlayerManager;
import com.holo.holo.helper.MediaSourceHelper;

import java.util.Map;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 播放器实例
 */
public class MediaPlayer extends AbstractPlayer implements VideoListener, Player.EventListener {

    private Context mApplicationContext;

    private MediaSourceHelper mMediaSourceHelper;
    private MediaSource mMediaSource;

    public SimpleExoPlayer mMediaInternalPlayer;    // 播放器核心
    private RenderersFactory mRenderersFactory;     // 渲染工厂
    private TrackSelector mTrackSelector;           // 音轨选择器
    private LoadControl mLoadControl;               // 播放流控制

    public MediaPlayer(Context context){
        mApplicationContext = context.getApplicationContext();
        mMediaSourceHelper = MediaSourceHelper.getInstance(context);
    }

    @Override
    public void initPlayer() {
        mMediaInternalPlayer = new SimpleExoPlayer.Builder(
                mApplicationContext,
                mRenderersFactory == null ? mRenderersFactory = new DefaultRenderersFactory(mApplicationContext) : mRenderersFactory,
                mTrackSelector == null ? mTrackSelector = new DefaultTrackSelector(mApplicationContext) : mTrackSelector,
                mLoadControl == null ? mLoadControl = new DefaultLoadControl() : mLoadControl,
                DefaultBandwidthMeter.getSingletonInstance(mApplicationContext),                       // 带宽控制
                Util.getLooper(),
                new AnalyticsCollector(Clock.DEFAULT),                                                 // 分析收集器
                true,
                Clock.DEFAULT)
                .build();
        setOptions();
        // 日志
        if (HoloPlayerManager.getConfig().mIsEnableLog && mTrackSelector instanceof MappingTrackSelector) {
            mMediaInternalPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mTrackSelector,"internal_player"));
        }

        mMediaInternalPlayer.addListener(this);
        mMediaInternalPlayer.addVideoListener(this);
    }

    @Override
    public void setDataSource(String url, Map<String, String> headers) {

    }

    @Override
    public void setDataSource(String url) {

    }

    @Override
    public void setDataSource(AssetFileDescriptor afd) {

    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void prepareAsync() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void release() {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void seekTo(long time) {

    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public int getBufferedPercentage() {
        return 0;
    }

    @Override
    public void setTextureView(Surface surface) {

    }

    @Override
    public void setSurfaceView(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void setVolume(float v1, float v2) {

    }

    @Override
    public void setLooping(boolean isLooping) {

    }

    @Override
    public void setOptions() {
        // 当准备好就开始播放
        mMediaInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public void setPlaySpeed(float speed) {

    }

    @Override
    public float getPlaySpeed() {
        return 0;
    }

    @Override
    public long getNetSpeed() {
        return 0;
    }
}
