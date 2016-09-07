package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kyleduo.switchbutton.SwitchButton;
import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartMobileConstant;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;

public class SetProtocolActivity extends Activity {
    RadioGroup rg_protocol;
    RadioButton rb_http;
    RadioButton rb_https;
    private String mProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_protocol);

        initView();
        initData();
        setListener();
    }

    private void initData() {
        mProtocol = SmartMobileUtil.getAppProtocol(this);

        if (SmartMobileConstant.APP_LT_HTTP.equals(mProtocol)) {
            rb_http.setChecked(true);
        }
        else {
            rb_https.setChecked(true);
        }
    }

    private void setListener() {
        rg_protocol.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_http:
                        mProtocol = SmartMobileConstant.APP_LT_HTTP;
                        break;
                    case R.id.rb_https:
                        mProtocol = SmartMobileConstant.APP_LT_HTTPS;
                        break;
                }
            }
        });
    }
    private void initView() {
        rg_protocol = (RadioGroup) findViewById(R.id.rg_protocol);
        rb_http = (RadioButton) findViewById(R.id.rb_http);
        rb_https = (RadioButton) findViewById(R.id.rb_https);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SmartMobileConstant.APP_SETTING_PROTOCOL, mProtocol);
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
        SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SmartMobileConstant.APP_SETTING_PROTOCOL, mProtocol);
        editor.commit();
        finish();
        overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        super.onDestroy();
    }
}
