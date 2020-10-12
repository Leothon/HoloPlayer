package com.holo.holo.factory;

import android.content.Context;

import com.holo.holo.player.MediaPlayer;

/**
 * Author: wangchengge
 * Date: 2020/10/12
 * Version: 1.0.0
 * Description:播放器工厂方法
 */
public class MediaPlayerFactory extends PlayerFactory<MediaPlayer> {

    public static MediaPlayerFactory create() {
        return new MediaPlayerFactory();
    }

    @Override
    public MediaPlayer createPlayer(Context context) {
        return new MediaPlayer(context);
    }
}
