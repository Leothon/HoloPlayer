package com.holo.holoplayer.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.holo.holoplayer.R;
import com.holo.holoplayer.databinding.ActivityOpenGlEsBinding;
import com.holo.holoplayer.helper.GLImageHelper;
import com.holo.holoplayer.utils.OpenGLUtils;
import com.holo.holoplayer.view.Holo2GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

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

    // 绘制图片的原理：定义一组矩形区域的顶点，然后根据纹理坐标把图片作为纹理贴在该矩形区域内

    // 原始的矩形区域顶点坐标，因为后面使用了顶点法绘制顶点，所以不用定义绘制顶点的索引，无论窗口的大小是多少，在OpenGl二维坐标系中都是为下面表示的矩形区域
    public static final float CUBE[] = {
            -1.0f,-1.0f,
             1.0f,-1.0f,
            -1.0f, 1.0f,
             1.0f, 1.0f
    };

    // 纹理坐标系，左上角为0，0 右下角是1，1
    public static final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    private int mGLTextureId = OpenGLUtils.NO_TEXTURE;
    private GLImageHelper mGlImageHelper = new GLImageHelper();

    private FloatBuffer mGLCubeBuffer;
    private FloatBuffer mGlTextureBuffer;
    private int mOutputWidth,mOutputHeight;
    private int mImageWidth,mImageHeight;


    public static void start(Context context) {
        context.startActivity(new Intent(context,OpenGlEsActivity.class));
    }

    private ActivityOpenGlEsBinding mBinding;

//    private Holo2GLSurfaceView holo2GLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityOpenGlEsBinding.inflate(getLayoutInflater());
//        holo2GLSurfaceView = new Holo2GLSurfaceView(this);
        setContentView(mBinding.getRoot());
        mBinding.glSurfaceView.setEGLContextClientVersion(2);

        mBinding.glSurfaceView.setRenderer(new HoloInternalRender());
        mBinding.glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);// 手动刷新
    }

    private class HoloInternalRender implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0,0,0,1);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST); // 当绘制透明图片时，关闭它
            mGlImageHelper.init();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.my_girl);
            mImageWidth = bitmap.getWidth();
            mImageHeight = bitmap.getHeight();

            //加载纹理
            mGLTextureId = OpenGLUtils.loadTexture(bitmap,mGLTextureId,true);

            mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLCubeBuffer.put(CUBE).position(0);

            mGlTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGlTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            mOutputHeight = height;
            mOutputWidth = width;
            GLES20.glViewport(0,0,width,height);
            adjustImageScaling();
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            // 绘制
            mGlImageHelper.onDraw(mGLTextureId,mGLCubeBuffer,mGlTextureBuffer);
        }

        private void adjustImageScaling() {
            float outputWidth = mOutputWidth;
            float outputHeight = mOutputHeight;

            float ratio1 = outputWidth / mImageWidth;
            float ratio2 = outputHeight / mImageHeight;
            float ratioMax = Math.min(ratio1,ratio2);

            int imageWidthNew = Math.round(mImageWidth * ratioMax);
            int imageHeightNew = Math.round(mImageWidth * ratioMax);

            float ratioWidth = outputWidth / imageWidthNew;
            float ratioHeight = outputHeight / imageHeightNew;

            float[] cube = new float[] {
                    CUBE[0] / ratioWidth, CUBE[1] / ratioHeight,
                    CUBE[2] / ratioWidth, CUBE[3] / ratioHeight,
                    CUBE[4] / ratioWidth, CUBE[5] / ratioHeight,
                    CUBE[6] / ratioWidth, CUBE[7] / ratioHeight,
            };
            mGLCubeBuffer.clear();
            mGLCubeBuffer.put(cube).position(0);
        }

    }


}