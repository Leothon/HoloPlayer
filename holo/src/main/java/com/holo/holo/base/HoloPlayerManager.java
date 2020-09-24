package com.holo.holo.base;

import android.app.Application;
import android.util.Log;

import com.holo.holo.config.HoloPlayerConfig;
import com.holo.holo.player.HoloVideoPlayer;

import java.util.LinkedHashMap;

/**
 * @Author: a10943
 * @Date: 2020/9/22
 * @Desc: 播放器管理类，管理配置等
 */
public class HoloPlayerManager {

    /**
     * 保存播放器实例
     */
    private LinkedHashMap<String, HoloVideoPlayer> holoPlayers = new LinkedHashMap<>();

    /**
     * 移动网络下播放视频
     */
    private boolean mPlayOnMobileNetwork;

    /**
     * 播放管理器实例
     */
    private static HoloPlayerManager sInstance;

    /**
     * 播放器配置实例
     */
    private static HoloPlayerConfig sConfig;

    public HoloPlayerManager(){
        mPlayOnMobileNetwork = getConfig().mPlayOnMobileNetwork;
    }

    public static void setConfig(HoloPlayerConfig config) {
        if (sConfig == null) {
            synchronized (HoloPlayerConfig.class) {
                sConfig = config == null ? HoloPlayerConfig.newBuilder().build() : config;
            }
        }
    }

    public static HoloPlayerManager getInstance() {
        if (sInstance == null) {
            synchronized (HoloPlayerManager.class) {
                if (sInstance == null) {
                    sInstance = new HoloPlayerManager();
                }
            }
        }
        return sInstance;
    }

    public static HoloPlayerConfig getConfig() {
        setConfig(null);
        return sConfig;
    }

    public boolean isPlayOnMobileNetwork() {
        return mPlayOnMobileNetwork;
    }

    public void setPlayOnMobileNetwork(boolean mPlayOnMobileNetwork) {
        this.mPlayOnMobileNetwork = mPlayOnMobileNetwork;
    }

    public void addPlayer(String tag,HoloVideoPlayer holoVideoPlayer) {
        if (!(holoVideoPlayer.getContext() instanceof Application)) {
            Log.e("error","The Context of this VideoView is not an Application Context," +
                                        "you must remove it after release,or it will lead to memory leek.");
        }
        HoloVideoPlayer oldPlayer = getPlayer(tag);
        if (oldPlayer != null) {
            oldPlayer.release();
            removePlayer(tag);
        }
        holoPlayers.put(tag,holoVideoPlayer);
    }

    public HoloVideoPlayer getPlayer(String tag) {
        return holoPlayers.get(tag);
    }

    public void removePlayer(String tag) {
        holoPlayers.remove(tag);
    }

    public void removeAll() {
        for (HoloVideoPlayer holoVideoPlayer : holoPlayers.values()) {
            holoVideoPlayer.release();
        }
        holoPlayers.clear();
    }

    public void releaseByTag (String tag) {
        releaseByTag(tag,true);
    }

    public void releaseByTag(String tag,boolean isRemove) {
        HoloVideoPlayer holoVideoPlayer = getPlayer(tag);
        if (holoVideoPlayer != null) {
            holoVideoPlayer.release();
            if (isRemove) {
                removePlayer(tag);
            }
        }
    }

    public boolean onBackPressed(String tag) {
        HoloVideoPlayer holoVideoPlayer = getPlayer(tag);
        if (holoVideoPlayer == null) return false;
        return holoVideoPlayer.onBackPressed();
    }

}
