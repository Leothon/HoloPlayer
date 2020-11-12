package com.holo.holoplayer.utils;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.IntBuffer;

/**
 * Author: wangchengge
 * Date: 2020/11/12
 * Version: 1.0.0
 * Description:OpenGL相关工具类
 */
public class OpenGLUtils {

    public static final int NO_TEXTURE = -1;

    public static void deleteTexture(int textureId) {
        GLES20.glDeleteTextures(1,new int[]{textureId},0);
    }

    public static int loadTexture(final Bitmap bitmap,final int usedTextureId) {
        return loadTexture(bitmap,usedTextureId,true);
    }

    public static int loadTexture(final Bitmap img,final int usedTextureId,final boolean recycle) {
        int textures[] = new int[]{-1};
        if (usedTextureId == NO_TEXTURE) {
            GLES20.glGenTextures(1,textures,0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);

            // 纹理坐标系ST坐标
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);// S轴的拉伸方式为重复，决定采样值的坐标超出图片范围时的采样方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT); // T轴的拉伸方式为重复

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,img,0);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,usedTextureId);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D,0,0,0,img);
            textures[0] = usedTextureId;
        }
        if (recycle) {
            img.recycle();
        }
        return textures[0];
    }

    public static int loadTexture(final IntBuffer data, final Camera.Size size, final int usedTexId) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1,textures,0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,size.width,size.height,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,data);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,usedTexId);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,0,0,size.width,size.height,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,data);
            textures[0] = usedTexId;
        }
        return textures[0];
    }

    public static int loadTextureAsBitmap(final IntBuffer data, final Camera.Size size,final int usedTexId) {
        Bitmap bitmap = Bitmap.createBitmap(data.array(),size.width,size.height, Bitmap.Config.ARGB_8888);
        return loadTexture(bitmap,usedTexId);
    }
}
