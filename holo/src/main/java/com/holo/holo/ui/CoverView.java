package com.holo.holo.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 封面ui
 */
public class CoverView extends ConstraintLayout {
    public CoverView(@NonNull Context context) {
        super(context);
    }

    public CoverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
