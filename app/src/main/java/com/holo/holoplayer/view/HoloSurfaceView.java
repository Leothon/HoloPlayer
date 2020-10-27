package com.holo.holoplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Author: wangchengge
 * Date: 2020/10/27
 * Version: 1.0.0
 * Description:使用surface显示图片
 */
public class HoloSurfaceView extends SurfaceView {
    public HoloSurfaceView(Context context) {
        super(context);
    }

    public HoloSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HoloSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
