package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.MyTaskItemListViewAdapter;
import com.pansoft.app.smartmobile.view.TaskItemListView;
import com.pansoft.app.smartmobile.view.TaskItembean;
import com.pansoft.app.smartmobile.view.UltraRefreshListener;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

public class MyDocumentActivity extends Activity implements UltraRefreshListener{
    private TaskItemListView mListView;
    private MyTaskItemListViewAdapter adapter;
    private List<TaskItembean> list = new ArrayList<TaskItembean>();
    private PtrClassicFrameLayout ptrFrame;
    private LinearLayout ll_op;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_document);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MyDocumentActivity.this, TaskDetailActivity.class);
                list.get(position).setVisibility(false);
                intent.putExtra("TASK", list.get(position));
//                intent.putExtra("F_DJLX",list.get(position).getDjlx());
//                intent.putExtra("F_VCHR_ID", list.get(position).getVchrId());
//                intent.putExtra("F_DJBH",list.get(position).getVchrKey());
//                intent.putExtra("F_NODE_ID",list.get(position).getNodeId());

                startActivity(intent);
            }
        });
    }

    private void initData() {
        adapter = new MyTaskItemListViewAdapter(this, list);
        adapter.setOpVisibility(false);
        mListView.setAdapter(adapter);
        try {
            String userId = SmartHttpClient.getInstance().getLoginUserId();
            loadMyTasks(userId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mListView = (TaskItemListView) findViewById(R.id.listview);
        mListView.initAddMore(false);
        mListView.initSlideMode(2);
        ptrFrame = ((PtrClassicFrameLayout) findViewById(R.id.ultra_ptr));
        ptrFrame.setLastUpdateTimeRelateObject(this);
        //下拉刷新的阻力，下拉时，下拉距离和显示头部的距离比例，值越大，则越不容易滑动
        ptrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrame.setDurationToClose(200);//返回到刷新的位置（暂未找到）
        ptrFrame.setDurationToCloseHeader(1000);//关闭头部的时间 // default is false
        ptrFrame.setPullToRefresh(false);//当下拉到一定距离时，自动刷新（true），显示释放以刷新（false）
        ptrFrame.setKeepHeaderWhenRefresh(true);//见名只意
        ptrFrame.setPtrHandler(mListView);
        //设置数据刷新回调接口
        mListView.setUltraRefreshListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
            default:
                break;
        }
    }
    private void loadMyTasks(String userId, String sqlWhere) throws Exception {
        try {
            //校验session是否失效
            SessionValidator sessionValidator = new SessionValidator(null, this);
            sessionValidator.start();
            sessionValidator.join();

            //ssion失效，跳转登录页面
            if (!sessionValidator.isValidate()) {
                sessionValidator = null;

                Intent loginIntent = new Intent();
                loginIntent.setClass(this, LoginActivity.class);
                startActivity(loginIntent);

                return;
            }

            sessionValidator = null;


            String appPath = SmartMobileUtil.getAppPath(this);
            String reqUrl = appPath + "mobileGeneralAction.do";

            Map<String, String> reqData = new HashMap<String, String>();
            JSONObject reqObj = new JSONObject();
            reqObj.put("service", "SmartMobileService");
            reqObj.put("method", "queryMyVchr");
            reqObj.put("F_USER_ID", userId);
            reqObj.put("PAGE_ROW", "1000");
            reqObj.put("ORDER_FIELD", "F_ZDSJ");
            reqObj.put("F_FILTER", sqlWhere == null ? "" : sqlWhere);
            reqObj.put("ORDER_DESC", "1");

            reqData.put("jsondata", reqObj.toString());

            OnResponseListener listener = new OnResponseListener<JSONObject>() {
                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {
                    JSONObject rspData = response.get();

                    try {
                        //成功请求到数据
                        if (rspData != null) {
                            //首先清空遗留待办
                            list.clear();

                            fillTaskList(rspData.getJSONArray("TASK_GROUPS"));
                            adapter.notifyDataSetChanged();
                            mListView.refreshComplete();
                        } else {
                            throw new Exception(rspData.getString("F_MESSAGE"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }

                @Override
                public void onFinish(int what) {

                }
            };

            SmartHttpClient.getInstance().sendRequest(30001,
                    SmartHttpConstant.HTTP_REQUEST_JSONOBJECT,
                    SmartHttpConstant.HTTP_METHOD_POST,
                    reqUrl,
                    reqData,
                    null,
                    listener);

        } catch (Exception e) {
            Log.e("LoadTask", "", e);
        }
    }

    private void fillTaskList(JSONArray groupList) throws Exception {
        JSONObject taskObj = groupList.getJSONObject(0);
        JSONArray taskList = taskObj.getJSONArray("TASK_LIST");

        int taskCount = taskList.length();

        JSONObject task = null;
        TaskItembean taskItem = null;
        String userId = "";
        String djlx = "";
        String vchrId = "";
        String vchrKey = "";
        String djmc = "";
        String je = "0.0";
        String user = "";
        String dept = "";
        String note = "";
        String time = "";

        for (int iIndex = 0; iIndex < taskCount; iIndex++) {
            task = taskList.getJSONObject(iIndex);

            userId = task.getString("F_BASE_USER_ID");
            djlx = task.getString("F_DJLX");
            vchrId = task.getString("F_DJMX");
            vchrKey = task.getString("F_DJBH");
            djmc = task.getString("F_DJLX_MC");
            je = task.getString("F_JE");
            user = task.getString("F_BASE_USER_NAME");
            dept = task.getString("F_BASE_ORG_CAPTION");
            note = task.getString("F_BIZ_INFO");
            time = SmartMobileUtil.getFormatTime(task.getString("F_ZDSJ"), null, "yyyy-MM-dd");

            taskItem = new TaskItembean(djmc, user, note, je, time, dept);
            taskItem.setSubmitor(userId);
            taskItem.setDjlx(djlx);
            taskItem.setVchrId(vchrId);
            taskItem.setVchrKey(vchrKey);

            list.add(taskItem);
        }
    }

    @Override
    public void onRefresh() {
        try {
            String userId = SmartMobileUtil.getLoginUser(this);
            loadMyTasks(userId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addMore() {

    }
}
