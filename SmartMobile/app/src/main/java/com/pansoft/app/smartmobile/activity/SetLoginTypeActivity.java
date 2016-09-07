package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartMobileConstant;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;

public class SetLoginTypeActivity extends Activity {
    RadioGroup rg_logintype;
    RadioButton rb_email;
    RadioButton rb_mobile;
    RadioButton rb_userid;
    private static final int EMAIL = 1;
    private static final int MOBILE = 2;
    private static final int USERID = 0;
    private int mLoginType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_logintype);
        initView();
        initData();
        setListener();
    }

    private void initData() {
        mLoginType = SmartMobileUtil.getLoginType(this);

        switch (mLoginType){
            case EMAIL:
                rb_email.setChecked(true);
                break;
            case MOBILE:
                rb_mobile.setChecked(true);
                break;
            case USERID:
                rb_userid.setChecked(true);
                break;
            default:
                break;

        }
    }
    private void setListener() {
        rg_logintype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_email:
                        mLoginType = EMAIL;
                        break;
                    case R.id.rb_mobile:
                        mLoginType = MOBILE;
                        break;
                    case R.id.rb_userid:
                        mLoginType = USERID;
                        break;
                }
            }
        });
    }
    private void initView() {
        rg_logintype = (RadioGroup) findViewById(R.id.rg_logintype);
        rb_email = (RadioButton) findViewById(R.id.rb_email);
        rb_mobile = (RadioButton) findViewById(R.id.rb_mobile);
        rb_userid = (RadioButton) findViewById(R.id.rb_userid);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                SharedPreferences sp = this.getSharedPreferences(SmartMobileConstant.APP_SETTING_SP, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(SmartMobileConstant.APP_SETTING_LT, mLoginType);
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
        editor.putInt(SmartMobileConstant.APP_SETTING_LT, mLoginType);
        editor.commit();
        finish();
        overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
        super.onDestroy();
    }
}

