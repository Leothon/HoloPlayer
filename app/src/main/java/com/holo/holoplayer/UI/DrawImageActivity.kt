package com.holo.holoplayer.UI

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.holo.holoplayer.R
import com.holo.holoplayer.databinding.ActivityDrawImageBinding

class DrawImageActivity : AppCompatActivity() {

    private var mBinding: ActivityDrawImageBinding? = null

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context,DrawImageActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDrawImageBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
    }
}