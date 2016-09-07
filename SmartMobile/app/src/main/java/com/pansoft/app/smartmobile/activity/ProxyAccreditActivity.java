package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.AssignItemAdapter;
import com.pansoft.app.smartmobile.view.AssignItemBean;
import com.pansoft.app.smartmobile.view.ServiceItemAdapter;
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

public class ProxyAccreditActivity extends Activity implements UltraRefreshListener{
    private TaskItemListView mListView;
    private List<AssignItemBean> list = new ArrayList<AssignItemBean>();
    private AssignItemAdapter adapter;
    private PtrClassicFrameLayout ptrFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_accredit);
        initView();
        initData();
    }

    private void initData() {
        try {
            String userId = SmartHttpClient.getInstance().getLoginUserId();
            loadAssigns(userId, null);
        } catch (Exception e) {

        }
    }

    private void initView() {
        mListView = (TaskItemListView) findViewById(R.id.mListView);
        ptrFrame = (PtrClassicFrameLayout) findViewById(R.id.ultra_ptr);
        //下拉刷新的阻力，下拉时，下拉距离和显示头部的距离比例，值越大，则越不容易滑动
        ptrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrame.setDurationToClose(200);//返回到刷新的位置（暂未找到）
        ptrFrame.setDurationToCloseHeader(1000);//关闭头部的时间 // default is false
        ptrFrame.setPullToRefresh(false);//当下拉到一定距离时，自动刷新（true），显示释放以刷新（false）
        ptrFrame.setKeepHeaderWhenRefresh(true);//见名只意
        ptrFrame.setPtrHandler(mListView);

        //不再把新增代理授权放到最下边
        //mListView.addFooterView(LayoutInflater.from(this).inflate(R.layout.foot_assign_listview, null));

        //设置数据刷新回调接口
        mListView.setUltraRefreshListener(this);
        adapter = new AssignItemAdapter(this, LayoutInflater.from(this),list);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        try {
            String userId = SmartHttpClient.getInstance().getLoginUserId();
            loadAssigns(userId, null);
        } catch (Exception e) {

        }
    }

    @Override
    public void addMore() {

    }

    private void loadAssigns(String userId, String sqlWhere) throws Exception {
        try {
            //校验session是否失效
            SessionValidator sessionValidator = new SessionValidator(null, this);
            sessionValidator.start();
            sessionValidator.join();

            //session失效，跳转登录页面
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
            reqObj.put("method", "queryAssignInfo");
            reqObj.put("F_USER_ID", userId);

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
                        if (rspData != null && "0".equals(rspData.getString("RET_CODE"))) {
                            //首先清空遗留待办
                            list.clear();

                            fillAssignList(rspData.getJSONArray("ASSIGN_LIST"));

                            //刷新完成
                            mListView.refreshComplete();
                            adapter.notifyDataSetChanged();
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

    private void fillAssignList(JSONArray assignList) throws Exception {
        int taskCount = assignList.length();

        JSONObject assignObj = null;
        AssignItemBean assignItem = null;
        String sn = "";
        String flowId = "";
        String flowName = "";
        String wtrId = "";
        String wtrName = "";
        String wtrBmbh = "";
        String wtrBmmc = "";
        String strId = "";
        String strName = "";
        String strBmbh = "";
        String strBmmc = "";
        String startTime = "";
        String endTime = "";
        boolean disable = false;
        for (int iIndex = 0; iIndex < taskCount; iIndex++) {
            assignObj = assignList.getJSONObject(iIndex);

            sn = assignObj.getString("F_SN");
            wtrId = assignObj.getString("F_WTR_ID");
            flowId = assignObj.getString("F_FLOW_ID");
            flowName = assignObj.getString("F_FLOW_NAME");
            wtrName = assignObj.getString("F_WTR_NAME");
            wtrBmbh = assignObj.getString("F_WTR_BMBH");
            wtrBmmc = assignObj.getString("F_WTR_BMMC");
            strId = assignObj.getString("F_STR_ID");
            strName = assignObj.getString("F_STR_NAME");
            strBmbh = assignObj.getString("F_STR_BMBH");
            strBmmc = assignObj.getString("F_STR_BMMC");
            startTime = SmartMobileUtil.getFormatTime(assignObj.getString("F_START_TIME"), null, null);
            endTime = SmartMobileUtil.getFormatTime(assignObj.getString("F_END_TIME"), null, null);
            disable = "1".equals(assignObj.getString("F_DISABLE"));

            assignItem = new AssignItemBean();
            assignItem.setSn(sn);
            assignItem.setFlowId(flowId);
            assignItem.setFlowName(flowName);
            assignItem.setWtrId(wtrId);
            assignItem.setWtrName(wtrName);
            assignItem.setWtrBmbh(wtrBmbh);
            assignItem.setWtrBmmc(wtrBmmc);
            assignItem.setStrId(strId);
            assignItem.setStrName(strName);
            assignItem.setStrBmbh(strBmbh);
            assignItem.setStrBmmc(strBmmc);
            assignItem.setStartTime(startTime);
            assignItem.setEndTime(endTime);
            assignItem.setDiable(disable);

            list.add(assignItem);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_assign :
                Intent assignDetailIntent = new Intent();
                assignDetailIntent.setClass(this, AssignDetailActivity.class);
                startActivity(assignDetailIntent);

                break;
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
        }
    }
}
