package com.pansoft.app.smartmobile.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.activity.LoginActivity;
import com.pansoft.app.smartmobile.activity.TaskDetailActivity;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.db.DatabaseHelper;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.SearchPopupWindow;
import com.pansoft.app.smartmobile.view.TaskItemListView;
import com.pansoft.app.smartmobile.view.TaskItemListViewAdapter;
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


public class CompleteTaskFragment extends Fragment implements UltraRefreshListener {

    private RelativeLayout rl_search;
    private TaskItemListView mListView;
    private TaskItemListViewAdapter adapter;
    private List<TaskItembean> list = new ArrayList<TaskItembean>();
    private PtrClassicFrameLayout ptrFrame;
    private LinearLayout ll_op;
    private View view;
    private SearchPopupWindow searchPopupWindow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_task_com, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }


    private void initView(View view) {
        searchPopupWindow = new SearchPopupWindow(getActivity(),view);
        rl_search = (RelativeLayout) view.findViewById(R.id.rl_search);
        mListView = (TaskItemListView) view.findViewById(R.id.listview);
        ptrFrame = ((PtrClassicFrameLayout) view.findViewById(R.id.ultra_ptr));
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
    //下载数据 加载进list
    private void initData() {

        try {
            String userId = SmartHttpClient.getInstance().getLoginUserId();
            loadProcessedTasks(userId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initEvent() {
        adapter = new TaskItemListViewAdapter(getActivity(), list);
        adapter.setOpVisibility(false);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), TaskDetailActivity.class);
                list.get(position).setVisibility(false);
                intent.putExtra("TASK", list.get(position));
//                intent.putExtra("F_DJLX",list.get(position).getDjlx());
//                intent.putExtra("F_VCHR_ID", list.get(position).getVchrId());
//                intent.putExtra("F_DJBH",list.get(position).getVchrKey());
//                intent.putExtra("F_NODE_ID",list.get(position).getNodeId());

                startActivity(intent);
            }
        });
        mListView.initSlideMode(0);

        rl_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPopupWindow.show(Gravity.CENTER);
            }
        });
    }

    @Override
    public void onRefresh() {
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = SmartHttpClient.getInstance().getLoginUserId();
                    loadProcessedTasks(userId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },1000);
    }

    @Override
    public void addMore() {
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {

                int count = adapter.getCount();
                for(int i = count; i< count +10; i++){
                    TaskItembean taskItembean = null;
                    taskItembean = new TaskItembean("add",String.valueOf(i),"1","1","1","1");
                    list.add(taskItembean);

                }
                adapter.notifyDataSetChanged();
                //刷新完成
                mListView.refreshComplete();
            }
        },1000);
    }
   /* //重写setMenuVisibility方法，不然会出现叠层的现象
    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }*/
   private void loadProcessedTasks(String userId, String sqlWhere) throws Exception {
       try {
           //校验session是否失效
           SessionValidator sessionValidator = new SessionValidator(null, getActivity());
           sessionValidator.start();
           sessionValidator.join();

           //session失效，跳转登录页面
           if (!sessionValidator.isValidate()) {
               sessionValidator = null;

               Intent loginIntent = new Intent();
               loginIntent.setClass(getActivity(), LoginActivity.class);
               startActivity(loginIntent);

               return;
           }

           sessionValidator = null;

           String appPath = SmartMobileUtil.getAppPath(getActivity());
           String reqUrl = appPath + "mobileGeneralAction.do";

           Map<String, String> reqData = new HashMap<String, String>();
           JSONObject reqObj = new JSONObject();
           reqObj.put("service", "SmartMobileService");
           reqObj.put("method", "queryProcessedTask");
           reqObj.put("F_USER_ID", userId);
           reqObj.put("PAGE_ROW", "1000");
           reqObj.put("ORDER_FIELD", "F_OPT_DAY");
           reqObj.put("F_FILTER", sqlWhere == null ? "" : sqlWhere);
           reqObj.put("ORDER_DESC", "1");
           reqObj.put("START_TIME", "");
           reqObj.put("END_TIME", "");

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

    private void fillTaskList(JSONArray groupList) throws Exception {
        JSONObject taskObj = groupList.getJSONObject(0);
        JSONArray taskList = taskObj.getJSONArray("TASK_LIST");

        int taskCount = taskList.length();

        JSONObject task = null;
        TaskItembean taskItem = null;
        String userId = "";
        String tasnSN = "";
        String flowId = "";
        String flowSN = "";
        String nodeId = "";
        String nodeName = "";
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
            tasnSN = task.getString("F_TASK_SN");
            flowId = task.getString("F_FLOW_ID");
            flowSN = task.getString("F_FLOW_SN");
            nodeId = task.getString("F_NODE_ID");
            nodeName = task.getString("F_NODE_CAPTION");
            djlx = task.getString("F_DJLX");
            vchrId = task.getString("F_DJMX");
            vchrKey = task.getString("F_MASTER_DRV_OBJECT");
            djmc = task.getString("F_DJLX_MC");
            je = task.getString("F_JE");
            user = task.getString("F_BASE_USER_NAME");
            dept = task.getString("F_BASE_ORG_CAPTION");
            note = task.getString("F_BIZ_INFO");
            time = SmartMobileUtil.getFormatTime(task.getString("F_ZDSJ"), null, "yyyy-MM-dd");

            taskItem = new TaskItembean(djmc, user, note, je, time, dept);
            taskItem.setSubmitor(userId);
            taskItem.setTaskSN(tasnSN);
            taskItem.setFlowId(flowId);
            taskItem.setFlowSN(flowSN);
            taskItem.setNodeId(nodeId);
            taskItem.setNodeName(nodeName);
            taskItem.setDjlx(djlx);
            taskItem.setVchrId(vchrId);
            taskItem.setVchrKey(vchrKey);

            list.add(taskItem);
        }
    }
}
