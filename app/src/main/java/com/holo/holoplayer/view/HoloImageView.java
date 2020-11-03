package com.holo.holoplayer.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.holo.holoplayer.R;
import com.holo.holoplayer.utils.DensityUtils;

/**
 * Author: wangchengge
 * Date: 2020/10/27
 * Version: 1.0.0
 * Description:自定义的绘制图片的view
 *  首先需要Resource获取到资源，并且转化成bitmap，加载到bitmapDrawable中，并且通过bitmapDrawable将图片绘制
 */
public class HoloImageView extends View {

    private Bitmap bitmap;

    private int width,height;

    private float imageRatio;
    public HoloImageView(Context context) {
        super(context);
        init();
    }

    public HoloImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HoloImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.my_girl);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        imageRatio = (float) (height / width);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        width = DensityUtils.getScreenWidth(getContext());
        height = (int) (width * imageRatio);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.save();
            canvas.drawBitmap(bitmap,null, new RectF(0, 0, width, height),null);
            canvas.restore();
        }
    }
}
