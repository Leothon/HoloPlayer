package com.holo.holo.render;

import android.content.Context;

/**
 * Author: wangchengge
 * Date: 2020/10/20
 * Version: 1.0.0
 * Description:工厂类
 */
public class TikTokRenderViewFactory extends RenderViewFactory{
    public static TikTokRenderViewFactory create() {
        return new TikTokRenderViewFactory();
    }

    @Override
    public IRenderView createRenderView(Context context) {
        return new TikTokRenderView(new TextureRenderView(context));
    }
}
