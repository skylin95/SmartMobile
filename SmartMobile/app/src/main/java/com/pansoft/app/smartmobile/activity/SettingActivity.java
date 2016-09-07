package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.AnimationToast;
import com.pansoft.app.smartmobile.view.LoadingDialog;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;



public class SettingActivity extends Activity {
    private LoadingDialog loadDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
            case R.id.rl_protocol:
                startActivity(new Intent(this,SetProtocolActivity.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
            case R.id.rl_ip:
                startActivity(new Intent(this,SetIpActivity.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
            case R.id.rl_port:
                startActivity(new Intent(this,SetPortActivity.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
            case R.id.rl_app:
                startActivity(new Intent(this,SetAppActivity.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
            case R.id.rl_logintype:
                startActivity(new Intent(this,SetLoginTypeActivity.class));
                overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                break;
            case R.id.btn_testNetwork:
                loadDialog = new LoadingDialog(this);
                loadDialog.setMsg("网络连接测试...");
                loadDialog.show();

                checkNetwork();
                break;
            default:
                break;
        }
    }

    private void checkNetwork() {
        String appPath = SmartMobileUtil.getAppPath(this);

        OnResponseListener<String> listener = new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                Log.i("Settings", "网络连接成功");
                loadDialog.dismiss();
                AnimationToast toast = AnimationToast.makeText(
                        SettingActivity.this,
                        "网络连接成功",true,
                        AnimationToast.LENGTH_LONG,
                        SettingActivity.this.getWindow().getDecorView(), Gravity.CENTER
                );
                toast.show();
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                Log.i("Settings", "网络连接失败");
                loadDialog.dismiss();
                AnimationToast.makeText(
                        SettingActivity.this,
                        "网络连接失败",false,
                        AnimationToast.LENGTH_LONG,
                        SettingActivity.this.getWindow().getDecorView(), Gravity.CENTER
                ).show();
            }

            @Override
            public void onFinish(int what) {

            }
        };

        try {
            SmartHttpClient.getInstance().sendRequest(20001, SmartHttpConstant.HTTP_REQUEST_STRING, SmartHttpConstant.HTTP_METHOD_GET, appPath, null, null, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
