package com.holo.holo.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holo.holo.factory.PlayerFactory;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: holo播放器(核心和渲染器）
 */
public class HoloVideoPlayer<P extends AbstractPlayer> extends FrameLayout implements MediaController.MediaPlayerControl,AbstractPlayer.PlayerEventListener {

    protected P mMediaPlayer;
    protected PlayerFactory<P> mPlayerFactory;

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    public static final int PLAYER_TINY_SCREEN = 12;   // 小屏播放器
    public static final int PLAYER_PENDING_FULL_SCREEN = 13; // 即将全屏
    public static final int PLAYER_PENDING_NORMAL = 14; // 即将普通

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

    public HoloVideoPlayer(@NonNull Context context) {
        super(context);
    }

    public HoloVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HoloVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int i) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
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

    public void release() {}

    public boolean onBackPressed() {return false;}
}
