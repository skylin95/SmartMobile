package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartMobileConstant;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;

public class SetPortActivity extends Activity {
    private EditText et_port;
    private String mPort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_port);
        initView();
        setListener();
    }

    private void setListener() {

    }

    private void initView() {
        et_port = (EditText) findViewById(R.id.et_port);

        if (et_port != null) {
            String port = SmartMobileUtil.getAppPort(this);
            et_port.setText(port);
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                mPort = et_port.getText().toString();
                SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SmartMobileConstant.APP_SETTING_PORT, mPort);
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
        mPort = et_port.getText().toString();
        SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SmartMobileConstant.APP_SETTING_PORT, mPort);
        editor.commit();
        super.onDestroy();
    }
}
