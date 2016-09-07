package com.pansoft.app.smartmobile.http;

import android.content.Context;
import android.util.Log;

import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpCookie;

/**
 * Created by skylin on 2016-8-18.
 */
public class SessionValidator extends Thread {
    private boolean isValidate = false;
    private HttpCookie cookie = null;
    private Context context = null;

    public SessionValidator(HttpCookie cookie, Context context) {
        this.cookie = cookie;
        this.context = context;
    }

    public void setValidate(boolean validate) {
        this.isValidate = validate;
    }

    public boolean isValidate() {
        return isValidate;
    }

    public void run() {
        if (cookie == null) {
            cookie = SmartHttpClient.getInstance().getSessionCookie();
        }

        try {
            String appPath = SmartMobileUtil.getAppPath(context);
            String reqUrl = appPath + "getSessionAction.do";
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
                    this.setValidate(true);
                }
            }
        } catch(Exception e) {
            Log.e("SmartHttpClient", e.getMessage());
        } finally {
            cookie = null;
            context = null;
        }
    }
}
