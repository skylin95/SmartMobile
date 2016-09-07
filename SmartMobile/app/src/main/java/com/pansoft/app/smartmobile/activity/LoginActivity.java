package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.Encrypt;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.AnimationToast;
import com.pansoft.app.smartmobile.view.LoadingDialog;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends Activity {
    private static final int LOGIN_TYPE_CODE  = 0;
    private static final int LOGIN_TYPE_MAIL  = 1;
    private static final int LOGIN_TYPE_PHONE = 2;
    private static final String[] LOGIN_TYPE_HINT = { "用户编码或手机", "邮箱", "手机" };

    private EditText           et_username;
    private EditText           et_password;
    private Button             btn_login;
    private SwitchButton       sb_remember;
    private TextView           tv_loginText;
    private ImageView          iv_setting;
    private InputMethodManager manager;
    private boolean            isRemeberPSWD = false;
    private int                mLoginType;
    private Resources          resources;
    private Context            mContext = this;
    private String             userName;
    private String             userPass;
    private LoadingDialog      loadDialog = null;
    private View               toastRoot;
    private ImageView          iv_login_type;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initEvent();
        NoHttp.initialize(getApplication());
    }

    private void initEvent() {
        //点击用户图标切换登录方式
        iv_login_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLoginType();
            }
        });
    }

    //切换登录方式
    private void changeLoginType() {
        switch (mLoginType) {
            case LOGIN_TYPE_CODE :
                mLoginType = LOGIN_TYPE_MAIL;
                iv_login_type.setImageResource(R.drawable.mail);
                break;
            case LOGIN_TYPE_MAIL :
                mLoginType = LOGIN_TYPE_CODE;
                iv_login_type.setImageResource(R.drawable.user);
                break;
        }

        et_username.setHint(LOGIN_TYPE_HINT[mLoginType]);
        SmartMobileUtil.setLoginType(this, mLoginType);
    }

    @Override
    protected void onStart() {
        mLoginType = SmartMobileUtil.getLoginType(this);
        et_username.setHint(LOGIN_TYPE_HINT[mLoginType]);

        switch (mLoginType) {
            case LOGIN_TYPE_CODE :
                iv_login_type.setImageResource(R.drawable.user);
                break;
            case LOGIN_TYPE_MAIL :
                iv_login_type.setImageResource(R.drawable.mail);
                break;
        }

        super.onStart();
    }

    private void initData() {
        SharedPreferences sp = mContext.getSharedPreferences("userMsg", MODE_PRIVATE);
        userName = sp.getString("userName", "").toString();
        userPass = sp.getString("userPass","").toString();
        if(userName != null && !"".equals(userName)){
            et_username.setText(userName);
        }
        if(userPass != null && !"".equals(userPass)){
            et_password.setText(userPass);
        }
        isRemeberPSWD = sp.getBoolean("isRemeberPSWD",false);
        sb_remember.setChecked(isRemeberPSWD);

    }

    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }
    /**
     * 控件初始化
     * */
    private void initView() {
        resources           = getResources();
        manager             = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        et_username         = (EditText) findViewById(R.id.et_username);
        et_password         = (EditText) findViewById(R.id.et_password);
        btn_login           = (Button) findViewById(R.id.btn_login);
        sb_remember         = (SwitchButton) findViewById(R.id.sb_remember);
        tv_loginText        = (TextView) findViewById(R.id.tv_loginText);
        iv_setting          = (ImageView) findViewById(R.id.iv_setting);
        iv_login_type       = (ImageView) findViewById(R.id.iv_login_type);
    }

    /**
     * 点击事件
     * */
    public void OnClick(View view) throws Exception {
        switch (view.getId()){
            case R.id.btn_login:
                loadDialog = new LoadingDialog(this);
                loadDialog.setMsg("登陆中...");
                loadDialog.show();
                onLogin();
                break;
            case R.id.sb_remember:
                if(sb_remember.isChecked()){
                    isRemeberPSWD = true;
                }else {
                    isRemeberPSWD = false;
                }
                break;
            case R.id.iv_setting:
                startActivity(new Intent(this,SettingActivity.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
            default:
                break;
        }
    }

    /**
     * 响应登录按钮点击事件
     * @throws Exception
     */
    private void onLogin() throws Exception {
        userName = et_username.getText().toString();

        if (userName == null || "".equals(userName)) {
            String userEmptyError = resources.getString(R.string.login_user_empty);
            tv_loginText.setText(userEmptyError);
            return;
        }

        userName = userName.trim();
        userPass = et_password.getText().toString();
        if (userPass != null) {
            userPass = userPass.trim();
        }

        /**
         String reqUrl = "http://192.168.1.104:8080/KLRQ/login2.do";
         Request<JSONObject> request = NoHttp.createJsonObjectRequest(reqUrl, RequestMethod.POST);
         JSONObject reqObj = new JSONObject();
         reqObj.put("USR_ID", userName);
         reqObj.put("USR_PASSWORD", userPass);
         reqObj.put("USR_LAN", "ZH");
         reqObj.put("ENV_DS_ID", "Default");
         reqObj.put("USR_DEVICE", "");

         request.add("jsondata", reqObj.toString());
         RequestQueue queue = NoHttp.newRequestQueue();
         queue.add(1000, request, new LoginListener());*/
        login(mLoginType, "", userName, userPass);
    }

    /**
     * 用户登录
     * @param logintType
     * @param action
     * @param userId
     * @param userPass
     * @throws Exception
     */
    private void login(int logintType, String action, String userId, String userPass) throws Exception {
        String protocol = SmartMobileUtil.getAppProtocol(this);
        String host = SmartMobileUtil.getAppHost(this);
        String port = SmartMobileUtil.getAppPort(this);
        String app = SmartMobileUtil.getApplication(this);

        StringBuffer reqUrl = new StringBuffer();
        reqUrl.append(protocol).append("://");
        reqUrl.append(host);
        if (port != null && !"".equals(port)) {
            reqUrl.append(":").append(port);
        }

        reqUrl.append("/");

        if (app != null && !"".equals(app)) {
            reqUrl.append(app).append("/");
        }

        if (action == null || "".equals(action)) {
            reqUrl.append("login2.do");
        } else {
            if (action.startsWith("/")) {
                action = action.substring(1);
            }

            reqUrl.append(action);
        }

        //加密密码(3DES)
        String encodeKey1 = resources.getString(R.string.login_encode_key1);
        String encodeKey2 = resources.getString(R.string.login_encode_key2);
        String encodeKey3 = resources.getString(R.string.login_encode_key3);
        userPass = Encrypt.strEncode(userPass, encodeKey1, encodeKey2, encodeKey3);

        Map<String, String> reqData = new HashMap<String, String>();
        JSONObject reqObj = new JSONObject();
        reqObj.put("USR_LAN", "ZH");
        reqObj.put("ENV_DS_ID", "Default");

        switch (logintType) {
            case LOGIN_TYPE_CODE :
                reqObj.put("USR_ID", userId);
                reqObj.put("USR_PASSWORD", userPass);
                reqObj.put("USR_DEVICE", "");
                break;
            case LOGIN_TYPE_MAIL :
                reqObj.put("USR_ID", "");
                reqObj.put("USR_EMAIL", userId);
                reqObj.put("USR_DEVICE", userPass);
                break;
            case LOGIN_TYPE_PHONE :
                reqObj.put("USR_ID", userId);
                reqObj.put("USR_PASSWORD", userPass);
                break;
        }

        reqData.put("jsondata", reqObj.toString());

        OnResponseListener listener = new LoginListener();
        SmartHttpClient.getInstance().sendRequest(1000,
                SmartHttpConstant.HTTP_REQUEST_JSONOBJECT,
                SmartHttpConstant.HTTP_METHOD_POST,
                reqUrl.toString(),
                reqData,
                null,
                listener);

    }

    class LoginListener implements OnResponseListener<JSONObject> {
        public void onStart(int what) {
            Log.i("SmartMobileLogin", "开始登录");
        }

        public void onSucceed(int what, Response<JSONObject> response) {
            JSONObject rspData = response.get();

            try {
                if (rspData != null && "0".equals(rspData.getString("RES_CODE"))) {
                    List<HttpCookie> cookieList = response.getCookies();
                    for (HttpCookie cookie : cookieList) {
                        if ("JSESSIONID".equals(cookie.getName())) {
                            SmartHttpClient.getInstance().setSessionCookie(cookie);
                        }
                    }

                    String userId = rspData.getString("USR_ID");
                    SmartHttpClient.getInstance().setLoginUserId(userId);

                    SharedPreferences sp = mContext.getSharedPreferences("userMsg", MODE_PRIVATE);
                    Editor editor = sp.edit();
                    editor.putString("userName", userName);
                    if(isRemeberPSWD){
                        editor.putString("userPass", userPass);
                    }
                    editor.putBoolean("isRemeberPSWD",isRemeberPSWD);
                    editor.commit();
                    Log.i("login.success", rspData.toString());
                    AnimationToast.makeText(
                            LoginActivity.this,
                            "登录成功",true,
                            AnimationToast.LENGTH_LONG,
                            LoginActivity.this.getWindow().getDecorView(), Gravity.BOTTOM
                    ).show();
                    /**
                     * 跳转到主页面
                     */
                    Intent mainIntent = new Intent();
                    mainIntent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    loadDialog.dismiss();

                } else {
                    Log.e("login.failure", rspData.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            Log.i("SmartMobileLogin", "登录异常" + exception.getMessage());
            AnimationToast.makeText(
                    LoginActivity.this,
                    "登录异常",false,
                    AnimationToast.LENGTH_LONG,
                    LoginActivity.this.getWindow().getDecorView(), Gravity.BOTTOM
            ).show();
            loadDialog.dismiss();
        }

        public void onFinish(int what) {
            Log.i("SmartMobileLogin", "完成登录");
        }
    }
}
