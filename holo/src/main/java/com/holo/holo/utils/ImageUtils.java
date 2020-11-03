package com.holo.holo.utils;


import android.net.Uri;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Author: wangchengge
 * Date: 2020/10/20
 * Version: 1.0.0
 * Description:图片加载工具类
 */
public class ImageUtils {

    public static void loadImage(SimpleDraweeView view, String url, int width, int height) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height);
        view.setLayoutParams(params);
        Uri uri = Uri.parse(url);
        view.setImageURI(uri);
    }
}
