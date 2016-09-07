package com.pansoft.app.smartmobile.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pansoft.app.smartmobile.R;

import java.util.List;

/**
 * Created by eunji on 2016/8/19.
 */
public class ServiceItemAdapter extends BaseAdapter {
    private Fragment mFragment;

    private LayoutInflater mInflater;

    private List<ServiceItembean> list;

    public ServiceItemAdapter(Fragment mFragment, LayoutInflater mInflater, List<ServiceItembean> list) {
        this.mFragment = mFragment;
        this.mInflater = mInflater;
        this.list = list;
    }
    static class ViewHolder{
        TextView tv_service;
        ImageView iv_service;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ServiceItembean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.griditem_service, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_service = (ImageView) convertView.findViewById(R.id.iv_grid_service);
            viewHolder.tv_service = (TextView) convertView.findViewById(R.id.tv_grid_service);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide
            .with(mFragment)
            .load(getItem(position).getServiceImage())
            .into(viewHolder.iv_service);
        viewHolder.tv_service.setText(getItem(position).getServiceName());
        return convertView;
    }
}
