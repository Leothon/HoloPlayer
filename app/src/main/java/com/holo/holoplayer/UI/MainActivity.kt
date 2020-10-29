package com.holo.holoplayer.UI

import android.os.Bundle
import android.view.View
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
    }

    override fun onClick(v: View?) {
       when(v) {
           mBinding?.toDrawView -> {
               DrawImageActivity.start(this)
           }
           mBinding?.toAudio -> {
               AudioHandleActivity.start(this)
           }
       }
    }
}