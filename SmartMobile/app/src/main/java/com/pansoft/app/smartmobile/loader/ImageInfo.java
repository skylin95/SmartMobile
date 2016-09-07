package com.pansoft.app.smartmobile.loader;

import java.io.Serializable;

/**
 * Created by skylin on 2016-9-1.
 */
public interface ImageInfo extends Serializable {
    //普通图片
    public static final int IMAGE_PURPOSE_NORMAL         = 0;
    //用户头像
    public static final int IMAGE_PURPOSE_USERPHOTO      = 1;

    public String getImageId();
    public void setImageId(String id);

    public String getImageName();
    public void setImageName(String name);

    public String getImageLocalUrl();
    public void setImageLocalUrl(String url);

    public String getImageRemoteUrl();
    public void setImageRemoteUrl(String url);

    public int getImagePurpose() ;
    public void setImagePurpose(int purpose);

    public boolean isCircleImage() ;
    public void setCircleImage(boolean circle);

    public int getCircleWidth() ;
    public void setCircleWidth(int width);

    public int getCircleHeight() ;
    public void setCircleHeight(int height);

    public float getCirclePx();
    public void setCirclePx(float pixel);
}
