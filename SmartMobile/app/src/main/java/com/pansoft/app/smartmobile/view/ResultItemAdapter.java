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
 * Created by eunji on 2016/8/26.
 */
public class ResultItemAdapter extends BaseAdapter{
    private Context mContext;

    private LayoutInflater mInflater;

    private List<ResultItembean> list;

    public ResultItemAdapter(Context mContext, List<ResultItembean> list) {
        this.mContext = mContext;
        this.list = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ResultItembean getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_approve_result,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_vchrKey = (TextView) convertView.findViewById(R.id.tv_vchrKey);
            viewHolder.tv_result = (TextView) convertView.findViewById(R.id.tv_result);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_vchrKey.setText(getItem(position).getVchrKey()+":");
        viewHolder.tv_result.setText(getItem(position).getResMsg());
        return convertView;
    }
    static class ViewHolder{
        TextView tv_vchrKey;
        TextView tv_result;
    }
}
