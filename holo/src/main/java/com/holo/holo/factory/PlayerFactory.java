package com.holo.holo.factory;

import android.content.Context;

import com.holo.holo.player.AbstractPlayer;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 播放器工厂,用来生成播放器
 */
public abstract class PlayerFactory<P extends AbstractPlayer> {

    public abstract P createPlayer(Context context);
}
