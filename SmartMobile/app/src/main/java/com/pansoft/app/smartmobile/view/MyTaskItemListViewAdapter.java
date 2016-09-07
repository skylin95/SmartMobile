package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.loader.BaseImageInfo;
import com.pansoft.app.smartmobile.loader.ImageInfo;
import com.pansoft.app.smartmobile.loader.ImageLoader;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;

import java.util.List;

public class MyTaskItemListViewAdapter extends BaseAdapter {

	private Context mContext;

	private LayoutInflater mInflater;

	private List<TaskItembean> list;

	private ArrayMap<Integer,Boolean> mSelectState = new ArrayMap<Integer,Boolean>();
	private boolean isVisibility;
	private String appPath;

	public MyTaskItemListViewAdapter(Context mContext, List<TaskItembean> list) {
		super();
		this.mContext = mContext;
		this.list = list;
		mInflater = LayoutInflater.from(mContext);
		appPath = SmartMobileUtil.getAppPath(mContext);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public TaskItembean getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}
	public void setOpVisibility(boolean isVisibility){
		this.isVisibility = isVisibility;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//实例化一个viewHolder
		ViewHolder viewHolder;
		onClick listener;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_task_mine,
					null);
			viewHolder = new ViewHolder();
			listener = new onClick();

			viewHolder.ll_left = (LinearLayout) convertView.findViewById(R.id.ll_left);
			viewHolder.ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);
			viewHolder.tv_submit = (TextView) convertView.findViewById(R.id.tv_submit);
			viewHolder.tv_taskname = (TextView) convertView.findViewById(R.id.tv_taskname);
			viewHolder.tv_originator = (TextView) convertView.findViewById(R.id.tv_originator);
			viewHolder.tv_remarks = (TextView) convertView.findViewById(R.id.tv_remarks);
			viewHolder.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			viewHolder.tv_department = (TextView) convertView.findViewById(R.id.tv_department);
			viewHolder.iv_user_photo = (ImageView) convertView.findViewById(R.id.iv_user_photo);

			convertView.setTag(viewHolder.tv_submit.getId(),listener);// 设置tag
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			listener = (onClick) convertView.getTag(viewHolder.tv_submit.getId());// 获取实例
		}
		listener.setPosition(convertView,position);// 传递position
		convertView.setVisibility(View.VISIBLE);
		viewHolder.tv_taskname.setText(getItem(position).getpTaskName());
		viewHolder.tv_originator.setText(getItem(position).getpOriginator());
		viewHolder.tv_remarks.setText(getItem(position).getpRemarks());
		viewHolder.tv_amount.setText(getItem(position).getpAmount());
		viewHolder.tv_time.setText(getItem(position).getpTime());
		viewHolder.tv_department.setText(getItem(position).getpDepartment());

		loadSubmitorPhoto(getItem(position).getSubmitor(), appPath, viewHolder.iv_user_photo);

		return convertView;
	}
	class onClick implements OnClickListener {

		int position;
		View itemView;
		public void setPosition(View itemView,int position) {
			this.itemView = itemView;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_submit:

				default:
					break;
			}

		}
	}
	static class ViewHolder {

		LinearLayout ll_left;
		LinearLayout ll_right;
		TextView tv_submit;

		TextView tv_taskname;
		TextView tv_originator;
		TextView tv_remarks;
		TextView tv_amount;
		TextView tv_time;
		TextView tv_department;
		ImageView iv_user_photo;
	}

	private void loadSubmitorPhoto(String userId, String appPath, ImageView imageView) {
		try {
			ImageInfo imageInfo = new BaseImageInfo(userId, "", "", appPath, ImageInfo.IMAGE_PURPOSE_USERPHOTO);
			imageInfo.setCircleImage(true);
			Glide.with(mContext).using(new ImageLoader()).load(imageInfo).into(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
