package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.media.Image;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Inflater;

/**
 * Created by eunji on 2016/9/2.
 */
public class DjlxItemAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> list;
    private ArrayMap<Integer,Boolean> mSelectState = new ArrayMap<Integer,Boolean>();
    public DjlxItemAdapter(Context mContext,List<String> list ) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void setSelectState(int position,boolean isselected){
        mSelectState.put(position,isselected);
    }
    public void deleteSelectState(int position){
        mSelectState.remove(position);
    }
    public Set<Integer> getSelectSet(){
        Set<Integer> s = new HashSet<>();
        s = mSelectState.keySet();
        return s;
    }
    public boolean getSelectState(int position){
        if (mSelectState.get(position) != null){
            return mSelectState.get(position);
        }else{
            return false;
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_djlx,null);
            viewHolder.tv_djlx = (TextView) convertView.findViewById(R.id.tv_djlx);
            viewHolder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mSelectState.get(position) == null ? false : mSelectState.get(position)) {
            viewHolder.iv_select.setVisibility(View.VISIBLE);
        }else {
            viewHolder.iv_select.setVisibility(View.GONE);
        }
        viewHolder.tv_djlx.setText(list.get(position));
        return convertView;
    }
    static class ViewHolder{
        TextView tv_djlx;
        ImageView iv_select;
    }
}
