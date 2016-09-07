package com.pansoft.app.smartmobile.http;

import android.app.Application;
import android.util.Log;
import android.webkit.CookieManager;

import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.yolanda.nohttp.Binary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by skylin on 2016-8-11.
 * <p>封装NoHttp访问网络的API</p>
 */
public class SmartHttpClient {
    private static SmartHttpClient mSmartClient = new SmartHttpClient();
    private boolean isInited = false;
    private RequestQueue mRequestQueue = null;
    private HttpCookie mSessionCookie = null;
    private boolean sessionValidate = false;
    private String mLoginUserId = "";

    private SmartHttpClient() {
        mRequestQueue = NoHttp.newRequestQueue();
    }

    public static SmartHttpClient getInstance() {
        return mSmartClient;
    }

    /**
     * NoHttp初始化
     * Called when application starting in "onCreate" method
     * @param application
     */
    public void init(Application application) {
        //已经初始化的不再处理
        if (isInited) {
            Log.e("SmartHttpClient.init", "the method com.pansoft.mobile.http.SmartHttpClient.init should called in Context 'onCreate' method.");
            return;
        }

//        NoHttp.initialize(application);
        isInited = true;
        NoHttp.setDefaultConnectTimeout(120 * 1000);
    }

    public void cancelAllRequests() {
        mRequestQueue.cancelAll();
    }

    public void cancleRequest(int sign) {
        mRequestQueue.cancelBySign(sign);
    }

    public void stop() {
        mRequestQueue.stop();
    }

    public void close() {
        cancelAllRequests();
        stop();
    }

    public void sendRequest(int what, int requestType, int method, String url, Map<String, String> reqData, Map<String, String> param, OnResponseListener<String> listener) throws Exception {
        sendRequestWithBinary(what, requestType, method, url, reqData, param, null, listener);
    }

    public void sendRequestWithBinary(int what, int requestType, int method, String url, Map<String, String> reqData, Map<String, String> param, Map<String, Binary> uploadFiles, OnResponseListener<String> listener) throws Exception {
        RequestMethod reqMethod =  RequestMethod.GET;

        switch(method) {
            case SmartHttpConstant.HTTP_METHOD_GET :
                break;
            case SmartHttpConstant.HTTP_METHOD_POST :
                reqMethod = RequestMethod.POST;
                break;
            case SmartHttpConstant.HTTP_METHOD_CONNECT :
                reqMethod = RequestMethod.CONNECT;
                break;
            case SmartHttpConstant.HTTP_METHOD_HEAD :
                reqMethod = RequestMethod.HEAD;
                break;
            case SmartHttpConstant.HTTP_METHOD_PATCH :
                reqMethod = RequestMethod.PATCH;
                break;
            case SmartHttpConstant.HTTP_METHOD_MOVE :
                reqMethod = RequestMethod.MOVE;
                break;
            case SmartHttpConstant.HTTP_METHOD_OPTIONS :
                reqMethod = RequestMethod.OPTIONS;
                break;
            case SmartHttpConstant.HTTP_METHOD_PUT :
                reqMethod = RequestMethod.PUT;
                break;
            case SmartHttpConstant.HTTP_METHOD_TRACE :
                reqMethod = RequestMethod.TRACE;
                break;
            case SmartHttpConstant.HTTP_METHOD_COPY :
                reqMethod = RequestMethod.COPY;
                break;
            case SmartHttpConstant.HTTP_METHOD_DELETE :
                reqMethod = RequestMethod.DELETE;
                break;
        }

        Request request = RequestFactory.getRequestInstance(requestType, url, reqMethod, param);
        request.setConnectTimeout(120*1000);
        request.setReadTimeout(120*1000);
        if (request == null) {
            return;
        }

        //设置请求参数
        if (reqData != null) {
            request.set(reqData);
        }

        if (uploadFiles != null && !uploadFiles.isEmpty()) {
            Set<String> keySet = uploadFiles.keySet();

            String key = "";
            for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
                key = iterator.next();
                request.add(key, uploadFiles.get(key));
            }
        }

        request.setCancelSign(what);
        mRequestQueue.add(what, request, listener);
    }

    public byte[] sendDownloadRequest(int method, String url, Map<String, Object> reqData, OnResponseListener<String> listener) throws Exception {
        throw new Exception("the method com.pansoft.mobile.http.SmartHttpClient.sendDownloadRequest is not implemented.");
    }

    public void setSessionCookie(HttpCookie cookie) {
        this.mSessionCookie = cookie;
    }

    public HttpCookie getSessionCookie() {
        return mSessionCookie;
    }

    public boolean isSessionValidate(HttpCookie cookie) {
        if (cookie == null) {
            cookie = getSessionCookie();
        }

        try {
            String reqUrl = "http://10.1.208.193:8080/KLRQ/getSessionAction.do";
            JSONObject jsonObj = new JSONObject();
            JSONArray keys = new JSONArray();
            jsonObj.put("key", keys);

            Request<String> request = NoHttp.createStringRequest(reqUrl);
            request.add("jsondata", jsonObj.toString());

            Response<String> response = NoHttp.startRequestSync(request);
            if (response != null && response.isSucceed()) {
                String rspData = response.get();
                Log.i("SmartClient", rspData);

                JSONObject rspObj = new JSONObject(rspData);
                if (rspData != null && rspObj.getString("UserId") != null && !"".equals(rspObj.getString("UserId"))) {
                    return true;
                }
            }
        } catch(Exception e) {
            Log.e("SmartHttpClient", e.getMessage());
        }

        return false;
    }

    public void synSession(String domain) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        HttpCookie cookie = getSessionCookie();

        cookieManager.setCookie(domain, cookie.getName() + "=" + cookie.getValue());
    }

    public void setLoginUserId(String userId) {
        this.mLoginUserId = userId;
    }

    public String getLoginUserId() {
        return mLoginUserId;
    }
}
