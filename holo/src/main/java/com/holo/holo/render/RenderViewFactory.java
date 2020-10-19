package com.holo.holo.render;

import android.content.Context;

/**
 * Author: wangchengge
 * Date: 2020/10/19
 * Version: 1.0.0
 * Description:继承该接口实现自己的渲染view的方案，重写createRenderView方法返回renderView
 */
public abstract class RenderViewFactory {

    public abstract IRenderView createRenderView(Context context);
}
