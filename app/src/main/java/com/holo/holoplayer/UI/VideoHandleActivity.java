package com.holo.holoplayer.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.holo.holoplayer.R;
import com.holo.holoplayer.databinding.ActivityVideoHandleBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * aac是PCM的编码后格式，h265则是YUY(Android相机回调的NV21数据)编码后的格式
 * 合并打包成为mp4视频文件
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class VideoHandleActivity extends AppCompatActivity {


    public static void start(Context context) {
        context.startActivity(new Intent(context,VideoHandleActivity.class));
    }

    private ActivityVideoHandleBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVideoHandleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.extractorVideo.setOnClickListener(clickListener);
        mBinding.mergeVideo.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mBinding.mergeVideo == view) {
                // 合并视频

            }
            if (mBinding.extractorVideo == view) {
                // 分离视频
                extractorMedia(getExternalCacheDir().getPath());
            }
        }
    };

    /**
     * 将视频分离成视频和音频
     * @param savePath 保存分离出来的文件的地址
     */
    private void extractorMedia(String savePath) {
        FileOutputStream videoOutputStream = null;
        FileOutputStream audioOutputStream = null;

        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            // 分离的h264视频文件
            File videoFile = new File(savePath,"output_video.h264");
            // 分离的aac音频文件
            File audioFile = new File(savePath,"output_audio.aac");
            videoOutputStream = new FileOutputStream(videoFile);
            audioOutputStream = new FileOutputStream(audioFile);
            // 输入文件，可以是网络文件
            AssetFileDescriptor fd = getResources().openRawResourceFd(R.raw.baby);
            mediaExtractor.setDataSource(fd);

            // 获取视频的信道数，视频轨道和音频轨道
            int trackCount = mediaExtractor.getTrackCount();
            Log.e("trackCount: ", "信道数" + trackCount);
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            for (int i = 0;i < trackCount;i ++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                if (mineType.startsWith("video/")) {
                    // 视频信道
                    videoTrackIndex = i;
                }
                if (mineType.startsWith("audio/")) {
                    // 音频信道
                    audioTrackIndex = i;
                }
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);

            // 切换到视频信道
            mediaExtractor.selectTrack(videoTrackIndex);
            while(true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer,0);
                Log.e("视频帧长度"," " + readSampleCount);
                if (readSampleCount < 0) {
                    break;
                }
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                videoOutputStream.write(buffer);
                byteBuffer.clear();
                // 进行到下一帧
                mediaExtractor.advance();
            }

            // 切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex);
            while(true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer,0);
                Log.e("音频帧长度"," " + readSampleCount);
                if (readSampleCount < 0) {
                    break;
                }
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                // 给aac添加adts头
                byte[] accAudioBuffer = new byte[readSampleCount + 7];
                addADTStoPacket(accAudioBuffer,readSampleCount + 7);
                // 将装有音频的buffer复制到添加过头信息的buffer中
                System.arraycopy(buffer,0,accAudioBuffer,7,readSampleCount);
                audioOutputStream.write(accAudioBuffer);
                byteBuffer.clear();
                // 进行到下一帧
                mediaExtractor.advance();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            mediaExtractor.release();
            mediaExtractor = null;
            try {
                videoOutputStream.close();
                audioOutputStream.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加aac的头
     * @param packet 一帧数据
     * @param packetLen 一帧数据的长度
     */
    private void addADTStoPacket(byte[] packet,int packetLen) {
        int profile = 2;
        int freqIdx = getFreqIdx(44100);
        int chanCfg = 2;

        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }

    private int getFreqIdx(int sampleRate) {
        int freqIdx;
        switch (sampleRate) {
            case 96000:
                freqIdx = 0;
                break;
            case 88200:
                freqIdx = 1;
                break;
            case 64000:
                freqIdx = 2;
                break;
            case 48000:
                freqIdx = 3;
                break;
            case 44100:
                freqIdx = 4;
                break;
            case 32000:
                freqIdx = 5;
                break;
            case 24000:
                freqIdx = 6;
                break;
            case 22050:
                freqIdx = 7;
                break;
            case 16000:
                freqIdx = 8;
                break;
            case 12000:
                freqIdx = 9;
                break;
            case 11025:
                freqIdx = 10;
                break;
            case 8000:
                freqIdx = 11;
                break;
            case 7350:
                freqIdx = 12;
                break;
            default:
                freqIdx = 8;
                break;
        }
        return freqIdx;
    }


}