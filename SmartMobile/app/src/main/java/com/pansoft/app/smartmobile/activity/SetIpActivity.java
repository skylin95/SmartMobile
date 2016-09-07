package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartMobileConstant;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;

public class SetIpActivity extends Activity {
    private EditText et_ip;
    private String mIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);
        initView();
    }
    private void initView() {
        et_ip = (EditText) findViewById(R.id.et_ip);

        if (et_ip != null) {
            String host = SmartMobileUtil.getAppHost(this);
            et_ip.setText(host);
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                mIP = et_ip.getText().toString();
                SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SmartMobileConstant.APP_SETTING_HOST, mIP);
                editor.commit();
                finish();
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mIP = et_ip.getText().toString();
        SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SmartMobileConstant.APP_SETTING_HOST, mIP);
        editor.commit();
        super.onDestroy();
    }
}
