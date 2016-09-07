package com.pansoft.app.smartmobile.view;

import com.pansoft.app.smartmobile.loader.ImageInfo;

import java.io.Serializable;

public class ImageItemBean implements Serializable {
	private ImageInfo imageInfo;
	private String imageUrl;
	private String imageName;
	private String imageSize;
	private String imageTime;
	private String imageKey;

	public ImageItemBean() {

	}

	public ImageItemBean(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}

	public ImageItemBean(String imageUrl, String imageName, String imageSize, String imageTime, String key) {
		this.imageUrl = imageUrl;
		this.imageName = imageName;
		this.imageSize = imageSize;
		this.imageTime = imageTime;
		this.imageKey = key;
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}

	public void setImageInfo(ImageInfo imageInfo) {
		this.imageInfo = imageInfo;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageSize() {
		return imageSize;
	}

	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}

	public String getImageTime() {
		return imageTime;
	}

	public void setImageTime(String imageTime) {
		this.imageTime = imageTime;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}
}