package com.holo.holo.render;

import android.content.Context;

/**
 * Author: wangchengge
 * Date: 2020/10/19
 * Version: 1.0.0
 * Description:创建textureRenderView的工厂类
 */
class TextureRenderViewFactory extends RenderViewFactory{

    public static TextureRenderViewFactory create() {
        return new TextureRenderViewFactory();
    }

    @Override
    public IRenderView createRenderView(Context context) {
        return new TextureRenderView(context);
    }
}
