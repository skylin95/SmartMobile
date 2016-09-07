package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;

import java.util.List;

/**
 * Created by eunji on 2016/8/19.
 */
public class AssignItemAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Context mContext;
    private List<AssignItemBean> list;
    public AssignItemAdapter(Context mContext, LayoutInflater mInflater, List<AssignItemBean> list) {
        this.mContext = mContext;
        this.mInflater = mInflater;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public AssignItemBean getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_proxy_accredit,
                    null);
            viewHolder = new ViewHolder();
            viewHolder.tv_flowName = (TextView) convertView.findViewById(R.id.tv_flowName);
            viewHolder.tv_wtrName = (TextView) convertView.findViewById(R.id.tv_wtrName);
            viewHolder.tv_strName = (TextView) convertView.findViewById(R.id.tv_strName);
            viewHolder.tv_startTime = (TextView) convertView.findViewById(R.id.tv_startTime);
            viewHolder.tv_endTime = (TextView) convertView.findViewById(R.id.tv_endTime);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_flowName.setText(getItem(position).getFlowName());
        viewHolder.tv_wtrName.setText(getItem(position).getWtrName());
        viewHolder.tv_strName.setText(getItem(position).getStrName());
        viewHolder.tv_startTime.setText(getItem(position).getStartTime());
        viewHolder.tv_endTime.setText(getItem(position).getEndTime());

        return convertView;
    }
    static class ViewHolder{
        TextView tv_flowName;
        TextView tv_wtrName;
        TextView tv_strName;
        TextView tv_startTime;
        TextView tv_endTime;

    }
}
