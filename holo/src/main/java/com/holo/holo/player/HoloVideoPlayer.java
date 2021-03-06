package com.holo.holo.player;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holo.holo.config.HoloPlayerConfig;
import com.holo.holo.controller.BaseVideoController;
import com.holo.holo.controller.MediaPlayerControl;
import com.holo.holo.controller.OnVideoPlayListener;
import com.holo.holo.factory.PlayerFactory;
import com.holo.holo.helper.AudioFocusHelper;
import com.holo.holo.manager.HoloPlayerManager;
import com.holo.holo.manager.PlayerProgressManager;
import com.holo.holo.render.IRenderView;
import com.holo.holo.render.RenderViewFactory;
import com.holo.holo.utils.CommonUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: holo播放器(核心和渲染器）
 */
public class HoloVideoPlayer<P extends AbstractPlayer> extends FrameLayout implements MediaPlayerControl,AbstractPlayer.PlayerEventListener {

    protected P mMediaPlayer;
    protected PlayerFactory<P> mPlayerFactory;
    protected BaseVideoController mVideoController;

    //承载播放器视图的容器
    protected FrameLayout mPlayerContainer;

    protected IRenderView mRenderView;
    protected RenderViewFactory mRenderViewFactory;

    public static final int SCREEN_SCALE_DEFAULT = 0;
    public static final int SCREEN_SCALE_16_9 = 1;
    public static final int SCREEN_SCALE_4_3 = 2;
    public static final int SCREEN_SCALE_MATCH_PARENT = 3;
    public static final int SCREEN_SCALE_ORIGINAL = 4;
    public static final int SCREEN_SCALE_CENTER_CROP = 5;
    protected int mCurrentScreenScaleType;

    protected int[] mVideoSize = {0,0};

    protected boolean mIsMute;                         // 是否静音

    /**
     * data Sources
     */
    protected String mUrl;
    protected Map<String,String> mHeaders;
    protected AssetFileDescriptor mAssetFileDescriptor;

    protected long mCurrentPosition;

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    public static final int PLAYER_TINY_SCREEN = 12;   // 小屏播放器
    public static final int PLAYER_PENDING_FULL_SCREEN = 13; // 即将全屏
    public static final int PLAYER_PENDING_NORMAL = 14; // 即将普通
    protected int mCurrentPlayerState = PLAYER_NORMAL;   // 当前播放器状态为普通

    //播放器的各种状态
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_BUFFERED = 7;
    public static final int STATE_START_ABORT = 8;//开始播放中止
    public static final int STATE_START_EXTRA = 9;// 片头广告
    protected int mCurrentPlayState = STATE_IDLE;  // 播放状态为闲置

    protected boolean mIsFullScreen;

    protected boolean mIsTinyScreen;
    protected int[] mTinyScreenSize = {0,0};

    protected boolean mEnableAudioFocus;

    protected AudioFocusHelper mAudioFocusHelper;

    // 监听器集合
    protected List<OnStateChangeListener> mOnStateChangeListeners;

    // 进度管理，记录播放进度
    protected PlayerProgressManager mProgressManager;

    protected boolean mIsLooping;

    private int mPlayerBackgroundColor = Color.BLACK;

    private boolean mLastPlayingStatus;

    // 改变播放状态和资源的接口
    private OnVideoPlayListener mOnVideoPlayerListener;



    public interface OnStateChangeListener {
        void playerStateChanged(int playerState);
        void playStateChanged(int playState);
    }


    public HoloVideoPlayer(@NonNull Context context) {
        super(context);
    }

    public HoloVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public HoloVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        HoloPlayerConfig config = HoloPlayerManager.getConfig();
        mEnableAudioFocus = config.mEnableAudioFocus;
        mProgressManager = config.mPlayerProgressManager;
        mPlayerFactory = config.mPlayerFactory;
        mCurrentScreenScaleType = config.mScreenScaleType;
        mRenderViewFactory = config.mRenderViewFactory;
        mOnVideoPlayerListener = config.mOnVideoPlayListener;

        initView();
    }

    protected void initView() {
        mPlayerContainer = new FrameLayout(getContext());
        mPlayerContainer.setBackgroundColor(mPlayerBackgroundColor);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer,params);
    }

    public void setPlayerBackgroundColor(int color) {
        mPlayerContainer.setBackgroundColor(color);
    }


    public void setVolume(float v1,float v2) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(v1,v2);
        }
    }

    @Override
    public void start() {
        boolean isStarted = false;
        if (!isInIdleState() || isInStartAbortState() || isInStartExtraState()) {
            isStarted = startPlay();
        }
    }

    protected boolean startPlay() {
        if (showNetWarning()) {
            //setPlayState()
        }
        return false;
    }

    public void release() {

    }

    public boolean onBackPressed() {
        return false;
    }

    public void setPlayerState(int state) {

    }
    protected void setPlayState(int playState) {
        mCurrentPlayState = playState;
        if (mVideoController != null) {
            mVideoController.setPlayState(playState);
        }
        if (mOnStateChangeListeners != null) {
            for (OnStateChangeListener listener : CommonUtils.getSnapshot(mOnStateChangeListeners)) {
                if (listener != null) {
                    listener.playStateChanged(playState);
                }
            }
        }

        if (mLastPlayingStatus != isPlaying()) {
            mLastPlayingStatus = isPlaying();
            if (mOnVideoPlayerListener != null) {
                //int source = (this instanceof TinyVideoPlayer) ? Task;
                //mOnVideoPlayerListener.onVideoPlayChanged(mLastPlayingStatus,source);
            }
        }
    }

    protected boolean showNetWarning() {
        if (isLocalDataSource()) return false;
        return mVideoController != null && mVideoController.showNetWarning();
    }

    protected boolean isLocalDataSource() {
        if (mAssetFileDescriptor != null) {
            return true;
        } else if (!TextUtils.isEmpty(mUrl)) {
            Uri uri = Uri.parse(mUrl);
            return ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme()) || ContentResolver.SCHEME_FILE.equals(uri.getScheme()) || "rawresource".equals(uri.getScheme());
        }
        return false;
    }

    // 是否处于未播放状态
    protected boolean isInIdleState() {
        return mCurrentPlayState == STATE_IDLE;
    }

    // 播放中止状态
    private boolean isInStartAbortState() {
        return mCurrentPlayState == STATE_START_ABORT;
    }

    // 开始播放开头任务（广告）
    private boolean isInStartExtraState() {
        return mCurrentPlayState == STATE_START_EXTRA;
    }

    // 是否处于播放状态
    protected boolean isInPlaybackState() {
        return mMediaPlayer != null
                && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE
                && mCurrentPlayState != STATE_PREPARING
                && mCurrentPlayState != STATE_START_ABORT
                && mCurrentPlayState != STATE_START_EXTRA
                && mCurrentPlayState != STATE_PLAYBACK_COMPLETED;
    }

    @Override
    public void pause() {

    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(long pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferedPercentage() {
        return 0;
    }

    @Override
    public void startFullScreen() {

    }

    @Override
    public void stopFullScreen() {

    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setMute(boolean isMute) {

    }

    @Override
    public boolean isMute() {
        return false;
    }

    @Override
    public void setScreenScaleType(int screenScaleType) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public float getSpeed() {
        return 0;
    }

    @Override
    public long getTcpSpeed() {
        return 0;
    }

    @Override
    public void replay(boolean resetPosition) {

    }

    @Override
    public void setMirrorRotation(boolean enable) {

    }

    @Override
    public Bitmap doScreenShot() {
        return null;
    }

    @Override
    public int[] getVideoSize() {
        return new int[0];
    }

    @Override
    public void startTinyScreen() {

    }

    @Override
    public void stopTinyScreen() {

    }

    @Override
    public boolean isTinyScreen() {
        return false;
    }

    @Override
    public void onError() {

    }

    @Override
    public void onCompletion() {

    }

    @Override
    public void onInfo(int what, int extra) {

    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onSizeChanged(int width, int height) {

    }
}
