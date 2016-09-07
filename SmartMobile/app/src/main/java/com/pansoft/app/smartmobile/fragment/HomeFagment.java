package com.pansoft.app.smartmobile.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.FixedSpeedScroller;
import com.pansoft.app.smartmobile.view.NoScrollViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * Created by eunji on 2016/8/9.
 */
public class HomeFagment extends Fragment {
    private NoScrollViewPager mPager;
    private ArrayList<Fragment> fragmentList;
    private RadioGroup mTaskRadioGroup;
    private TextView tv_operation;
    FixedSpeedScroller mScroller = null;
    private ImageView userPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        initView(view);
        setListener();
        return view;
    }

    private void setListener() {
        mTaskRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_untask:
                        mPager.setCurrentItem(0);
                        tv_operation.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_comtask:
                        mPager.setCurrentItem(1);
                        tv_operation.setVisibility(View.GONE);
                        break;
                    default:
                        mPager.setCurrentItem(0);
                        break;
                }
            }
        });

    }
    private void initView(View view) {
        tv_operation = (TextView) view.findViewById(R.id.tv_operation);
        mTaskRadioGroup = (RadioGroup) view.findViewById(R.id.mTaskRadioGroup);
        mPager = (NoScrollViewPager)view.findViewById(R.id.viewpager);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new UnfinishedTaskFragment());
        fragmentList.add(new CompleteTaskFragment());
        //给ViewPager设置适配器
        mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
        mTaskRadioGroup.check(R.id.rb_untask);

        userPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.login_logo);
        Bitmap output= SmartMobileUtil.toRoundCorner(bitmap, 15.0f);
        userPhoto.setImageBitmap(output);
        userPhoto.setVisibility(View.GONE);
    }

    //重写setMenuVisibility方法，不然会出现叠层的现象
    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;
        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }
    }
}
