package com.holo.holoplayer.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.holo.holoplayer.R;
import com.holo.holoplayer.databinding.ActivityCustomCameraBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
    private String[] permissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    /**
     * 音频相关
     */
    //缓冲区大小
    private int mRecordBufferSize;
    private AudioRecord mAudioRecord;
    private double recordTime = 0.0;
    private int audioSampleRate = 0;
    private MediaCodec encoder = null;
    private MediaFormat encoderFormat = null;
    private int aacSampleRate = 4;
    private FileOutputStream outputStreamAAC = null;
    private MediaCodec.BufferInfo info = null;
    private byte[] outByteBuffer = null;
    private boolean mIsRecord = false;

    /**
     * 视频相关
     */
    private Camera camera;
    private Camera.Parameters parameters;
    private Camera.CameraInfo cameraInfo;

    public static void start(Context context) {
        context.startActivity(new Intent(context,CustomCameraActivity.class));
    }

    private ActivityCustomCameraBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCustomCameraBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        checkPermission(permissions);
    }

    private void checkPermission(String[] permissions) {

        List<String> mPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissions.add(permission);
            }
        }
        if (!mPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions, 100);
        } else {
            permissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            permissionGranted();
        }
    }

    private void permissionGranted() {
        // 获取权限后，开始初始化相机和录音设备
        // 第一个参数，采样率
        // 第二个参数，声道数.CHANNEL_CONFIGURATION_STEREO 双声道，CHANNEL_IN_MONO 单声道
        // 第三个参数，采样精度，16比特
        mRecordBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        // 实例化录音器
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mRecordBufferSize);
        //初始化编码器信息，将pcm编码为aac
        initMediaCodecEncoder(44100,new File(this.getExternalFilesDir("media").getAbsolutePath(), "audioRecordAAC.aac"));
        initCamera();
    }


    private void initMediaCodecEncoder(int sampleRate, File outFile) {
        try {
            aacSampleRate = getADTSSampleRate(sampleRate);
            // 立体声
            encoderFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, 2);
            encoderFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            encoderFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            encoderFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096);
            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            info = new MediaCodec.BufferInfo();
            if (encoder == null) {
                return;
            }
            recordTime = 0.0;
            encoder.configure(encoderFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            outputStreamAAC = new FileOutputStream(outFile);
            encoder.start();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCamera() {
        mBinding.previewCamera.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull final SurfaceTexture surfaceTexture, int i, int i1) {
                if (camera == null) {
                    cameraInfo = new Camera.CameraInfo();
                    camera = Camera.open();
                    initParameter(camera);
                }
                try {
                    if (camera != null) {
                        camera.setPreviewTexture(mBinding.previewCamera.getSurfaceTexture());
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        Log.e("==相机==","change " + bytes);
                        // TODO 将返回的 数据（YUY NV21）编码成h264并且保存
                    }
                });

            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                if (camera != null) {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                    camera.release();
                    camera = null;
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });
    }

    private int picWidth = 2160;
    private int picHeight = 3840;

    private void initParameter(Camera camera) {
        parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21); // 设置预览数据的格式
        Camera.Size previewSize = getPreviewSize(mBinding.previewCamera.getWidth(),mBinding.previewCamera.getHeight(),parameters.getSupportedPreviewSizes());
        if (previewSize != null) {
            parameters.setPreviewSize(previewSize.width,previewSize.height);
        }
        Camera.Size saveSize = getPreviewSize(picWidth,picHeight,parameters.getSupportedVideoSizes());
        if (saveSize != null) {
            parameters.setPictureSize(saveSize.width,saveSize.height);
        }

        camera.setParameters(parameters);
    }

    private Camera.Size getPreviewSize(int surfaceWidth, int surfaceHeight, List<Camera.Size> sizeList) {
        Camera.Size previewSize = null;
        float previewRatio = (surfaceHeight / surfaceWidth);
        float minDiff = previewRatio;

        for (Camera.Size size : sizeList) {
            if (size.width == surfaceHeight && size.height == surfaceWidth) {
                previewSize = size;
                break;
            }
            float supportRatio = size.width / size.height;
            if (Math.abs(supportRatio - previewRatio) < minDiff) {
                minDiff = Math.abs(supportRatio - previewRatio);
                previewSize = size;
            }
        }
        return previewSize;
    }

    // 用户启动录像，开始录音
    private void startRecord() {
        mIsRecord = true;
        // 开启新的线程进行录制
        new Thread(recordRunnable).start();
    }

    private Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            mAudioRecord.startRecording();
            byte[] bytes = new byte[mRecordBufferSize];
            // 这里建立一个while循环来控制录制的开关
            // 当布尔值为真时，一直录制，在需要关闭的时候，将布尔值设置为false，即可执行停止录制
            // 实际上可以根据录制的状态来控制，但是延迟很高
            while (mIsRecord) {
                // 如果正在录制，则通过audioRecord读取流
                mAudioRecord.read(bytes, 0, bytes.length);
                // 通过mediaCodeC将PCM编码为aac，节省了很大的存储空间
                encodePcmToAAC(bytes.length,bytes);
            }
            mAudioRecord.stop();
        }
    };

    // 用户停止录像同时，停止录音
    private void stopRecord() {
        mIsRecord = false;
    }

    /**
     * pcm编码为aac
     */
    private void encodePcmToAAC(int size, byte[] buffer) {
        // 录音时间，size/ 采样率*声道数 * bits/8
        recordTime += size * 1.0 / (audioSampleRate * 2 * (16 / 8));
        int inputBufferIndex = encoder.dequeueInputBuffer(0);

        if (inputBufferIndex >= 0) {
            ByteBuffer byteBuffer = encoder.getInputBuffer(inputBufferIndex);
            byteBuffer.clear();
            byteBuffer.put(buffer);
            encoder.queueInputBuffer(inputBufferIndex, 0, size, 0, 0);
        }

        int index = encoder.dequeueOutputBuffer(info, 0);

        while (index >= 0) {
            try {
                outByteBuffer = new byte[info.size];
                ByteBuffer byteBuffer = encoder.getInputBuffer(index);
                byteBuffer.position(info.offset);
                byteBuffer.limit(info.offset + info.size);
                byteBuffer.get(outByteBuffer, 7, info.size);
                byteBuffer.position(info.offset);
                outputStreamAAC.write(outByteBuffer, 0, info.size);
                encoder.releaseOutputBuffer(index, false);
                index = encoder.dequeueOutputBuffer(info, 0);
                outByteBuffer = null;
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getADTSSampleRate(int sampleRate) {
        int rate = 4;
        switch (sampleRate) {
            case 96000:
                rate = 0;
                break;
            case 88200:
                rate = 1;
                break;
            case 64000:
                rate = 2;
                break;
            case 48000:
                rate = 3;
                break;
            case 44100:
                rate = 4;
                break;
            case 32000:
                rate = 5;
                break;
            case 24000:
                rate = 6;
                break;
            case 22050:
                rate = 7;
                break;
            case 16000:
                rate = 8;
                break;
            case 12000:
                rate = 9;
                break;
            case 11025:
                rate = 10;
                break;
            case 8000:
                rate = 11;
                break;
            case 7350:
                rate = 12;
                break;
            default:
                break;

        }
        return rate;
    }
}