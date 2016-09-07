package com.pansoft.app.smartmobile.view;

import java.util.List;
import java.util.Set;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
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

public class TaskItemListViewAdapter extends BaseAdapter {

	private Context mContext;

	private LayoutInflater mInflater;

	private List<TaskItembean> list;

	private ArrayMap<Integer,Boolean> mSelectState = new ArrayMap<Integer,Boolean>();

	ISingleProcess mSingleProcess;
	private boolean isVisibility;
	private String appPath;

	public TaskItemListViewAdapter(Context mContext,
								   List<TaskItembean> list) {
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
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	public void setSelectState(int position,boolean isselected){
		mSelectState.put(position,isselected);
	}
	public void setOpVisibility(boolean isVisibility){
		this.isVisibility = isVisibility;
	}
	public void deleteSelectState(int position){
		mSelectState.remove(position);
	}
	public Set<Integer> getSelectSet(){
		Set<Integer> s =mSelectState.keySet();
		return s;
	}

	public boolean getSelectState(int position){
		if (mSelectState.get(position) != null){
			return mSelectState.get(position);
		}else{
			return false;
		}
	}

	public void clearSelectState(){
		mSelectState.clear();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//实例化一个viewHolder
		ViewHolder viewHolder;
		onClick listener;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_task,
					null);
			viewHolder = new ViewHolder();
			listener = new onClick();

			viewHolder.ll_left = (LinearLayout) convertView.findViewById(R.id.ll_left);
			viewHolder.ll_right = (LinearLayout) convertView.findViewById(R.id.ll_right);
			viewHolder.ll_op = (LinearLayout) convertView.findViewById(R.id.ll_op);
			viewHolder.tv_agree = (TextView) convertView.findViewById(R.id.tv_agree);
			viewHolder.tv_refuse = (TextView) convertView.findViewById(R.id.tv_refuse);
			viewHolder.tv_taskname = (TextView) convertView.findViewById(R.id.tv_taskname);
			viewHolder.tv_originator = (TextView) convertView.findViewById(R.id.tv_originator);
			viewHolder.tv_remarks = (TextView) convertView.findViewById(R.id.tv_remarks);
			viewHolder.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);
			viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			viewHolder.tv_department = (TextView) convertView.findViewById(R.id.tv_department);
			viewHolder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
			viewHolder.iv_user_photo = (ImageView) convertView.findViewById(R.id.iv_user_photo);

			viewHolder.tv_agree.setOnClickListener(listener);
			viewHolder.tv_refuse.setOnClickListener(listener);
			viewHolder.tv_agree_inside = (TextView) convertView.findViewById(R.id.tv_agree_inside);
			viewHolder.tv_reject_inside = (TextView) convertView.findViewById(R.id.tv_reject_inside);
			viewHolder.tv_agree_inside.setOnClickListener(listener);
			viewHolder.tv_reject_inside.setOnClickListener(listener);
			convertView.setTag(viewHolder.tv_agree.getId(), listener);// 设置tag
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			listener = (onClick) convertView.getTag(viewHolder.tv_agree.getId());// 获取实例
		}
		listener.setPosition(convertView, position);// 传递position

		if (mSelectState.get(position) == null ? false : mSelectState.get(position)) {
			viewHolder.iv_select.setImageResource(R.drawable.selected);
		}else {
			viewHolder.iv_select.setImageResource(R.drawable.noselected);
		}
		if (isVisibility){
			viewHolder.ll_op.setVisibility(View.VISIBLE);
		}else {
			viewHolder.ll_op.setVisibility(View.GONE);
		}
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
				case R.id.tv_agree:
					mSingleProcess.approveTask(itemView, position);
					break;
				case R.id.tv_refuse:
					mSingleProcess.rejectTask(itemView, position);
					break;
				case R.id.tv_agree_inside:
					mSingleProcess.approveTask(itemView, position);
					break;
				case R.id.tv_reject_inside:
					mSingleProcess.rejectTask(itemView, position);
					break;
				default:
					break;
			}

		}
	}

	public interface ISingleProcess{
		void approveTask(View view,int position);
		void rejectTask(View view,int position);
	}

	public void setSingleProcess(ISingleProcess mSingleProcess) {
		this.mSingleProcess = mSingleProcess;
	}

	static class ViewHolder {
		boolean needInflate = false;
		LinearLayout ll_left;
		LinearLayout ll_right;
		LinearLayout ll_op;
		int pLLLeftWidth;
		TextView tv_agree;
		TextView tv_refuse;

		TextView tv_taskname;
		TextView tv_originator;
		TextView tv_remarks;
		TextView tv_amount;
		TextView tv_time;
		TextView tv_department;

		ImageView iv_select;
		TextView tv_agree_inside;
		TextView tv_reject_inside;

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
