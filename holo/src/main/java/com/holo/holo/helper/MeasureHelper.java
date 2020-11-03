package com.holo.holo.helper;

import android.view.View;

import com.holo.holo.player.HoloVideoPlayer;

/**
 * Author: wangchengge
 * Date: 2020/10/19
 * Version: 1.0.0
 * Description:测量工具
 */
public class MeasureHelper {

    private int mVideoWidth;
    private int mVideoHeight;

    private int mCurrentScreenScale;

    private int mVideoRotationDegree;

    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    public void setVideoSize(int width,int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    public void setScreenScale(int scale) {
        mCurrentScreenScale = scale;
    }

    /**
     * 仅适用于固定高度和宽度的播放器
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @return
     */
    public int[] doMeasure(int widthMeasureSpec,int heightMeasureSpec) {
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec - widthMeasureSpec;
        }

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        if (mVideoHeight == 0 || mVideoWidth == 0) {
            return new int[]{width,height};
        }

        switch (mCurrentScreenScale) {
            case HoloVideoPlayer.SCREEN_SCALE_DEFAULT:
            default:
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                break;
            case HoloVideoPlayer.SCREEN_SCALE_ORIGINAL:
                width = mVideoWidth;
                height = mVideoHeight;
                break;
            case HoloVideoPlayer.SCREEN_SCALE_16_9:
                if (height > width / 16 * 9) {
                    height = width / 16 * 9;
                } else {
                    width = height / 9 * 16;
                }
                break;
            case HoloVideoPlayer.SCREEN_SCALE_4_3:
                if (height > width / 4 * 3) {
                    height = width / 4 * 3;
                } else {
                    width = height / 3 * 4;
                }
                break;
            case HoloVideoPlayer.SCREEN_SCALE_MATCH_PARENT:
                width = widthMeasureSpec;
                height = heightMeasureSpec;
                break;
            case HoloVideoPlayer.SCREEN_SCALE_CENTER_CROP:
                if (mVideoWidth * height > width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else {
                    height = width * mVideoHeight / mVideoWidth;
                }
                break;
        }
        return new int[]{width, height};
    }
}
