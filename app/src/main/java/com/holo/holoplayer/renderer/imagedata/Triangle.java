package com.holo.holoplayer.renderer.imagedata;

import android.opengl.GLES20;

import com.holo.holoplayer.renderer.HoloGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;

/**
 * Author: wangchengge
 * Date: 2020/11/10
 * Version: 1.0.0
 * Description:图形数据：三角形
 */
public class Triangle {


    /**
     * 顶点着色器
     */
    private static String VERTEX_SHADER = "" +
            // vec4 4个分量的向量：x,y,z,w
            "attribute vec4 a_Position;" +
            "void main()" +
            "{" +
            // gl_Position:设置顶点的位置
            "  gl_Position = a_Position;" +
            "}";

    /**
     * 片段着色器
     */
    private static String FRAGMENT_SHADER = "" +
            // 定义浮点数精度，lowp，mediump，highp
            "precision mediump float;" +
            "uniform vec4 u_Color;" +
            "void main()" +
            "{" +
            // 当前片段的颜色
            "  gl_FragColor = u_Color;" +
            "}";


    // 为顶点坐标创建一个浮点型缓冲区
    private FloatBuffer vertexBuffer;

    // 顶点个数
    static final int COORDINATES_PER_VERTEX = 3;

    // 以逆时针顺序，分别指定坐标顶点坐标
    // 每个顶点需要指定三个坐标，分别是XYZ坐标轴方向的数据
    static float triangleCoords[] = {
            0.0f,1.0f,0.0f,
            -1.0f,-1.0f,0,0f,
            1.0f,-1.0f,0.0f
    };

    float color[] = {0.0f,0.0f,0.0f,1.0f};

    int mProgram;
    private int mPositionHandle; // 存取attribute修饰的变量的位置编号
    private int mColorHandle;    // 存取uniform修饰的变量的位置编号

    private final int vertexCount = triangleCoords.length / COORDINATES_PER_VERTEX;
    private final int vertexStride = COORDINATES_PER_VERTEX * 4;

    // ByteBuffer作为中转，用来读取其他数据类型

    public Triangle() {
        // 运行再虚拟机上的代码不能直接访问物理环境，而OpenGl又是运行在物理环境上，所以需要一个缓冲区类
        // 用来将java的数据复制到本地内存，再被物理环境读取
        // 顶点个数 * 4bytes
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // 设置缓冲区使用设备硬件的原本字节顺序进行读取
        byteBuffer.order(ByteOrder.nativeOrder());
        // 数据移入缓冲区
        vertexBuffer = byteBuffer.asFloatBuffer();
        // 坐标信息存入FloatBuffer
        vertexBuffer.put(triangleCoords);
        // 从第一个位置开始读取坐标
        vertexBuffer.position(0);

        int vertexShader = HoloGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,VERTEX_SHADER);
        int fragmentShader = HoloGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,FRAGMENT_SHADER);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram,vertexShader);
        GLES20.glAttachShader(mProgram,fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    // 在渲染器中调用
    public void draw() {
        GLES20.glUseProgram(mProgram);
        // 拿到位置参数
        mPositionHandle = GLES20.glGetAttribLocation(mProgram,"a_Position");
        // 设置位置参数
        GLES20.glVertexAttribPointer(mPositionHandle,COORDINATES_PER_VERTEX,
                GLES20.GL_FLOAT,false,
                vertexStride,vertexBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 取出片元参数
        mColorHandle = GLES20.glGetUniformLocation(mPositionHandle,"u_Color");
        // 设置颜色
        GLES20.glUniform4fv(mColorHandle,1,color,0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,vertexCount);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
