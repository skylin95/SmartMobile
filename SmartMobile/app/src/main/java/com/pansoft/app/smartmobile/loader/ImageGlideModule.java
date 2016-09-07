package com.pansoft.app.smartmobile.loader;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by skylin on 2016-9-1.
 */
public class ImageGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);

        //缓存地址data/data/glide/cache
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide", 500 * 1024 * 1024));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(ImageInfo.class, InputStream.class, new ImageLoaderFactory());
    }
}
