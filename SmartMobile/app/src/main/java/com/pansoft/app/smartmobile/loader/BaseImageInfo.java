package com.pansoft.app.smartmobile.loader;

/**
 * Created by skylin on 2016-9-1.
 */
public class BaseImageInfo implements ImageInfo {
    private String id;
    private String name;
    private String localUrl;
    private String remoteUrl;
    private int purpose = 0;
    private boolean isCirle;
    private int circleWidth;
    private int circleHeight;
    private float circlePx = 0.0f;

    public BaseImageInfo() {

    }

    public BaseImageInfo(String id, String name, String localUrl, String remoteUrl, int purpose) {
        this.id = id;
        this.name = name;
        this.localUrl = localUrl;
        this.remoteUrl = remoteUrl;
        this.purpose = purpose;
    }

    @Override
    public String getImageId() {
        return id;
    }

    @Override
    public void setImageId(String id) {
        this.id = id;
    }

    public String getImageName() {
        return name;
    }

    public void setImageName(String name) {
        this.name = name;
    }

    @Override
    public String getImageLocalUrl() {
        return localUrl;
    }

    @Override
    public void setImageLocalUrl(String url) {
        this.localUrl = url;
    }

    @Override
    public String getImageRemoteUrl() {
        return remoteUrl;
    }

    @Override
    public void setImageRemoteUrl(String url) {
        this.remoteUrl = url;
    }

    @Override
    public int getImagePurpose() {
        return purpose;
    }

    @Override
    public void setImagePurpose(int purpose) {
        this.purpose = purpose;
    }

    @Override
    public boolean isCircleImage() {
        return isCirle;
    }

    @Override
    public void setCircleImage(boolean circle) {
        this.isCirle = circle;
    }

    @Override
    public int getCircleWidth() {
        return circleWidth;
    }

    @Override
    public void setCircleWidth(int width) {
        this.circleWidth = width;
    }

    @Override
    public int getCircleHeight() {
        return circleHeight;
    }

    @Override
    public void setCircleHeight(int height) {
        this.circleHeight = height;
    }

    @Override
    public float getCirclePx() {
        return circlePx;
    }

    @Override
    public void setCirclePx(float pixel) {
        this.circlePx = pixel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (! (obj instanceof ImageInfo)) {
            return false;
        }

        if (hashCode() != obj.hashCode()) {
            return false;
        }

        ImageInfo otherInfo = (ImageInfo) obj;
        if (!(getImageId().equals(otherInfo.getImageId()) && getImageRemoteUrl().equals(otherInfo.getImageRemoteUrl()))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public String toString() {
        return getImageName();
    }
}
