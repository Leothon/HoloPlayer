package com.holo.holo.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import com.holo.holo.helper.MeasureHelper;
import com.holo.holo.player.AbstractPlayer;

/**
 * Author: wangchengge
 * Date: 2020/10/19
 * Version: 1.0.0
 * Description:textureRenderView
 */
public class TextureRenderView extends TextureView implements IRenderView,TextureView.SurfaceTextureListener {

    private MeasureHelper measureHelper;

    /**
     * 不直接处理显示图像流，而是转为GL外部纹理，用来对图像做二次处理，最后可以交给GLSurfaceView处理，也可以通过surfaceTexture交给TextureView来显示
     */
    private SurfaceTexture surfaceTexture;

    private AbstractPlayer abstractPlayer;

    private Surface surface;

    public TextureRenderView(Context context) {
        super(context);
    }

    {
        measureHelper = new MeasureHelper();
        setSurfaceTextureListener(this);
    }

    @Override
    public void attachToPlayer(AbstractPlayer abstractPlayer) {
        this.abstractPlayer = abstractPlayer;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            measureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        measureHelper.setVideoRotation(degree);
        setRotation(degree);
    }

    @Override
    public void setScaleType(int scaleType) {
        measureHelper.setScreenScale(scaleType);
        requestLayout();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public Bitmap doScreenShot() {
        return getBitmap();
    }

    @Override
    public void release() {
        if (surface != null) {
            surface.release();
        }
        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measureSize = measureHelper.doMeasure(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(measureSize[0],measureSize[1]);
    }

    /**
     * 开始绘制textureView
     * @param surfaceTexture
     * @param i
     * @param i1
     */
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        if (this.surfaceTexture != null) {
            setSurfaceTexture(surfaceTexture);
        } else {
            this.surfaceTexture = surfaceTexture;
            surface = new Surface(surfaceTexture);
            if (abstractPlayer != null) {
                abstractPlayer.setTextureView(surface);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }
}
