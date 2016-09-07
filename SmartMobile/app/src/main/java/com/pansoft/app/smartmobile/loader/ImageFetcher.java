package com.pansoft.app.smartmobile.loader;

import android.graphics.Bitmap;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by skylin on 2016-9-1.
 */
public class ImageFetcher implements DataFetcher<InputStream> {
    private ImageInfo imageInfo;
    private volatile boolean isCanceled;
    private InputStream image;
    private Request imageRequest;
    private Request urlRequest;

    public ImageFetcher(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        String imageRemoteUrl = imageInfo.getImageRemoteUrl();
        switch (imageInfo.getImagePurpose()) {
            case ImageInfo.IMAGE_PURPOSE_NORMAL :
                image = getRemoteImage(imageRemoteUrl);
                break;
            case ImageInfo.IMAGE_PURPOSE_USERPHOTO :
                image = getUserPhoto(imageInfo.getImageId(), imageRemoteUrl);
                break;
        }

        return image;
    }

    @Override
    public void cleanup() {
        if (image != null) {
            try {
                image.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (urlRequest != null) {
            urlRequest.cancel();
        }

        if (imageRequest != null) {
            imageRequest.cancel();
        }
    }

    @Override
    public String getId() {
        return imageInfo.getImageId();
    }

    @Override
    public void cancel() {
        isCanceled = true;

        if (urlRequest != null) {
            urlRequest.cancel();
        }

        if (imageRequest != null) {
            imageRequest.cancel();
        }
    }

    private InputStream getRemoteImage(String url) {
        if (url == null || "".equals(url)) {
            return null;
        }

        imageRequest = NoHttp.createImageRequest(url);
        Response<Bitmap> response = NoHttp.startRequestSync(imageRequest);
        InputStream inputStream = null;

        if (response != null && response.isSucceed()) {
            Bitmap bitmap = response.get();
            if (imageInfo.isCircleImage()) {
                float roundPx = imageInfo.getCirclePx();
                bitmap = SmartMobileUtil.toRoundCorner(bitmap, roundPx);
            }

            inputStream = transferBitmap2Stream(bitmap);
        }

        return inputStream;
    }

    private InputStream getUserPhoto(String userId, String appPath) {
        String userPicUrl = "";

        try {
            String reqUrl = appPath + "mobileGeneralAction.do";
            urlRequest = NoHttp.createJsonObjectRequest(reqUrl, RequestMethod.POST);

            JSONObject reqObj = new JSONObject();
            reqObj.put("service", "SmartMobileService");
            reqObj.put("method", "queryUserInfo");
            reqObj.put("F_USER_ID", userId);

            urlRequest.add("jsondata", reqObj.toString());

            Response<JSONObject> response = NoHttp.startRequestSync(urlRequest);

            if (response != null && response.isSucceed()) {
                JSONObject rspObj = response.get();
                if ("0".equals(rspObj.getString("RET_CODE"))) {
                    userPicUrl = appPath + rspObj.getString("F_USER_PIC");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getRemoteImage(userPicUrl);
    }

    private InputStream transferBitmap2Stream(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        ByteArrayInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;

        try {
            outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return inputStream;
    }

    private String saveImage(Bitmap image, String name) {
        String imageDir = "sccard/app/user/photos";
        File file = new File(imageDir, name);
        if (file.exists()) {
            file.delete();
        }

        String imagePath = "";
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            imagePath = file.getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return imagePath;
    }
}
