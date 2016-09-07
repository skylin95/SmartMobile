package com.pansoft.app.smartmobile.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.fragment.AddressFagment;
import com.pansoft.app.smartmobile.fragment.HomeFagment;
import com.pansoft.app.smartmobile.fragment.MineFagment;
import com.pansoft.app.smartmobile.fragment.ServiceFagment;


public class MainActivity extends FragmentActivity {
    private FrameLayout mHomeContent;
    private RadioGroup  mHomeRadioGroup;
    private RadioButton mHomeRb;
    private RadioButton mServiceRb;
    private RadioButton mAddressRb;
    private RadioButton mMineRb;


    static final int NUM_ITEMS = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView() {
        mHomeContent    = (FrameLayout) findViewById(R.id.mHomeContent);
        mHomeRadioGroup = (RadioGroup) findViewById(R.id.mHomeRadioGroup);
        mHomeRb         = (RadioButton) findViewById(R.id.mHomeRb);
        mServiceRb      = (RadioButton) findViewById(R.id.mServiceRb);
        mAddressRb      = (RadioButton) findViewById(R.id.mAddressRb);
        mMineRb         = (RadioButton) findViewById(R.id.mMineRb);
        mHomeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = 0;
                switch (checkedId) {
                    case R.id.mHomeRb:
                        index = 0;
                        break;
                    case R.id.mServiceRb:
                        index = 1;
                        break;
                    case R.id.mAddressRb:
                        index = 2;
                        break;
                    case R.id.mMineRb:
                        index = 3;
                        break;
                }
                Fragment fragment = (Fragment) fragments.instantiateItem(mHomeContent, index);
                fragments.setPrimaryItem(mHomeContent, 0, fragment);
                fragments.finishUpdate(mHomeContent);
            }
        });
        mHomeRadioGroup.check(R.id.mHomeRb);

    }
    FragmentStatePagerAdapter fragments = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch (i) {
                case 0://首页
                    fragment = new HomeFagment();
                    break;
                case 1://服务
                    fragment = new ServiceFagment();
                    break;
                case 2://通讯录
                    fragment = new AddressFagment();
                    break;
                case 3://我的
                    fragment = new MineFagment();
                    break;
                default:
                    fragment = new HomeFagment();
                    break;

            }
            return fragment;
        }
    };

}
