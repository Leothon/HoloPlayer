package com.holo.holo.helper;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

import com.holo.holo.player.HoloVideoPlayer;

import java.lang.ref.WeakReference;

/**
 * Author: wangchengge
 * Date: 2020/10/19
 * Version: 1.0.0
 * Description:音频焦点改变
 */
public final class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private WeakReference<HoloVideoPlayer> mWeakVideoPlayer;

    private AudioManager mAudioManager;

    private boolean mStartRequested = false;
    private boolean mPausedForLoss = false;

    private int mCurrentFocus = 0;

    public AudioFocusHelper(HoloVideoPlayer holoVideoPlayer) {
        mWeakVideoPlayer = new WeakReference<>(holoVideoPlayer);
        mAudioManager = (AudioManager) holoVideoPlayer.getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
        if (mCurrentFocus == focusChange) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                handleAudioFocusChange(focusChange);
            }
        });
        mCurrentFocus = focusChange;
    }

    private void handleAudioFocusChange(int focusChange) {
        final HoloVideoPlayer holoVideoPlayer = mWeakVideoPlayer.get();
        if (holoVideoPlayer == null) {
            return;
        }
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:             // 获得焦点
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:   // 暂时获得焦点
                if (mStartRequested || mPausedForLoss) {
                    holoVideoPlayer.start();
                    mStartRequested = false;
                    mPausedForLoss = false;
                }
                if (!holoVideoPlayer.isMute()) {
                    holoVideoPlayer.setVolume(1.0f,1.0f);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (holoVideoPlayer.isPlaying()) {
                    mPausedForLoss = true;
                    holoVideoPlayer.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (holoVideoPlayer.isPlaying() && !holoVideoPlayer.isMute()) {
                    holoVideoPlayer.setVolume(0.1f,0.1f);
                }
                break;
        }
    }

    void requestFocus() {
        if (mCurrentFocus == AudioManager.AUDIOFOCUS_GAIN) {
            return;
        }

        if (mAudioManager == null) {
            return;
        }

        int status = mAudioManager.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
            mCurrentFocus = AudioManager.AUDIOFOCUS_GAIN;
            return;
        }
        mStartRequested = true;
    }

    void abandonFocus() {
        if (mAudioManager == null) {
            return;
        }
        mStartRequested = false;
        mAudioManager.abandonAudioFocus(this);
    }
}
