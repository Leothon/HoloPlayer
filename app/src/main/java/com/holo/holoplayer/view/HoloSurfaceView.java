package com.holo.holoplayer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.holo.holoplayer.R;
import com.holo.holoplayer.utils.DensityUtils;



/**
 * Author: wangchengge
 * Date: 2020/10/27
 * Version: 1.0.0
 * Description:使用surface显示图片
 * surfaceview和普通的view区别是，普通view和宿主窗口共用一个surface，而surfaceview虽然也在view树中，但是有属于自己的surface，其内部持有一个canvas，用来绘制
 */
public class HoloSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Canvas canvas;

    private Bitmap bitmap;

    private int width,height;

    private float imageRatio;

    private int count = 10;

    private Paint paint;
    public HoloSurfaceView(Context context) {
        super(context);
        init();
    }

    public HoloSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HoloSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.my_girl);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        imageRatio = (float) (height / width);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        width = DensityUtils.getScreenWidth(getContext());
        height = (int) (width * imageRatio);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(200);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        width = getMeasuredWidth();
        height = (int) (width * imageRatio);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            draw((count -- ) + "");
            if (count > 0) {
                handler.sendEmptyMessage(0);
            } else {
                draw("我爱你，铁锤宝贝");
            }
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                handler.postDelayed(runnable,1000);
            }
        }
    };

    private void draw(String s) {
        int length = s.length();

        canvas = surfaceHolder.lockCanvas();
        canvas.drawBitmap(bitmap,null,new RectF(0,0,width,height),null);
        canvas.drawText(s,width / 2 - (length / 2) * 200,height / 2,paint);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        new Thread(runnable).start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
