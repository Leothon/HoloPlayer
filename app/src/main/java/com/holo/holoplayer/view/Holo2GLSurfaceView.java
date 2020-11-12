package com.holo.holoplayer.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.holo.holoplayer.renderer.HoloGLRenderer;

/**
 * Author: wangchengge
 * Date: 2020/11/10
 * Version: 1.0.0
 * Description:GLSurfaceView实例
 * OpenGl只能画点，线，三角形，复杂的图形都是由三角形构成
 * 先把单独的点放在一个数组里（顶点着色器），再告诉OpenGl如何连接这些点
 * TODO 1，定义三角形顶点的坐标数据的浮点型缓冲区FloatBuffer；
 * TODO 2，创建数组triangleCoords[],里面定义三角形三个顶点的坐标
 * TODO 3，定义一个构造器，实现三个逻辑：
 *      TODO 1，初始化形状中顶点坐标数据的字节缓冲区ByteBuffer
 *      TODO 2，从ByteBuffer中获得一个基本类型缓冲区即浮点缓冲区FloatBuffer
 *      TODO 3，把坐标数组triangleCoords[]放入FloatBuffer中，并定义读取顺序
 */
public class Holo2GLSurfaceView extends GLSurfaceView {

    private HoloGLRenderer holoGLRenderer;

    public Holo2GLSurfaceView(Context context) {
        super(context);
        // 设置OpenGl版本
        setEGLContextClientVersion(2);

        holoGLRenderer = new HoloGLRenderer();

        setRenderer(holoGLRenderer);
        // RENDERMODE_WHEN_DIRTY 和RENDERMODE_CONTINOUSLY，前者是懒惰渲染，需要手动调用
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
