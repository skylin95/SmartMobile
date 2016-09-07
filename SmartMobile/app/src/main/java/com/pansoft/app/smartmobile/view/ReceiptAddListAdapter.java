package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eunji on 2016/9/7.
 */
public class ReceiptAddListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<String> groupList = new ArrayList<String>();
    private final int APPLY_MSG = 0;
    private final int APPLY_PERSON = 1;
    private final int APPLY_TRIP = 2;

    public ReceiptAddListAdapter(Context mContext) {
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        groupList.add("申请信息");
        groupList.add("新增人员");
        groupList.add("新增行程");
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public String getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int type = getItemViewType(groupPosition);
        if (convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_group_add_receipt, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_add = (ImageView) convertView.findViewById(R.id.iv_add);
            viewHolder.tv_groupName = (TextView) convertView.findViewById(R.id.tv_groupName);
            viewHolder.iv_more = (ImageView) convertView.findViewById(R.id.iv_more);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_groupName.setText(getGroup(groupPosition));

        switch (type){
            case APPLY_MSG:
                viewHolder.iv_add.setVisibility(View.GONE);
                break;
            case APPLY_PERSON:
                break;
            case APPLY_TRIP:
                break;
        }

        return convertView;
    }

    private int getItemViewType(int groupPosition) {
        int p = groupPosition;
        if (p == 0) {
            return APPLY_MSG;
        } else if (p == 1) {
            return APPLY_PERSON;
        } else if (p == 2) {
            return APPLY_TRIP;
        } else {
            return APPLY_PERSON;
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
    static class ViewHolder{
        ImageView iv_add;
        TextView tv_groupName;
        ImageView iv_more;
    }
}
