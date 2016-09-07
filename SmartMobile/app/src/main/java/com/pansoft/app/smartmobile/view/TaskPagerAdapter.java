package com.pansoft.app.smartmobile.view;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by eunji on 2016/8/30.
 */
public class TaskPagerAdapter extends PagerAdapter{
    List<View> viewLists;

    public TaskPagerAdapter(List<View> viewList) {
        this.viewLists = viewList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        System.out.println("第几个pager=="+position);
        try {
            if(viewLists.get(position).getParent()==null)
                ((ViewPager) container).addView(viewLists.get(position), 0);
            else{
                ((ViewGroup)viewLists.get(position).getParent()).removeView(viewLists.get(position));
                ((ViewPager) container).addView(viewLists.get(position), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewLists.get(position);
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
