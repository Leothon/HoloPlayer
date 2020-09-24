package com.holo.holo.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.video.VideoListener;
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

    public MediaPlayer(Context context){
        mApplicationContext = context.getApplicationContext();
        mMediaSourceHelper = MediaSourceHelper.getInstance(context);
    }

    @Override
    public void initPlayer() {

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
    public void setOtherOptions() {

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
