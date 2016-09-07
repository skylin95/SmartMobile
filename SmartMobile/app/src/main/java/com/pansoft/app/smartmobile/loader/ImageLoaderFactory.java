package com.pansoft.app.smartmobile.loader;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;

import java.io.InputStream;

/**
 * Created by skylin on 2016-9-1.
 */
public class ImageLoaderFactory implements ModelLoaderFactory<ImageInfo, InputStream> {
    private ModelCache<ImageInfo, ImageInfo> modelCache = new ModelCache<ImageInfo, ImageInfo>(500);

    @Override
    public ModelLoader<ImageInfo, InputStream> build(Context context, GenericLoaderFactory factories) {
        return new ImageLoader(modelCache);
    }

    @Override
    public void teardown() {

    }
}
