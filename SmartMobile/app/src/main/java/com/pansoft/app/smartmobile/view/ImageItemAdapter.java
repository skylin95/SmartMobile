package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.activity.LoginActivity;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eunji on 2016/8/23.
 */
public class ImageItemAdapter extends BaseAdapter{

    private Context mContext;

    private LayoutInflater mInflater;

    private List<ImageItemBean> list;

    public ImageItemAdapter(Context mContext, LayoutInflater mInflater, List<ImageItemBean> list) {
        super();
        this.mContext = mContext;
        this.mInflater = mInflater;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ImageItemBean getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_image, null);
            viewHolder = new ViewHolder();

            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image_thumb);
            viewHolder.tv_image_name = (TextView) convertView.findViewById(R.id.tv_image_name_value);
            viewHolder.tv_image_size = (TextView) convertView.findViewById(R.id.tv_image_size_value);
            viewHolder.tv_image_time =  (TextView) convertView.findViewById(R.id.tv_image_time_value);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageItemBean imageBean = getItem(position);
        viewHolder.tv_image_name.setText(imageBean.getImageName());
        viewHolder.tv_image_size.setText(imageBean.getImageSize());
        viewHolder.tv_image_time.setText(imageBean.getImageTime());

        return convertView;
    }

    static class ViewHolder{
        ImageView iv_image;
        TextView tv_image_name;
        TextView tv_image_size;
        TextView tv_image_time;
    }
}
