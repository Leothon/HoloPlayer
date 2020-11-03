package com.holo.holoplayer.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author: wangchengge
 * Date: 2020/10/29
 * Version: 1.0.0
 * Description:处理pcm文件
 */
public class PcmToWavUtil {
    private int mBufferSize;
    private int mSampleRate;
    private int mChannel;

    /**
     * @param sampleRate 采样率
     * @param channel    声道
     * @param encoding   音频格式
     */
    public PcmToWavUtil(int sampleRate,int channel,int encoding) {
        this.mSampleRate = sampleRate;
        this.mChannel = channel;
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate,channel,encoding);
    }

    public void pcmToWav(String inFileName,String outFileName) {
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long totalDataLen;
        long longSampleRate = mSampleRate;
        int channels = mChannel == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
        // 码率 ：采样率 * 采样位数 * 声道个数
        // bytePerSecond = sampleRate * (bitsPerSample / 8) * channels
        long byteRate =  mSampleRate * (16 / 8) * channels;
        byte[] data = new byte[mBufferSize];
        try {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            // 为输出文件添加头
            writeWaveFileHeader(out,totalAudioLen,totalDataLen,longSampleRate,channels,byteRate);

            // 读取输入的文件，并写入已经有信息头的输出文件
            while (in.read(data) != -1) {
                out.write(data);
                out.flush();
            }
            in.close();
            out.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * 麦克风录制的文件时PCM格式的，不包括头部信息，播放器不知道相关信息，所以无法播放，需要将其转化为wav格式
     偏移地址　　 命名 　　　　　　内容
     00-03 　　ChunkId 　　　　　　"RIFF"
     04-07 　　ChunkSize 　　　　　下个地址开始到文件尾的总字节数(此Chunk的数据大小)
     08-11　　 fccType 　　　　　　"WAVE"
     12-15 　　SubChunkId1 　　　   "fmt ",最后一位空格。
     16-19 　　SubChunkSize1 　　　一般为16，表示fmt Chunk的数据块大小为16字节
     20-21 　　FormatTag 　　　　　1：表示是PCM 编码
     22-23 　　Channels 　　　　　   声道数，单声道为1，双声道为2
     24-27 　　SamplesPerSec 　　   采样率
     28-31 　　BytesPerSec 　　　　码率 ：采样率 * 采样位数 * 声道个数，bytePerSecond = sampleRate * (bitsPerSample / 8) * channels
     32-33 　　BlockAlign 　　　　　 每次采样的大小：位宽*声道数/8
     34-35 　　BitsPerSample 　　　 位宽
     36-39 　　SubChunkId2 　　　　"data"
     40-43 　　SubChunkSize2 　　   音频数据的长度
     44-... 　　data 　　　　　　　　音频数据
     */
    private void writeWaveFileHeader(FileOutputStream out,long totalAudioLen,long totalDataLen,long longSampleRate,int channels,long byteRate) throws IOException {

        byte[] header = new byte[44];
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        //WAVE
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        // 'fmt ' chunk
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // 4 bytes: size of 'fmt ' chunk
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // format = 1
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // block align
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        // bits per sample
        header[34] = 16;
        header[35] = 0;
        //data
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header,0,44);
    }
}
