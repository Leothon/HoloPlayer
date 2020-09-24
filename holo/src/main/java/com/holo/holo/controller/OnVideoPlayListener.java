package com.holo.holo.controller;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 毁掉播放器改变状态接口
 */
public interface OnVideoPlayListener {
    void onVideoPlayChanged(boolean isPlaying,int source);
}
