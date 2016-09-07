package com.pansoft.app.smartmobile.http;

import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;

import java.util.Map;

/**
 * Created by skylin on 2016-8-11.
 */
public class RequestFactory {
    public static Request getRequestInstance(int type, String url, RequestMethod method, Map<String, String> param) {
        Request request = null;

        switch (type) {
            case SmartHttpConstant.HTTP_REQUEST_JSONOBJECT :
                request = NoHttp.createJsonObjectRequest(url, method);
                break;
            case SmartHttpConstant.HTTP_REQUEST_JSONARRAY :
                request = NoHttp.createJsonArrayRequest(url, method);
                break;
            case SmartHttpConstant.HTTP_REQUEST_IMAGE :
                request = NoHttp.createImageRequest(url, method);
                break;
            case SmartHttpConstant.HTTP_REQUEST_DOWNLOAD :
                break;
            default:
                request = NoHttp.createStringRequest(url, method);
                break;
        }

        return request;
    }
}
