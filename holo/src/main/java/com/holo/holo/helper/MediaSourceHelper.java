package com.holo.holo.helper;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Author: a10943
 * @Date: 2020/9/24
 * @Desc: 获取视频资源帮助类
 */
public final class MediaSourceHelper {

    private Context mApplicationContext;
    private final String mUserAgent;                       // 用户代理，网络请求时一般网站用来确定用户的访问方式
    private Cache mCache;                                  // 缓存

    private HttpDataSource.Factory mHttpDataSourceFactory;  //网络加载资源工厂

    private MediaSourceHelper(Context context) {
        this.mApplicationContext = context.getApplicationContext();
        mUserAgent = Util.getUserAgent(mApplicationContext,mApplicationContext.getApplicationInfo().name);
    }

    private static volatile MediaSourceHelper mInstance = null;

    public static MediaSourceHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MediaSourceHelper.class){
                if (mInstance == null){
                    mInstance = new MediaSourceHelper(context);
                }
            }
        }
        return mInstance;
    }

    public MediaSource getMediaSource(String uri) {
        return getMediaSource(uri,null,false);
    }

    public MediaSource getMediaSource(String uri,boolean isCache) {
        return getMediaSource(uri,null,isCache);
    }

    public MediaSource getMediaSource(String uri, Map<String,String> headers) {
        return getMediaSource(uri,headers,false);
    }

    /**
     * 通过uri获取media资源
     * @param uri     uri路径（本地或者网络）
     * @param headers headers信息
     * @param isCache 是否缓存
     * @return 返回mediaSource
     */
    public MediaSource getMediaSource(String uri, Map<String,String> headers,boolean isCache) {
        Uri contentUri = Uri.parse(uri);
        int urlType = inferUriType(uri);
        DataSource.Factory factory;
        if (isCache){
            // 缓存中获取factory
            factory = getCacheDataSourceFactory();
        }else {
            // 直接获取
            factory = getDataSourceFactory();
        }
        if (mHttpDataSourceFactory != null) {
            setHeaders(headers);
        }
        switch (urlType) {
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(factory).createMediaSource(contentUri);
            case C.TYPE_DASH:
            case C.TYPE_SS:
            default:
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(factory).createMediaSource(contentUri);
        }
    }

    /**
     * 根据uri确定流媒体协议类型
     * @param uri uri
     * @return DASH，SMOOTH STREAM，HLS
     */
    private int inferUriType(String uri) {
        if (uri.contains(".mpd")) {
            return C.TYPE_DASH;
        } else if (uri.contains(".ism") || uri.contains(".isml")) {
            return C.TYPE_SS;
        } else if (uri.contains(".m3u8")) {
            return C.TYPE_HLS;
        } else {
            return C.TYPE_OTHER;
        }
    }

    /**
     * 获取缓存资源工厂
     * @return
     */
    private DataSource.Factory getCacheDataSourceFactory() {
        if (mCache == null){
            mCache = createCache();
        }
        return new CacheDataSourceFactory(
                mCache,
                getDataSourceFactory(),
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        );
    }

    /**
     * 获取资源工厂
     * @return
     */
    private DataSource.Factory getDataSourceFactory() {
        return new DefaultDataSourceFactory(mApplicationContext,getHttpDataSourceFactory());
    }

    /**
     * 获取网络资源工厂
     * @return
     */
    private HttpDataSource.Factory getHttpDataSourceFactory() {
        if (mHttpDataSourceFactory == null) {
            mHttpDataSourceFactory = new DefaultHttpDataSourceFactory(
                    mUserAgent,
                    null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true
            );
        }
        return mHttpDataSourceFactory;
    }

    /**
     * 创建缓存
     * @return
     */
    private Cache createCache() {
        return new SimpleCache(
                new File(mApplicationContext.getExternalCacheDir(),"media_video_cache"),   // 设置缓存目录
                new LeastRecentlyUsedCacheEvictor(512 * 1024 * 1024),                  // LRU算法，设置缓存大小
                new ExoDatabaseProvider(mApplicationContext)                                     // 设置数据库，exoplayer提供的database
        );
    }

    /**
     * 如果传递了header就进行替换userAgent
     * @param headers 传入的headers
     */
    private void setHeaders(Map<String,String> headers){
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String,String> header : headers.entrySet()) {
                String key = header.getKey();
                String value = header.getValue();
                if (TextUtils.equals(key,"User-Agent")) {
                    if (!TextUtils.isEmpty(value)) {
                        try {
                            Field userAgentFiled = mHttpDataSourceFactory.getClass().getDeclaredField("userAgent");
                            userAgentFiled.setAccessible(true);
                            userAgentFiled.set(mHttpDataSourceFactory,value);
                        }catch (Exception e){

                        }
                    }
                } else {
                    mHttpDataSourceFactory.getDefaultRequestProperties().set(key,value);
                }
            }
        }
    }
}
