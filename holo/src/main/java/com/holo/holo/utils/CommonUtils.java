package com.holo.holo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 通用工具类
 */
public class CommonUtils {

    public static Activity scanForActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }
}
