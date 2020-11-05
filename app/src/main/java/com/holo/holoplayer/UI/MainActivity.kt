package com.holo.holoplayer.UI

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.holo.holoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var mBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
        mBinding?.toDrawView?.setOnClickListener(this)
        mBinding?.toAudio?.setOnClickListener(this)
        mBinding?.toCamera?.setOnClickListener(this)
        mBinding?.toVideo?.setOnClickListener(this)
        mBinding?.toOpenGl?.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
       when(v) {
           mBinding?.toDrawView -> {
               DrawImageActivity.start(this)
           }
           mBinding?.toAudio -> {
               AudioHandleActivity.start(this)
           }
           mBinding?.toCamera -> {
               CameraActivity.start(this)
           }
           mBinding?.toVideo -> {
               VideoHandleActivity.start(this)
           }
           mBinding?.toOpenGl -> {
               OpenGlEsActivity.start(this)
           }
       }
    }
}