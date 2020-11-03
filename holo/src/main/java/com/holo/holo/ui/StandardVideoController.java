package com.holo.holo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holo.holo.controller.GestureVideoController;

/**
 * Author: wangchengge
 * Date: 2020/10/22
 * Version: 1.0.0
 * Description:控制器 ,控制界面的UI可以参考此例继承GestureVideoController编写
 */
public class StandardVideoController extends GestureVideoController implements View.OnClickListener {
    public StandardVideoController(@NonNull Context context) {
        super(context);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }
}
