package com.pansoft.app.smartmobile.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.stream.HttpUrlGlideUrlLoader;
import com.pansoft.app.smartmobile.activity.LoginActivity;
import com.pansoft.app.smartmobile.activity.MyDocumentActivity;
import com.pansoft.app.smartmobile.activity.PersonalMsgActivity;
import com.pansoft.app.smartmobile.activity.ProxyAccreditActivity;
import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.activity.ReceiptAddActivity;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.loader.BaseImageInfo;
import com.pansoft.app.smartmobile.loader.ImageInfo;
import com.pansoft.app.smartmobile.loader.ImageLoader;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.yolanda.nohttp.Binary;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.JsonObjectRequest;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by eunji on 2016/8/9.
 */
public class MineFagment extends Fragment{

    private ImageView iv_user_photo;
    private TextView tv_username;
    private TextView tv_my_signature;
    private TextView tv_department;
    private TextView tv_mobile;
    private RelativeLayout rl_grxx;
    private RelativeLayout rl_sqdl;
    private RelativeLayout rl_wddj;
    private RelativeLayout rl_xzdj;
    private LinearLayout ll_grxx;
    private Button btn_logout;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_mine, null);
        initView(view);
        initData();
        setListener();
        return view;
    }

    private void initView(View view) {
        iv_user_photo = (ImageView) view.findViewById(R.id.iv_user_photo);
        tv_username = (TextView) view.findViewById(R.id.tv_user_name);
        tv_my_signature = (TextView) view.findViewById(R.id.tv_user_sign);
        tv_department = (TextView) view.findViewById(R.id.tv_user_dept);
        tv_mobile = (TextView) view.findViewById(R.id.tv_user_mobile);
        rl_grxx = (RelativeLayout) view.findViewById(R.id.rl_grxx);
        rl_sqdl = (RelativeLayout) view.findViewById(R.id.rl_sqdl);
        rl_wddj = (RelativeLayout) view.findViewById(R.id.rl_wddj);
        rl_xzdj = (RelativeLayout) view.findViewById(R.id.rl_xzdj);
        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        ll_grxx = (LinearLayout) view.findViewById(R.id.ll_grxx);
    }

    private void setListener() {
        rl_grxx.setOnClickListener(goGRXX);
        rl_sqdl.setOnClickListener(goSQDL);
        rl_wddj.setOnClickListener(goWDDj);
        rl_xzdj.setOnClickListener(goXZDJ);
        btn_logout.setOnClickListener(logoutListener);
        ll_grxx.setOnClickListener(goGRXX);
    }

    private void initData() {
        try {
            String userId = SmartMobileUtil.getLoginUser(getActivity());
            loadUserInfo(userId, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //重写setMenuVisibility方法，不然会出现叠层的现象
    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }
    private View.OnClickListener goGRXX = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(),PersonalMsgActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
        }
    };
    private View.OnClickListener goSQDL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(),ProxyAccreditActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
        }
    };
    private View.OnClickListener goWDDj = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(),MyDocumentActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
        }
    };
    private View.OnClickListener goXZDJ = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getActivity(),ReceiptAddActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
        }
    };
    private View.OnClickListener logoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            logout();

            Intent intent = new Intent();
            intent.setClass(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    };

    private void logout() {
        try {
            String appPath = SmartMobileUtil.getAppPath(getActivity());
            String reqUrl = appPath + "logout2.do";

            OnResponseListener listener = new OnResponseListener<JSONObject>() {
                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {

                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }

                @Override
                public void onFinish(int what) {

                }
            };

            SmartHttpClient.getInstance().sendRequest(30001,
                    SmartHttpConstant.HTTP_REQUEST_JSONOBJECT,
                    SmartHttpConstant.HTTP_METHOD_POST,
                    reqUrl,
                    null,
                    null,
                    listener);

        } catch (Exception e) {
            Log.e("Logout", "", e);
        }
    }

    private void loadUserInfo(String userId, String sqlWhere) throws Exception {
        try {
            //校验session是否失效
            SessionValidator sessionValidator = new SessionValidator(null, getActivity());
            sessionValidator.start();
            sessionValidator.join();

            //session失效，跳转登录页面
            if (!sessionValidator.isValidate()) {
                sessionValidator = null;

                Intent loginIntent = new Intent();
                loginIntent.setClass(getActivity(), LoginActivity.class);
                startActivity(loginIntent);

                return;
            }

            sessionValidator = null;

            String appPath = SmartMobileUtil.getAppPath(getActivity());
            String reqUrl = appPath + "mobileGeneralAction.do";

            Map<String, String> reqData = new HashMap<String, String>();
            JSONObject reqObj = new JSONObject();
            reqObj.put("service", "SmartMobileService");
            reqObj.put("method", "queryUserInfo");
            reqObj.put("F_USER_ID", userId);

            reqData.put("jsondata", reqObj.toString());

            OnResponseListener listener = new OnResponseListener<JSONObject>() {
                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {
                    JSONObject rspData = response.get();

                    try {
                        //成功请求到数据
                        if (rspData != null && "0".equals(rspData.getString("RET_CODE"))) {
                            showUserInfo(rspData);
                        } else {
                            throw new Exception(rspData.getString("RET_MSG"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }

                @Override
                public void onFinish(int what) {

                }
            };

            SmartHttpClient.getInstance().sendRequest(40001,
                    SmartHttpConstant.HTTP_REQUEST_JSONOBJECT,
                    SmartHttpConstant.HTTP_METHOD_POST,
                    reqUrl,
                    reqData,
                    null,
                    listener);

        } catch (Exception e) {
            Log.e("LoadUserInfo", "", e);
        }
    }

    private void showUserInfo(JSONObject userInfo) throws Exception {
        String appPath = SmartMobileUtil.getAppPath(getActivity());
        String userid= userInfo.getString("F_USER_ID");
        String userName = userInfo.getString("F_USER_NAME");
        String gender = userInfo.getString("F_GENDER");
        String phone = userInfo.getString("F_TEL_NUM");
        String dept = userInfo.getString("F_BASE_DEPT");
        String deptName = userInfo.getString("F_DEPT_NAME");
        String picUrl = userInfo.getString("F_USER_PIC");

        tv_username.setText(userName);
        tv_mobile.setText(phone);
        tv_department.setText(deptName);

        ImageInfo imageInfo = new BaseImageInfo(userid, "", "", appPath + picUrl, ImageInfo.IMAGE_PURPOSE_NORMAL);
        imageInfo.setCircleImage(true);
        Glide.with(this).using(new ImageLoader()).load(imageInfo).into(iv_user_photo);
    }

    private void uploadUserPhoto(final String userId, final String appPath) {
        try {
            //校验session是否失效
            SessionValidator sessionValidator = new SessionValidator(null, getActivity());
            sessionValidator.start();
            sessionValidator.join();

            //session失效，跳转登录页面
            if (!sessionValidator.isValidate()) {
                sessionValidator = null;

                Intent loginIntent = new Intent();
                loginIntent.setClass(getActivity(), LoginActivity.class);
                startActivity(loginIntent);

                return;
            }

            sessionValidator = null;

            String reqUrl = appPath + "VoucherUploadFileAction.do";

            Map<String, String> reqData = new HashMap<String, String>();
            JSONObject reqObj = new JSONObject();
            reqObj.put("service", "SmartMobileService");
            reqObj.put("method", "queryUserInfo");
            reqObj.put("F_USER_ID", userId);

            reqData.put("jsondata", reqObj.toString());

            OnResponseListener listener = new OnResponseListener<String>() {
                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<String> response) {
                    String rspDataText = response.get();

                    try {
                        JSONObject rspData = new JSONObject(rspDataText);

                        //成功请求到数据
                        if (rspData != null && "0".equals(rspData.getString("F_CODE"))) {
                            String appName = SmartMobileUtil.getAppPath(getActivity());
                            String userPicUrl = rspData.getString("fileurl").replace("/" + appName, "");
                            updataUserPhoto(userId, appPath, userPicUrl);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }

                @Override
                public void onFinish(int what) {

                }
            };

            File file = new File("");
            Binary binary = new FileBinary(file);
            Map<String, Binary> uploadFiles = new HashMap<String, Binary>();
            uploadFiles.put(file.getName(), binary);

            SmartHttpClient.getInstance().sendRequestWithBinary(40001,
                    SmartHttpConstant.HTTP_REQUEST_STRING,
                    SmartHttpConstant.HTTP_METHOD_POST,
                    reqUrl,
                    reqData,
                    null,
                    uploadFiles,
                    listener);

        } catch (Exception e) {
            Log.e("LoadUserInfo", "", e);
        }
    }

    private void updataUserPhoto(String userId, String userPicUrl, String appPath) throws Exception {
        String reqUrl = appPath + "mobileGeneralAction.do";
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(reqUrl, RequestMethod.POST);
        JSONObject reqData = new JSONObject();
        reqData.put("service", "SmartMobileService");
        reqData.put("method", "updateUserPhoto");
        reqData.put("F_USER_ID", userId);
        reqData.put("F_USER_PIC", userPicUrl);
        reqData.put("jsondata", reqData.toString());

        Response<JSONObject> response = NoHttp.startRequestSync(request);

        if (response != null && response.isSucceed()) {
            JSONObject rspData = response.get();
            //上传成功
            if (rspData != null && "0".equals(rspData.getString("RET_CODE"))) {

            } else {
                throw new Exception(rspData.getString("RET_MSG"));
            }
        }
    }
}
