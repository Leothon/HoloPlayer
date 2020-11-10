package com.holo.holoplayer.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.holo.holoplayer.renderer.imagedata.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Author: wangchengge
 * Date: 2020/11/10
 * Version: 1.0.0
 * Description:渲染器
 */
public class HoloGLRenderer implements GLSurfaceView.Renderer {


    private Triangle triangle;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        triangle = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // 设置大小
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // 清除背景色
        GLES20.glClearColor(0.2f,0.3f,0.7f,1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        triangle.draw();
    }

    // 加载并编译着色器代码
    public static int loadShader(int type,String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
