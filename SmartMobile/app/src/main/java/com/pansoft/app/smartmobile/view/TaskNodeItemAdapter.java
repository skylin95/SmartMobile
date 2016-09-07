package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.StringSignature;
import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.loader.BaseImageInfo;
import com.pansoft.app.smartmobile.loader.ImageInfo;
import com.pansoft.app.smartmobile.loader.ImageLoader;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;

import java.util.List;

/**
 * Created by eunji on 2016/8/23.
 */
public class TaskNodeItemAdapter extends BaseAdapter{

    private Context mContext;

    private LayoutInflater mInflater;

    private List<TaskNodeItembean> list;
    private String appPath;

    public TaskNodeItemAdapter(Context mContext, LayoutInflater mInflater, List<TaskNodeItembean> list) {
        super();
        this.mContext = mContext;
        this.mInflater = mInflater;
        this.list = list;
        appPath = SmartMobileUtil.getAppPath(mContext);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public TaskNodeItembean getItem(int position) {
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
            convertView = mInflater.inflate(R.layout.item_task_node,null);
            viewHolder = new ViewHolder();

            viewHolder.tv_approver = (TextView) convertView.findViewById(R.id.tv_approver);
            viewHolder.tv_approveOpinion = (TextView) convertView.findViewById(R.id.tv_approveOpinion);
            viewHolder.tv_approveTime = (TextView) convertView.findViewById(R.id.tv_approveTime);
            viewHolder.iv_circle = (ImageView) convertView.findViewById(R.id.iv_circle);
            viewHolder.iv_user_photo = (ImageView) convertView.findViewById(R.id.iv_user_photo);
            viewHolder.view_top = convertView.findViewById(R.id.view_top);
            viewHolder.ll_approveOpinion = (LinearLayout) convertView.findViewById(R.id.ll_approveOpinion);
            viewHolder.ll_approveTime = (LinearLayout) convertView.findViewById(R.id.ll_approveTime);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置不同审批状态的view style
        setStyle(viewHolder,getItem(position).getNodeState());

        if (position == 0){
            viewHolder.view_top.setVisibility(View.GONE);
        }else {
            viewHolder.view_top.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_approver.setText(getItem(position).getApprover());

        if (getItem(position).getApproveOpinion()!=null && !"".equals(getItem(position).getApproveOpinion())){
            viewHolder.tv_approveOpinion.setText(getItem(position).getApproveOpinion());
        }
        if (getItem(position).getApproveTime()!=null  && !"".equals(getItem(position).getApproveTime())){
            viewHolder.tv_approveTime.setText(getItem(position).getApproveTime());
        }

        loadApproverPhoto(getItem(position).getApproverId(), appPath, viewHolder.iv_user_photo);

        return convertView;
    }
    static class ViewHolder{
        TextView tv_approver;
        TextView tv_approveOpinion;
        TextView tv_approveTime;
        LinearLayout ll_approveOpinion;
        LinearLayout ll_approveTime;
        ImageView iv_user_photo;
        ImageView iv_circle;
        View view_top;
    }
    private void setStyle(ViewHolder viewHolder,int nodeState){
        switch (nodeState){
            case 2:             //已审批
                viewHolder.tv_approver.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                viewHolder.tv_approveOpinion.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                viewHolder.tv_approveTime.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                viewHolder.iv_circle.setImageResource(R.drawable.checked);
                break;
            case 1:             //当前审批
                viewHolder.tv_approver.setTextColor(mContext.getResources().getColor(R.color.colorhintText));
                viewHolder.iv_circle.setImageResource(R.drawable.shape_circle);
                viewHolder.ll_approveTime.setVisibility(View.GONE);
                viewHolder.ll_approveOpinion.setVisibility(View.GONE);
                break;
            case 0:             //未审批
                viewHolder.tv_approver.setTextColor(mContext.getResources().getColor(R.color.colorhintText));
                viewHolder.iv_circle.setImageResource(R.drawable.shape_circle);
                viewHolder.ll_approveTime.setVisibility(View.GONE);
                viewHolder.ll_approveOpinion.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void loadApproverPhoto(String userId, String appPath, ImageView imageView) {
        try {
            ImageInfo imageInfo = new BaseImageInfo(userId, "", "", appPath, ImageInfo.IMAGE_PURPOSE_USERPHOTO);
            imageInfo.setCircleImage(true);    //圆形头像
            Key cacheKey = new StringSignature(userId);
            Glide.with(mContext).using(new ImageLoader()).load(imageInfo).signature(cacheKey).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
