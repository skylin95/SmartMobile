package com.pansoft.app.smartmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.Inflater;

/**
 * Created by eunji on 2016/8/25.
 */
public class TaskDetailAdapter {
    LinearLayout ll_task_detail;
    Context mContext;
    LayoutInflater mInflater;
    LinearLayout ll_block;
    static class ViewHolder{
    }

    public TaskDetailAdapter(Context mContext,LinearLayout ll_task_detail) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        View view  = mInflater.inflate(R.layout.activity_task_detail,null);
        this.ll_task_detail = ll_task_detail;
    }
    public void createView(JSONObject task) throws JSONException {
        View taskTitle = mInflater.inflate(R.layout.title_task_detail,null);
        if (task.getBoolean("showtitle")){
            TextView tv_titleName = (TextView) taskTitle.findViewById(R.id.tv_titleName);
            tv_titleName.setText(task.getString("title"));
        }
        if (task.getString("type").equals("mx_table")){
            taskTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (ll_block.getVisibility()){
                        case View.GONE:
                            ll_block.setVisibility(View.VISIBLE);
                            break;
                        case View.VISIBLE:
                            ll_block.setVisibility(View.GONE);
                            break;
                    }

                }
            });
        }

        ll_task_detail.addView(taskTitle);

        ll_block = new LinearLayout(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_block.setOrientation(LinearLayout.VERTICAL);
        ll_task_detail.addView(ll_block,lp);
        JSONArray detail = task.getJSONArray("keylist");
        JSONObject detailitme = null;
        int taskCount = detail.length();
        String title = "";
        String value = "";
        for (int i = 0; i < taskCount; i++){
            detailitme = detail.getJSONObject(i);

            title = detailitme.getString("title");
            value = detailitme.getString("value");

            View taskDetail = mInflater.inflate(R.layout.layout_task_detail,null);
            TextView tv_detailName = (TextView) taskDetail.findViewById(R.id.tv_detailName);

            TextView tv_content = (TextView) taskDetail.findViewById(R.id.tv_content);
            tv_detailName.setText(title);
            if (value != null && !"".equals(value)){
                tv_content.setText(value);
            }
            ll_block.addView(taskDetail);
        }
    }
}
