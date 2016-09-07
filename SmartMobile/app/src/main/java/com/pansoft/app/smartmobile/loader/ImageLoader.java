package com.pansoft.app.smartmobile.loader;

import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

/**
 * Created by skylin on 2016-9-1.
 */
public class ImageLoader implements StreamModelLoader<ImageInfo> {
    private ModelCache<ImageInfo,ImageInfo> modelCache;

    public ImageLoader(ModelCache<ImageInfo,ImageInfo> modelCache) {
        this.modelCache = modelCache;
    }

    public ImageLoader() {
        this(null);
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(ImageInfo model, int width, int height) {
        ImageInfo imageInfo = model;

        if (modelCache != null) {
            imageInfo = modelCache.get(model, 0, 0);

            if (imageInfo == null) {
                modelCache.put(model, 0, 0, model);
                imageInfo = model;
            }
        }

        return new ImageFetcher(imageInfo);
    }
}
