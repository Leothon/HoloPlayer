package com.holo.holoplayer.helper;

import android.opengl.GLES20;

import com.holo.holoplayer.renderer.HoloGLRenderer;

import java.nio.FloatBuffer;
import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL;

/**
 * Author: wangchengge
 * Date: 2020/11/11
 * Version: 1.0.0
 * Description:处理GLImage
 * attribute：一般用于各个顶点各不相同的量。如顶点颜色、坐标等。
 * uniform：一般用于对于3D物体中所有顶点都相同的量。比如光源位置，统一变换矩阵等。
 * varying：表示易变量，一般用于顶点着色器传递到片元着色器的量。
 * const：常量。 限定符与java限定符类似，放在变量类型之前，并且只能用于全局变量。在GLSL中，没有默认限定符一说。
 */
public class GLImageHelper {
    // 数据中有多少个顶点，管线就调用多少次顶点着色器
    public static final String NO_FILTER_VERTEX_SHADER = "" +
            // 顶点着色器顶点坐标，由外部传入
            "attribute vec4 position;\n" +
            // 传入的纹理坐标
            "attribute vec4 inputTextureCoordinate;\n" +
            "varying vec2 textureCoordinate;\n" +
            "void main(){" +
            "  gl_Position = position;\n" +
            "  textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";

    // 光栅化后产生了多少片段，就会插值计算出多少个varying变量，同时渲染管线就会调用多少次片段着色器
//    gl_FragCoord：当前片元相对窗口位置所处的坐标。
//    gl_FragFacing：bool型，表示是否为属于光栅化生成此片元的对应图元的正面。 输出变量:
//    gl_FragColor：当前片元颜色
//    gl_FragData：vec4类型的数组。向其写入的信息，供渲染管线的后继过程使用。
    public static final String NO_FILTER_FRAGMENT_SHADER = "" +
            // 最终顶带你位置，由顶点着色器传递
            "varying highp vec2 textureCoordinate;\n" +
            // 外部传入的图片纹理
            "uniform sampler2D inputImageTexture;\n" +
            "void main(){" +
            // 调用函数，进行纹理贴图
            "  gl_FragColor = texture2D(inputImageTexture,textureCoordinate);\n" +
            "}";

    private final LinkedList<Runnable> mRunOnDraw;

    private final String mVertexShader;

    private final String mFragmentShader;

    protected int mGLProgId;

    protected int mGLAttribPosition;

    protected int mGLUniformTexture;

    protected int mGLAttribTextureCoordinate;

    public GLImageHelper() {
        this(NO_FILTER_VERTEX_SHADER,NO_FILTER_FRAGMENT_SHADER);
    }

    public GLImageHelper(final String vertexShader,final String fragmentShader) {
        mRunOnDraw = new LinkedList<>();
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
    }

    public final void init() {
        // 获取program，得到相关参数
        mGLProgId = loadProgram(mVertexShader,mFragmentShader);
        mGLAttribPosition = GLES20.glGetAttribLocation(mGLProgId,"position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mGLProgId,"inputImageTexture");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mGLProgId,"inputTextureCoordinate");
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mGLProgId);
        // 顶点着色器渲染顶点坐标
        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition,2,GLES20.GL_FLOAT,false,0,cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        // 顶点着色器纹理坐标
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate,2,GLES20.GL_FLOAT,false,0,textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);

        // 传入图片纹理
        if (textureId != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
            GLES20.glUniform1i(mGLUniformTexture,0);
        }

        // 绘制顶点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
    }

    private int loadProgram(String VERTEX_SHADER,String FRAGMENT_SHADER) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,FRAGMENT_SHADER);

        int mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram,vertexShader);
        GLES20.glAttachShader(mProgram,fragmentShader);
        GLES20.glLinkProgram(mProgram);

        return mProgram;
    }

    public int loadShader(int type,String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

}
