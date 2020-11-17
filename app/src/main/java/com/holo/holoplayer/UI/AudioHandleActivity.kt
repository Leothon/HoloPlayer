package com.holo.holoplayer.UI

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.*
import android.os.Bundle
import android.os.Process
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.holo.holoplayer.databinding.ActivityAudioHandleBinding
import com.holo.holoplayer.utils.PcmToWavUtil
import java.io.*
import java.nio.ByteBuffer

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

    private var permissions: Array<String>? = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var mBinding: ActivityAudioHandleBinding? = null

    // 录音缓冲区
    private var mRecordBufferSize: Int? = null
    // 播放缓冲区
    private var mMinBufferSize: Int? = null

    private var mAudioRecord: AudioRecord? = null

    private var mIsRecord: Boolean = false
    private var pcmFile: File? = null

    private var mAudioTrack: AudioTrack? = null
    private var mDataInputStream: DataInputStream? = null
    private var mRecordThread: Thread? = null
    private var isStart: Boolean = false


    private var encoderFormat: MediaFormat? = null;
    private var encoder: MediaCodec? = null;
    private var outputStream: FileOutputStream? = null;
    private var info: MediaCodec.BufferInfo? = null;

    private var perPcmSize = 0;
    private var outByteBuffer: ByteArray? = null;
    private var aacSampleRate = 4;
    private var recordTime: Double = 0.0;
    private var audioSampleRate = 0;


    companion object{

        /**
        AudioManager.STREAM_MUSIC：用于音乐播放的音频流。
        AudioManager.STREAM_SYSTEM：用于系统声音的音频流。
        AudioManager.STREAM_RING：用于电话铃声的音频流。
        AudioManager.STREAM_VOICE_CALL：用于电话通话的音频流。
        AudioManager.STREAM_ALARM：用于警报的音频流。
        AudioManager.STREAM_NOTIFICATION：用于通知的音频流。
        AudioManager.STREAM_BLUETOOTH_SCO：用于连接到蓝牙电话时的手机音频流。
        AudioManager.STREAM_SYSTEM_ENFORCED：在某些国家实施的系统声音的音频流。
        AudioManager.STREAM_DTMF：DTMF音调的音频流。
        AudioManager.STREAM_TTS：文本到语音转换（TTS）的音频流。
         */
        private const val mStreamType: Int = AudioManager.STREAM_MUSIC

        /**
        AudioTrack.MODE_STREAM
        STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到AudioTrack中。
        这个和我们在socket中发送数据一样，应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到AudioTrack。
        这种方式的坏处就是总是在JAVA层和Native层交互，效率损失较大。

        AudioTrack.MODE_STATIC
        STATIC就是数据一次性交付给接收方。
        好处是简单高效，只需要进行一次操作就完成了数据的传递;
        缺点当然也很明显，对于数据量较大的音频回放，显然它是无法胜任的，因而通常只用于播放铃声、系统提醒等对内存小的操作
         */
        private val mMode = AudioTrack.MODE_STREAM

        fun start(context: Context) {
            context.startActivity(Intent(context, AudioHandleActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAudioHandleBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
        permissions?.let { checkPermission(it) }
        mBinding?.startRecord?.setOnClickListener(onClickListener)
        mBinding?.startPlay?.setOnClickListener(onClickListener)
        mBinding?.startPlay?.visibility = View.GONE
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
            mBinding?.startPlay -> {
                if (isStart) {
                    stopPlay()
                } else {
                    startPlay(externalCacheDir?.path + "/audioRecord.pcm")
                }
            }
        }
    }

    private fun permissionGranted() {
        initMinBufferSize()
        initAudioRecord()
        initAudioTrack()
        initMediaCodec(44100,File(this.externalCacheDir?.path, "audioRecord.aac"))
    }


    private fun initMediaCodec(sampleRate: Int, outFile: File) {
        try {
            aacSampleRate = getADTSSampleRate(sampleRate)
            // 立体声
            encoderFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, 2)
            encoderFormat?.setInteger(MediaFormat.KEY_BIT_RATE, 96000)
            encoderFormat?.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
            encoderFormat?.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096)
            encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            info = MediaCodec.BufferInfo()
            if (encoder == null) {
                return
            }
            recordTime = 0.0
            encoder?.configure(encoderFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            outputStream = FileOutputStream(outFile)
            encoder?.start()
        }catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun encodecPcmToAAC(size: Int, buffer: ByteArray) {
        // 录音时间，size/ 采样率*声道数 * bits/8
        recordTime += size * 1.0 / (audioSampleRate * 2 * (16 / 8))
        var inputBufferIndex: Int? = encoder?.dequeueInputBuffer(0)
        if (inputBufferIndex != null) {
            if (inputBufferIndex >= 0) {
                var byteBuffer: ByteBuffer? = encoder?.inputBuffers?.get(inputBufferIndex)
                byteBuffer?.clear()
                byteBuffer?.put(buffer)
                encoder?.queueInputBuffer(inputBufferIndex, 0, size, 0, 0);
            }
        }

        var index: Int = encoder?.dequeueOutputBuffer(info!!, 0)!!

        while (index >= 0) {
            try {
                perPcmSize = info?.size?.plus(7)!!
                outByteBuffer = ByteArray(perPcmSize)

                var byteBuffer: ByteBuffer? = index?.let { encoder?.outputBuffers?.get(it) }
                byteBuffer?.position(info?.offset!!)
                byteBuffer?.limit(info?.offset!! + info?.size!!)
                addADTSHeader(outByteBuffer!!, perPcmSize, aacSampleRate)
                byteBuffer?.get(outByteBuffer, 7, info?.size!!)
                byteBuffer?.position(info?.offset!!)
                outputStream?.write(outByteBuffer, 0, perPcmSize)

                encoder?.releaseOutputBuffer(index!!, false)
                index = encoder?.dequeueOutputBuffer(info!!, 0)!!
                outByteBuffer = null
            }catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun addADTSHeader(packet: ByteArray, packetLen: Int, sampleRate: Int) {
        var profile: Int = 2;
        var freqIdx: Int = sampleRate;
        var chanCfg: Int = 2;

        packet[0] = 0xFF.toByte() // 0xFFF(12bit) 这里只取了8位，所以还差4位放到下一个里
        packet[1] = 0xF9.toByte() // 第一个t位放F
        packet[2] = ((profile - 1 shl 6) + (freqIdx shl 2) + (chanCfg shr 2)).toByte()
        packet[3] = ((chanCfg and 3 shl 6) + (packetLen shr 11)).toByte()
        packet[4] = (packetLen and 0x7FF shr 3).toByte()
        packet[5] = ((packetLen and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }

    private fun getADTSSampleRate(sampleRate: Int): Int {
        var rate = 4
        when (sampleRate) {
            96000 -> {
                rate = 0
            }
            88200 -> {
                rate = 1
            }
            64000 -> {
                rate = 2
            }
            48000 -> {
                rate = 3
            }
            44100 -> {
                rate = 4
            }
            32000 -> {
                rate = 5
            }
            24000 -> {
                rate = 6
            }
            22050 -> {
                rate = 7
            }
            16000 -> {
                rate = 8
            }
            12000 -> {
                rate = 9
            }
            11025 -> {
                rate = 10
            }
            8000 -> {
                rate = 11
            }
            7350 -> {
                rate = 12
            }

        }
        return rate
    }

    /**
     * 初始化获取每一帧流的大小
     */
    private fun initMinBufferSize() {
        // 第一个参数，采样率
        // 第二个参数，声道数.CHANNEL_CONFIGURATION_STEREO 双声道，CHANNEL_IN_MONO 单声道
        // 第三个参数，采样精度，16比特
        mRecordBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        //指定采样率 （MediaRecorder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
        mMinBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
    }

    private fun initAudioRecord() {
        // 第一个参数，音频源，MIC代表麦克风
        mAudioRecord = mRecordBufferSize?.let { AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, it) }
    }

    private fun initAudioTrack() {
        mAudioTrack = mMinBufferSize?.let { AudioTrack(mStreamType, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, it, mMode) }
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
                bytes?.let { mAudioRecord?.read(it, 0, it.size) }
                // 通过mediaCodeC将PCM编码为aac，节省了很大的存储空间
                bytes?.size?.let { encodecPcmToAAC(it,bytes) }
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

    private var playRunnable = Runnable {
        try {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            var tempBuffer: ByteArray? = mMinBufferSize?.let { ByteArray(it) }
            var readCount: Int? = 0
            while (mDataInputStream?.available()!! > 0) {
                readCount = mDataInputStream?.read(tempBuffer)
                if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                    continue;
                }
                if (readCount != 0 && readCount != -1) {
                    // 未初始化
                    if (mAudioTrack?.state == AudioTrack.STATE_UNINITIALIZED) {
                        initAudioTrack()
                    }
                    mAudioTrack?.play()
                    readCount?.let { tempBuffer?.let { it1 -> mAudioTrack?.write(it1, 0, it) } }
                }
            }
            stopPlay();
        }catch (e: Exception) {
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
        pcmFile = File(this.externalCacheDir?.path, "audioRecord.pcm")
        var handleWavFile = File(this.externalCacheDir?.path, "audioRecord_handle.wav")
        var pcmToWavUtil = PcmToWavUtil(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        pcmToWavUtil.pcmToWav(pcmFile.toString(), handleWavFile.toString())
    }

    private fun startRecord() {
        // 获取存储目录，创建文件
        pcmFile = File(this.externalCacheDir?.path, "audioRecord.pcm")
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
        mBinding?.startPlay?.visibility = View.VISIBLE
    }

    private fun startPlay(path: String) {
        isStart = true;
        mBinding?.showRecordStatus?.text = "播放中..."
        mDataInputStream = DataInputStream(FileInputStream(File(path)))
        destroyThread()
        if (mRecordThread == null) {
            mRecordThread = Thread(playRunnable)
            mRecordThread?.start()
        }
    }

    private fun stopPlay() {
        runOnUiThread {
            mBinding?.startPlay?.visibility = View.GONE
            mBinding?.showRecordStatus?.text = "播放完"
        }
        try {
            destroyThread()
            if (mAudioTrack?.state == AudioRecord.STATE_INITIALIZED) {
                mAudioTrack?.stop()
            }
            mAudioTrack?.release()
            mDataInputStream?.close()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun destroyThread() {
        try {
            isStart = false
            if(null != mRecordThread && Thread.State.RUNNABLE == mRecordThread?.state) {
                try {
                    Thread.sleep(500)
                    mRecordThread?.interrupt()
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            mRecordThread = null
        }catch (e: Exception) {
            e.printStackTrace()
        }finally {
            mRecordThread = null
        }
    }




    private fun checkPermission(permissions: Array<String>) {

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