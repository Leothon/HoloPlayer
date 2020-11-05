package com.holo.holoplayer.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Author: wangchengge
 * Date: 2020/11/5
 * Version: 1.0.0
 * Description:GLSurface使用
 *
 * * TODO OpenGL提供了接口用于处理高性能2D和3D的图形
 *  * Android framework提供了GLSurface和GLSurfaceView.Renderer基础类
 *  * GLSurfaceView,用来显示OpenGL ES Api绘制和操作的图像对象的view，并且在功能上和SurfaceView类似，通过创建GLSurfaceView的
 *  * 实例并且将Renderer添加到该实例来使用此类，如果要捕捉Touch Event，应继承GLSurfaceView并且实现onTouchEvent
 *  *
 *  * TODO GLSurfaceView.Renderer，该接口定义了在GLSurfaceView中绘制图形对象所需要的方法：
 *    TODO onSurfaceCreated():创建GLSurfaceView时，会调用，只需要执行一次，设置OpenGl环境参数或者初始化OpenGL图形对象
 *  * TODO onDrawFrame():系统每次绘制GLSurfaceView时候调用，作为绘制图形对象的主要执行点
 *  * TODO onSurfaceChanged():当GLSurfaceView更改时，会调用，包括GLSurfaceView的大小和方向改变
 */
public class HoloGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    /**
     * 顶点着色器
     */
    private static final String VERTEX_SHADER = "" +
            // vec4 4个分量的向量：x,y,z,w
            "attribute vec4 a_Position;\n" +
            "void main()\n" +
            "{\n" +
            // gl_Position:设置顶点的位置
            "  gl_Position = a_Position;\n" +
            // gl_PointSize:设置当前顶点的大小
            "  gl_PointSize = 40.0;\n" +
            "}";

    /**
     * 片段着色器
     */
    private static final String FRAGMENT_SHADER = "" +
            // 定义浮点数精度，lowp，mediump，highp
            "precision mediump float;\n" +
            "uniform mediump vec4 u_Color;\n" +
            "void main()\n" +
            "{\n" +
            // 当前片段的颜色
            "  gl_FragColor = u_Color;\n" +
            "}";

    private int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER,shaderCode);
    }

    private int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER,shaderCode);
    }


    /**
     * 编译着色器
     * @param type 着色器类型
     * @param shaderCode 着色器编译代码
     * @return 着色器对象ID
     */
    private int compileShader(int type,String shaderCode) {
        // 创建一个着色器对象
        final int shaderObjectId = GLES20.glCreateShader(type);
        // 获取创建状态
        if (shaderObjectId == 0) {
            Log.e("着色器创建","创建失败");
            return 0;
        }
        // 将着色器代码加载到着色器对象中
        GLES20.glShaderSource(shaderObjectId,shaderCode);
        // 编译着色器对象
        GLES20.glCompileShader(shaderObjectId);
        // 获取编译状态，OpenGL将想要获取的值放入长度为1的数组的首位
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId,GLES20.GL_COMPILE_STATUS,compileStatus,0);
        Log.e("着色器状态"," " + GLES20.glGetShaderInfoLog(shaderObjectId));
        // 验证编译状态
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderObjectId);
            Log.e("着色器状态","编译失败");
            return 0;
        }
        return shaderObjectId;
    }


    public HoloGLSurfaceView(Context context) {
        super(context);
    }

    public HoloGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }
}
