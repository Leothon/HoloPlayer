package com.holo.holo.factory;

import android.content.Context;

import com.holo.holo.render.IRenderView;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 渲染view工厂
 */
public abstract class RenderViewFactory {

    public abstract IRenderView createRenderView(Context context);

}
