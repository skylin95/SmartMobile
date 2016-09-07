package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pansoft.app.smartmobile.R;

public class ReceiptAddActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_child_applymsg);
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_assign :
                Intent assignDetailIntent = new Intent();
                assignDetailIntent.setClass(this, AssignDetailActivity.class);
                startActivity(assignDetailIntent);

                break;
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
        }
    }
}
