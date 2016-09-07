package com.pansoft.app.smartmobile.view;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pansoft.app.smartmobile.R;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by eunji on 2016/8/31.
 */
public class SearchPopupWindow {
    private Context mContext;
    private View mView;
    private PopupWindow popupWindow;
    private TextView tv_djlx;
    private TextView tv_startTime;
    private TextView tv_endTime;

    private LinearLayout ll_startTime;
    private LinearLayout ll_endTime;

    private Button bt_submit;
    private Button bt_cancel;

    public SearchPopupWindow(Context mContext, View mView) {
        this.mContext = mContext;
        this.mView = mView;
        initView();
        initDate();
        initEvent();
    }
    private void initView() {
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.popupwindow_search, null);
        tv_djlx = (TextView) contentView.findViewById(R.id.tv_djlx);
        tv_startTime = (TextView) contentView.findViewById(R.id.tv_startTime);
        tv_endTime = (TextView) contentView.findViewById(R.id.tv_endTime);
        ll_startTime = (LinearLayout) contentView.findViewById(R.id.ll_startTime);
        ll_endTime = (LinearLayout) contentView.findViewById(R.id.ll_endTime);
        bt_submit = (Button) contentView.findViewById(R.id.bt_submit);
        bt_cancel = (Button) contentView.findViewById(R.id.bt_cancel);

        popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
    }
    public void show(int gravity) {
        popupWindow.showAtLocation(mView,gravity,0,0);
    }
    private void initDate() {

    }


    private void initEvent() {
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        tv_djlx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDjlxView();
                initDjlxData();
                initDjlxEvent();
                showDjlxList();
            }
        });
        ll_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    private ImageView iv_select;
    private PopupWindow djlxList;
    private DjlxItemAdapter adapter;
    private List<String> list = new ArrayList<String>();
    private LinearLayout ll_djlx;
    private List<String> selectedList = new ArrayList<String>();
    private ListView lv_djlx;
    private Button bt_djlx;
    private Set<Integer> positions = new HashSet<Integer>();
    private void initDjlxView() {
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.popupwindow_djlx, null);
        ll_djlx = (LinearLayout) contentView.findViewById(R.id.ll_djlx);
        lv_djlx = (ListView) contentView.findViewById(R.id.lv_djlx);
        bt_djlx = (Button) contentView.findViewById(R.id.bt_djlx);
        djlxList = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        djlxList.setTouchable(true);
        djlxList.setFocusable(true);
    }

    private void initDjlxData() {
        for (int i = 0;i<10;i++){
            list.add(" "+i);
        }
        adapter = new DjlxItemAdapter(mContext,list);
        if (positions.size() != 0){
            for (int p : positions){
                adapter.setSelectState(p,true);
            }
        }
        lv_djlx.setAdapter(adapter);
    }

    private void initDjlxEvent() {
        ll_djlx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                djlxList.dismiss();
                list.clear();
            }
        });
        bt_djlx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positions =  adapter.getSelectSet();

                if (positions.size() != 0) {
                    for (int position : positions) {
                        selectedList.add(list.get(position));
                    }
                }
                djlxList.dismiss();
                list.clear();
            }
        });
        lv_djlx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iv_select = (ImageView)view.findViewById(R.id.iv_select);
                boolean isselected;
                isselected = adapter.getSelectState(position);
                if(isselected){
                    iv_select.setVisibility(View.GONE);
                    isselected = false;
                    adapter.deleteSelectState(position);
                }else{
                    iv_select.setVisibility(View.VISIBLE);
                    isselected = true;
                    adapter.setSelectState(position,isselected);
                }
            }
        });
    }
    private void showDjlxList() {

        djlxList.showAtLocation(mView,Gravity.BOTTOM,0,0);
    }
}
