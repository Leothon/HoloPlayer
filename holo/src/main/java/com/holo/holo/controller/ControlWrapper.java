package com.holo.holo.controller;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;

import com.holo.holo.player.HoloVideoPlayer;

/**
 * Author: wangchengge
 * Date: 2020/10/14
 * Version: 1.0.0
 * Description:此类的目的是封装部分api
 */
public class ControlWrapper implements MediaPlayerControl,IVideoController{

    private MediaPlayerControl mMediaPlayerControl;
    private IVideoController mController;

    public ControlWrapper(MediaPlayerControl playerControl,IVideoController controller) {
        mMediaPlayerControl = playerControl;
        mController = controller;
    }

    @Override
    public void startFadeOut() {
        mController.startFadeOut();
    }

    @Override
    public void stopFadeOut() {
        mController.stopFadeOut();
    }

    @Override
    public boolean isShowing() {
        return mController.isShowing();
    }

    @Override
    public void setLocked(boolean isLocked) {
        mController.setLocked(isLocked);
    }

    @Override
    public boolean isLocked() {
        return mController.isLocked();
    }

    @Override
    public void startProgress() {
        mController.startProgress();
    }

    @Override
    public void stopProgress() {
        mController.stopProgress();
    }

    @Override
    public void hide() {
        mController.hide();
    }

    @Override
    public void show() {
        mController.show();
    }

    @Override
    public boolean hasCutout() {
        return mController.hasCutout();
    }

    @Override
    public int getCutoutHeight() {
        return mController.getCutoutHeight();
    }

    public void toggleLockState() {
        setLocked(!isLocked());
    }

    public void toggleShowState() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    @Override
    public void start() {
        mMediaPlayerControl.start();
    }

    @Override
    public void pause() {
        mMediaPlayerControl.pause();
    }

    @Override
    public long getDuration() {
        return mMediaPlayerControl.getDuration();
    }

    @Override
    public long getDurationPosition() {
        return mMediaPlayerControl.getDurationPosition();
    }

    @Override
    public void seekTo(long pos) {
        mMediaPlayerControl.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayerControl.isPlaying();
    }

    @Override
    public int getBufferedPercentage() {
        return mMediaPlayerControl.getBufferedPercentage();
    }

    @Override
    public void startFullScreen() {
        mMediaPlayerControl.startFullScreen();
    }

    @Override
    public void stopFullScreen() {
        mMediaPlayerControl.stopFullScreen();
    }

    public void setPlayerState(int state) {
        if (mMediaPlayerControl instanceof HoloVideoPlayer) {
            ((HoloVideoPlayer)mMediaPlayerControl).setPlayerState(state);
        }
    }

    public void setStartExtraNeed(boolean need) {
        if (mController instanceof BaseVideoController) {
            ((BaseVideoController) mController).setStartExtraNeed(need);
        }
    }

    @Override
    public boolean isFullScreen() {
        return mMediaPlayerControl.isFullScreen();
    }

    @Override
    public void setMute(boolean isMute) {
        mMediaPlayerControl.setMute(isMute);
    }

    @Override
    public boolean isMute() {
        return mMediaPlayerControl.isMute();
    }

    @Override
    public void setScreenScaleType(int screenScaleType) {
        mMediaPlayerControl.setScreenScaleType(screenScaleType);
    }

    @Override
    public void setSpeed(float speed) {
        mMediaPlayerControl.setSpeed(speed);
    }

    @Override
    public float getSpeed() {
        return mMediaPlayerControl.getSpeed();
    }

    @Override
    public long getTcpSpeed() {
        return mMediaPlayerControl.getTcpSpeed();
    }

    @Override
    public void replay(boolean resetPosition) {
        mMediaPlayerControl.replay(resetPosition);
    }

    @Override
    public void setMirrorRotation(boolean enable) {
        mMediaPlayerControl.setMirrorRotation(enable);
    }

    @Override
    public Bitmap doScreenShot() {
        return mMediaPlayerControl.doScreenShot();
    }

    @Override
    public int[] getVideoSize() {
        return mMediaPlayerControl.getVideoSize();
    }

    @Override
    public void setRotation(float rotation) {
        mMediaPlayerControl.setRotation(rotation);
    }

    @Override
    public void startTinyScreen() {
        mMediaPlayerControl.startTinyScreen();
    }

    @Override
    public void stopTinyScreen() {
        mMediaPlayerControl.stopFullScreen();
    }

    @Override
    public boolean isTinyScreen() {
        return mMediaPlayerControl.isTinyScreen();
    }

    public void togglePlay() {
        if (isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    /**
     * 切换全屏，旋转屏幕
     * @param activity
     */
    public void toggleFullScreen(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (isFullScreen()) {
            setPlayerState(HoloVideoPlayer.PLAYER_PENDING_NORMAL);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            stopFullScreen();
        } else {
            setPlayerState(HoloVideoPlayer.PLAYER_PENDING_FULL_SCREEN);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            startFullScreen();
        }
    }

    /**
     * 切换全屏不旋转屏幕
     */
    public void toggleFullScreen() {
        if (isFullScreen()) {
            stopFullScreen();
        } else {
            startFullScreen();
        }
    }

    /**
     * 切换全屏，根据屏幕尺寸决定是否旋转屏幕
     * @param activity
     */
    public void toggleFullScreenByVideoSize(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        int[] size = getVideoSize();
        int width = size[0];
        int height = size[1];
        if (isFullScreen()) {
            stopFullScreen();
            if (width > height) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            startFullScreen();
            if (width > height) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }
}
