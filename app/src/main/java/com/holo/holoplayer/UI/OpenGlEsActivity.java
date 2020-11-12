package com.holo.holoplayer.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.holo.holoplayer.databinding.ActivityOpenGlEsBinding;
import com.holo.holoplayer.view.Holo2GLSurfaceView;

/**
 *
 *  TODO GLSurfaceView常用方法
 *  TODO setEGLContextClientVersion：设置OpenGL ES版本，2.0则设置2
 *  TODO onPause：暂停渲染，最好是在Activity、Fragment的onPause方法内调用，减少不必要的性能开销，避免不必要的崩溃
 *  TODO onResume：恢复渲染，用法类比onPause
 *  TODO setRenderer：设置渲染器
 *  TODO setRenderMode：设置渲染模式
 *  TODO requestRender: 请求渲染，由于是请求异步线程进行渲染，所以不是同步方法，调用后不会立刻就进行渲染。渲染会回调到Renderer接口的onDrawFrame方法。
 *  TODO queueEvent：插入一个Runnable任务到后台渲染线程上执行。相应的，渲染线程中可以通过Activity的runOnUIThread的方法来传递事件给主线程去执行
 *  TODO GLSurfaceView渲染模式
 *  TODO RENDERMODE_CONTINUOUSLY：不停地渲染
 *  TODO RENDERMODE_WHEN_DIRTY：只有调用了requestRender之后才会触发渲染回调onDrawFrame方法
 *
 */
public class OpenGlEsActivity extends AppCompatActivity {


    public static void start(Context context) {
        context.startActivity(new Intent(context,OpenGlEsActivity.class));
    }

    //private ActivityOpenGlEsBinding mBinding;

    private Holo2GLSurfaceView holo2GLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///mBinding = ActivityOpenGlEsBinding.inflate(getLayoutInflater());
        holo2GLSurfaceView = new Holo2GLSurfaceView(this);
        setContentView(holo2GLSurfaceView);
    }


}