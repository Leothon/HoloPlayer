package com.holo.holoplayer.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.holo.holoplayer.databinding.ActivityCustomCameraBinding;

/**
 * 自定义的录像机
 * 1，初始化
 *      （1）初始化audioRecord用来录音
 *      （2）初始化相机用来获取相关数据
 * 2，点击按钮，开始录像
 *      audioRecord开始录音，并将流数据（PCM）编码为aac
 *      接受相机返回的流数据（YUY），编码为h264
 *      分别存储
 * 3，再次点击按钮，停止录像，保存
 *      h264和aac无法直接合成，需要将h264混合mpeg4包装成mp4（无音频）
 *      将aac（无adts）混合成mpeg4包装的MP4（无视频）
 *      最后将混合包装好的，重新分离并再生成新的视频
 *
 */
public class CustomCameraActivity extends AppCompatActivity {


    public static void start(Context context) {
        context.startActivity(new Intent(context,CustomCameraActivity.class));
    }

    private ActivityCustomCameraBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCustomCameraBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }
}