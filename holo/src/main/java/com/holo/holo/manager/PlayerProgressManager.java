package com.holo.holo.manager;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 播放器进度管理
 */
public abstract class PlayerProgressManager {

    public abstract void saveProgress(String url,long progress);

    public abstract long getProgress(String url);

}
