package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartMobileConstant;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;

public class SetAppActivity extends Activity {
    private EditText et_app;
    private String mApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_app);
        initView();
    }

    private void initView() {
        et_app = (EditText) findViewById(R.id.et_app);

        if (et_app != null) {
            String app = SmartMobileUtil.getApplication(this);
            et_app.setText(app);
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                mApp = et_app.getText().toString();
                SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SmartMobileConstant.APP_SETTING_APP, mApp);
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
        mApp = et_app.getText().toString();
        SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SmartMobileConstant.APP_SETTING_APP, mApp);
        editor.commit();
        super.onDestroy();
    }
}
