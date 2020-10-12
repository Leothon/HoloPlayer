package com.holo.holo.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
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
 * @Desc: 播放器实例，底层基于exoplayer，之后要进行替换，替换成自己编写的播放器
 * 似乎是将exoplayer包裹了一层，以便于我们更好的使用
 */
public class MediaPlayer extends AbstractPlayer implements VideoListener, Player.EventListener {

    private Context mApplicationContext;

    private MediaSourceHelper mMediaSourceHelper;
    private MediaSource mMediaSource;

    public SimpleExoPlayer mMediaInternalPlayer;    // 播放器核心
    private RenderersFactory mRenderersFactory;     // 渲染工厂
    private TrackSelector mTrackSelector;           // 音轨选择器
    private LoadControl mLoadControl;               // 播放流控制

    private PlaybackParameters mPlaybackParameters; // 可能是设置播放速度的参数

    private int mLastReportedPlaybackState = Player.STATE_IDLE; // 最后报告的播放器状态？
    private boolean mLastReportedPlayWhenReady = false;         // 最后报告的 当准备好是否播放
    private boolean mIsPreparing;                               // 是否做好了准备
    private boolean mIsBuffering;                               // 是否缓冲

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

    /**
     * 设置音轨选择
     * @param mTrackSelector
     */
    public void setTrackSelector(TrackSelector mTrackSelector) {
        this.mTrackSelector = mTrackSelector;
    }

    /**
     * 设置渲染工厂
     * @param mRenderersFactory
     */
    public void setRenderersFactory(RenderersFactory mRenderersFactory) {
        this.mRenderersFactory = mRenderersFactory;
    }

    /**
     * 设置播放控制流
     * @param mLoadControl
     */
    public void setLoadControl(LoadControl mLoadControl) {
        this.mLoadControl = mLoadControl;
    }

    /**
     * 设置播放资源
     * @param url  地址链接
     * @param headers 播放请求头
     */
    @Override
    public void setDataSource(String url, Map<String, String> headers) {
        mMediaSource = mMediaSourceHelper.getMediaSource(url,headers);
    }

    /**
     * 设置播放资源
     * @param url 地址链接
     */
    @Override
    public void setDataSource(String url) {
        mMediaSource = mMediaSourceHelper.getMediaSource(url);
    }

    /**
     * @param afd 从本地中拿到source
     */
    @Override
    @Deprecated
    public void setDataSource(AssetFileDescriptor afd) {
        mMediaSource = mMediaSourceHelper.getMediaSource(afd);
    }

    /**
     * 直接设置source资源
     * @param mDataSource
     */
    public void setDataSource(MediaSource mDataSource) {
        mMediaSource = mDataSource;
    }

    @Override
    public void start() {
        if (mMediaInternalPlayer == null) {
            return;
        }
        // 当准备好就开始播放
        mMediaInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        if (mMediaInternalPlayer == null) {
            return;
        }
        mMediaInternalPlayer.setPlayWhenReady(false);
    }

    @Override
    public void stop() {
        if (mMediaInternalPlayer == null) {
            return;
        }
        mMediaInternalPlayer.stop();
    }

    /**
     * 异步准备
     */
    @Override
    public void prepareAsync() {
        if (mMediaInternalPlayer == null) {
            return;
        }
        if (mMediaSource == null) return;
        if (mPlaybackParameters != null) {
            mMediaInternalPlayer.setPlaybackParameters(mPlaybackParameters);
        }
        mIsPreparing = true;
        //为什么加一个handler
        mMediaSource.addEventListener(new Handler(),mediaSourceEventListener);
        mMediaInternalPlayer.prepare(mMediaSource);
    }

    /**
     * 似乎是监听资源
     */
    private MediaSourceEventListener mediaSourceEventListener = new MediaSourceEventListener() {
        @Override
        public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            // 资源读取开始？
            if (mPlayerEventListener != null && mIsPreparing) {
                mPlayerEventListener.onPrepared();
            }
        }
    };

    @Override
    public void reset() {
        if (mMediaInternalPlayer != null) {
            mMediaInternalPlayer.stop(true);
            mMediaInternalPlayer.setVideoSurface(null);
            mIsPreparing = false;
            mIsBuffering = false;
            mLastReportedPlaybackState = Player.STATE_IDLE;
            mLastReportedPlayWhenReady = false;
        }
    }

    @Override
    public void release() {
        if (mMediaInternalPlayer != null) {
            mMediaInternalPlayer.removeListener(this);
            mMediaInternalPlayer.removeVideoListener(this);
            mMediaInternalPlayer.release();
            mMediaInternalPlayer = null;
        }
        mIsBuffering = false;
        mIsPreparing = false;
        mLastReportedPlaybackState = Player.STATE_IDLE;
        mLastReportedPlayWhenReady = false;
        mPlaybackParameters = null;
    }

    @Override
    public boolean isPlaying() {
        if (mMediaInternalPlayer != null) {
            return false;
        }
        int state = mMediaInternalPlayer.getPlaybackState();
        switch (state) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                return mMediaInternalPlayer.getPlayWhenReady();
            case Player.STATE_IDLE:
            case Player.STATE_ENDED:
            default:
                return false;
        }
    }

    @Override
    public void seekTo(long time) {
        if (mMediaInternalPlayer == null) {
            return;
        }
        mMediaInternalPlayer.seekTo(time);
    }

    @Override
    public long getCurrentPosition() {
        return mMediaInternalPlayer == null ? 0 : mMediaInternalPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaInternalPlayer == null ? 0 : mMediaInternalPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return mMediaInternalPlayer == null ? 0 : mMediaInternalPlayer.getBufferedPercentage();
    }

    /**
     * 设置textureView作为画布
     * @param surface
     */
    @Override
    public void setTextureView(Surface surface) {
        if (mMediaInternalPlayer != null) {
            mMediaInternalPlayer.setVideoSurface(surface);
        }
    }

    /**
     * 设置surfaceview为画布
     * @param surfaceHolder
     */
    @Override
    public void setSurfaceView(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            setTextureView(null);
        }else {
            setTextureView(surfaceHolder.getSurface());
        }
    }

    @Override
    public void setVolume(float v1, float v2) {
        if (mMediaInternalPlayer != null) {
            mMediaInternalPlayer.setVolume((v1 + v2) / 2);
        }
    }

    @Override
    public void setLooping(boolean isLooping) {
        if (mMediaInternalPlayer != null) {
            mMediaInternalPlayer.setRepeatMode(isLooping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
        }
    }

    @Override
    public void setOptions() {
        // 当准备好就开始播放
        mMediaInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public void setPlaySpeed(float speed) {
        // 似乎是播放参数，此处传入播放速度
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        mPlaybackParameters = playbackParameters;
        if (mMediaInternalPlayer != null) {
            mMediaInternalPlayer.setPlaybackParameters(playbackParameters);
        }
    }

    @Override
    public float getPlaySpeed() {
        return mPlaybackParameters == null ? 1f : mPlaybackParameters.speed;
    }

    @Override
    public long getNetSpeed() {
        return 0;
    }

    /**
     * 播放状态改变监听
     * @param playWhenReady
     * @param playbackState
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mPlayerEventListener == null) return;
        if (mIsPreparing) return;
        if (mLastReportedPlayWhenReady != playWhenReady || mLastReportedPlaybackState != playbackState) {
            // 当状态改变，根据状态更改自定义的回调接口
            switch (playbackState) {
                case Player.STATE_BUFFERING:
                    mPlayerEventListener.onInfo(MEDIA_INFO_BUFFERING_START,getBufferedPercentage());
                    break;
                case Player.STATE_READY:
                    if (mIsBuffering) {
                        mPlayerEventListener.onInfo(MEDIA_INFO_BUFFERING_END,getBufferedPercentage());
                        mIsBuffering = false;
                    }
                    break;
                case Player.STATE_ENDED:
                    mPlayerEventListener.onCompletion();
                    break;
            }
            mLastReportedPlaybackState = playbackState;
            mLastReportedPlayWhenReady = playWhenReady;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onSizeChanged(width,height);
            if (unappliedRotationDegrees > 0) {
                // 视频旋转
                mPlayerEventListener.onInfo(MEDIA_INFO_VIDEO_ROTATION_CHANGED,unappliedRotationDegrees);
            }
        }
    }

    @Override
    public void onRenderedFirstFrame() {
        if (mPlayerEventListener != null && mIsPreparing) {
            mPlayerEventListener.onInfo(MEDIA_INFO_VIDEO_RENDERING_START,0);
            mIsPreparing = false;
        }
    }
}
