package com.holo.holoplayer.UI

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.holo.holoplayer.databinding.ActivityAudioHandleBinding
import com.holo.holoplayer.utils.PcmToWavUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * AudioRecord负责采集PCM数据，AudioTrack负责播放PCM数据
 * PCM是什么？
 * PCM是脉冲编码调制pulse code modulation,是数字通信编码的一种，将语音，图像等模拟信号隔一段时间进行取样，使其离散化，最后取正量化（大概意思是将模拟信号转化为数字信号）
 * 模拟信号进行采样，每秒从连续信号中提取并组成离散信号的采样个数
 * 量化就是将采样的数据进行一个取整，使模拟信号的无限多值，变为有限个值，采样率越高，数字信号越接近模拟信号.
 * 量化后要将取整的数字信号进行编码并存储
 *
 * 采样频率，每秒对声音进行20000个采样，就能满足人耳的需求
 *
 * AudioRecord,是一个音频采集接口。可以得到原始的一帧帧PCM音频数据
 */
class AudioHandleActivity : AppCompatActivity() {

    private var permissions: Array<String>? = arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var mBinding: ActivityAudioHandleBinding? = null

    private var mRecordBufferSize: Int? = null

    private var mAudioRecord: AudioRecord? = null

    private var mIsRecord: Boolean = false
    private var pcmFile: File? = null

    companion object{
        public fun start(context: Context) {
            context.startActivity(Intent(context,AudioHandleActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAudioHandleBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
        permissions?.let { checkPermission(it) }
        mBinding?.startRecord?.setOnClickListener(onClickListener)
    }

    private var onClickListener = View.OnClickListener{
        when(it) {
            mBinding?.startRecord -> {
                if (mIsRecord) {
                    stopRecord()
                } else {
                    startRecord()
                }
            }
        }
    }

    private fun permissionGranted() {
        initMinBufferSize()
        initAudioRecord()
    }

    private var runnable: Runnable = Runnable {
        mAudioRecord?.startRecording()
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(pcmFile)
            var bytes: ByteArray? = mRecordBufferSize?.let { ByteArray(it) }
            // 这里建立一个while循环来控制录制的开关
            // 当布尔值为真时，一直录制，在需要关闭的时候，将布尔值设置为false，即可执行停止录制
            // 实际上可以根据录制的状态来控制，但是延迟很高
            while (mIsRecord) {
                // 如果正在录制，则通过audioRecord读取流
                bytes?.let { mAudioRecord?.read(it,0, it.size) }
                fileOutputStream.write(bytes)
                fileOutputStream.flush()
            }
            mAudioRecord?.stop()
            fileOutputStream.flush()
            fileOutputStream.close()
            addHeadToWAV()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            mAudioRecord?.stop()
        } catch (e: IOException) {
            e.printStackTrace()
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
    private fun addHeadToWAV(){
        pcmFile = File(this.externalCacheDir?.path,"audioRecord.pcm")
        var handleWavFile = File(this.externalCacheDir?.path,"audioRecord_handle.wav")
        var pcmToWavUtil = PcmToWavUtil(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)
        pcmToWavUtil.pcmToWav(pcmFile.toString(),handleWavFile.toString())
    }

    private fun startRecord() {
        // 获取存储目录，创建文件
        pcmFile = File(this.externalCacheDir?.path,"audioRecord.pcm")
        mIsRecord = true
        // 开启新的线程进行录制
        Thread(runnable).start()
        mBinding?.showRecordStatus?.text = "正在录制..."
        mBinding?.startRecord?.text = "停止录制"
    }

    private fun stopRecord() {
        mIsRecord = false
        mBinding?.showRecordStatus?.text = "录制已停止"
        mBinding?.startRecord?.text = "开始录制"
    }

    private fun initAudioRecord() {
        // 第一个参数，音频源，MIC代表麦克风
        mAudioRecord = mRecordBufferSize?.let { AudioRecord(MediaRecorder.AudioSource.MIC,8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT, it) }
    }

    /**
     * 初始化获取每一帧流的大小
     */
    private fun initMinBufferSize() {
        // 第一个参数，采样率
        // 第二个参数，声道数.CHANNEL_CONFIGURATION_STEREO 双声道，CHANNEL_IN_MONO 单声道
        // 第三个参数，采样精度，16比特
        mRecordBufferSize = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT)
    }



    private fun checkPermission(permissions : Array<String>) {

        val mPermissions: MutableList<String> = mutableListOf()
        for (i in permissions.indices) {
            if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissions.add(permissions[i])
            }
        }
        if (!mPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions, 100)
        } else {
           permissionGranted()
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            permissionGranted()
        }
    }
}