package com.pansoft.app.smartmobile.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Scroller;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.activity.LoginActivity;
import com.pansoft.app.smartmobile.activity.TaskDetailActivity;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.AnimationToast;
import com.pansoft.app.smartmobile.view.LoadingDialog;
import com.pansoft.app.smartmobile.view.ResultItemAdapter;
import com.pansoft.app.smartmobile.view.ResultItembean;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;


public class UnfinishedTaskFragment extends Fragment implements UltraRefreshListener,TaskItemListViewAdapter.ISingleProcess {
    private TaskItemListView mListView;
    private TaskItemListViewAdapter adapter;
    private List<TaskItembean> list = new ArrayList<TaskItembean>();
    private PtrClassicFrameLayout ptrFrame;
    private TextView tv_operation;
    private ImageView iv_select;
    private LinearLayout ll_op;
    private static final int  WROKOUT = 1;
    private static final int OPERATION = 0;
    private int type = OPERATION;
    private Scroller scroller;
    private PopupWindow popupWindow;
    private Button btn_agree;
    private Button btn_refuse;
    private View view;
    private LoadingDialog loadDialog;
    private int approveCount = 0;
    private int approveComplete = 0;
    private List<Integer> reslutSuccess = new ArrayList<Integer>();
    private List<ResultItembean> resultList = new ArrayList<ResultItembean>();
    ResultItemAdapter resultItemAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_task, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }


    private void initView(View view) {
        ll_op = (LinearLayout) view.findViewById(R.id.ll_op);
        tv_operation = (TextView) getParentFragment().getView().findViewById(R.id.tv_operation);
        iv_select = (ImageView) view.findViewById(R.id.iv_select);
        mListView = (TaskItemListView) view.findViewById(R.id.listview);
        mListView.initAddMore(true);
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
        scroller = new Scroller(mListView.getContext());

        btn_agree = (Button) view.findViewById(R.id.btn_agree);
        btn_refuse = (Button) view.findViewById(R.id.btn_refuse);
    }
    //下载数据 加载进list
    private void initData() {
        adapter = new TaskItemListViewAdapter(getActivity(), list);
        mListView.setAdapter(adapter);
        //添加list之后 adapter.notifyDataSetChanged() 刷新 listView
        try {
            String userId = SmartHttpClient.getInstance().getLoginUserId();
            loadWaittingTasks(userId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initEvent() {
        adapter.setOpVisibility(true);
        adapter.setSingleProcess(this);
        mListView.setOnItemClickListener(itemListener);
        tv_operation.setOnClickListener(operation);
        btn_agree.setOnClickListener(batchAgree);
        btn_refuse.setOnClickListener(batchRefuse);
        mListView.initSlideMode(0);
    }
    private View.OnClickListener batchAgree = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Set<Integer> positions = adapter.getSelectSet();
            approveCount = positions.size();
            try {
                loadDialog = new LoadingDialog(getContext());
                loadDialog.setMsg("审批提交中...");
                loadDialog.show();
                for (int position : positions)
                {
                    TaskItembean task = list.get(position);
                    task.setOptMsg("同意");
                    approve(task,0,position);
                    System.out.println("taskName:"+task.getpTaskName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private View.OnClickListener batchRefuse = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Set<Integer> positions = adapter.getSelectSet();
            showPopupWindow(positions,1);
        }
    };
    private View.OnClickListener operation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (type){
                case OPERATION:
                    if (mListView.isSlide()){
                        mListView.slideBack();
                    }
                    tv_operation.setText("完成");
                    type = WROKOUT;
                    /*mListView.initSlideMode(0);*/
                    mListView.scrollTo(mListView.getLeftPadding(),0);
                    ll_op.setVisibility(View.VISIBLE);
                    break;
                case WROKOUT:
                    tv_operation.setText("批量");
                    type = OPERATION;
                    /*mListView.initSlideMode(2);*/
                    mListView.scrollTo(0,0);
                    adapter.clearSelectState();
                    ll_op.setVisibility(View.GONE);
                    break;
                default:

                    break;
            }
        }
    };
    private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (type){
                case OPERATION:
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), TaskDetailActivity.class);
                    list.get(position).setVisibility(true);
                    intent.putExtra("TASK", list.get(position));
//                    intent.putExtra("F_DJLX",list.get(position).getDjlx());
//                    intent.putExtra("F_VCHR_ID", list.get(position).getVchrId());
//                    intent.putExtra("F_DJBH",list.get(position).getVchrKey());
//                    intent.putExtra("F_NODE_ID",list.get(position).getNodeId());

                    startActivity(intent);
                    break;
                case WROKOUT:
                    iv_select = (ImageView)view.findViewById(R.id.iv_select);
                    boolean isselected;
                    isselected = adapter.getSelectState(position);
                    if(isselected){
                        iv_select.setImageResource(R.drawable.noselected);
                        isselected = false;
                        adapter.deleteSelectState(position);
                    }else{
                        iv_select.setImageResource(R.drawable.selected);
                        isselected = true;
                        adapter.setSelectState(position,isselected);
                    }
                    break;
                default:

                    break;
            }

        }
    };
    @Override
    public void onRefresh() {
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = SmartHttpClient.getInstance().getLoginUserId();
                    loadWaittingTasks(userId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //刷新完成
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
   private void loadWaittingTasks(String userId, String sqlWhere) throws Exception {
       try {
           //校验session是否失效
           SessionValidator sessionValidator = new SessionValidator(null, getActivity());
           sessionValidator.start();
           sessionValidator.join();

           //ssion失效，跳转登录页面
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
           reqObj.put("method", "queryProcessTask");
           reqObj.put("F_USER_ID", userId);
           reqObj.put("PAGE_ROW", "1000");
           reqObj.put("ORDER_FIELD", "F_CRT_TIME");
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
        String userId = "";
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
            vchrKey = task.getString("F_DJBH");
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
    private boolean approve(final TaskItembean task, int mode, final int position) throws Exception {
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

                return false;
            }

            sessionValidator = null;

            String appPath = SmartMobileUtil.getAppPath(getActivity());
            String reqUrl = appPath + "mobileGeneralAction.do";

            Map<String, String> reqData = new HashMap<String, String>();
            JSONObject reqObj = new JSONObject();
            reqObj.put("service", "SmartMobileService");
            reqObj.put("method", (mode == 0)?"approveTask" : "rejectTask");
            reqObj.put("F_TASK_SN", task.getTaskSN());
            reqObj.put("F_FLOW_ID", task.getFlowId());
            reqObj.put("F_OPT_MSG", task.getOptMsg());

            if (mode == 0) {
                reqObj.put("F_REJECT_MODE", "2");
            }

            JSONArray vchrList = new JSONArray();
            JSONObject vchr = new JSONObject();
            vchr.put("VCHR_ID", task.getVchrId());
            vchr.put("VCHR_PK", task.getVchrKey());
            vchr.put("DRV_KEY", "");
            reqObj.put("VCHR_LIST", vchrList);

            reqData.put("jsondata", reqObj.toString());

                OnResponseListener listener = new OnResponseListener<JSONObject>() {
                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {
                    JSONObject rspData = response.get();
                    approveComplete++;
                    try {
                        //成功请求到数据
                        if (rspData != null && "0".equals(rspData.getString("F_CODE"))) {
                            Log.i("approveTask", task.getVchrKey() + "审批成功");
                            reslutSuccess.add(position);
                            if (approveComplete == approveCount){
                                loadDialog.dismiss();
                                showResult();
                                for (int p : reslutSuccess){
                                    list.remove(p);
                                    adapter.deleteSelectState(p);
                                }
                                adapter.notifyDataSetChanged();
                                reslutSuccess.clear();
                            }

                        } else {
                            ResultItembean result = new ResultItembean(task.getVchrKey(),rspData.getString("F_MESSAGE"));
                            resultList.add(result);
                            if (approveComplete == approveCount){
                                loadDialog.dismiss();
                                showResult();
                            }
                            throw new Exception(rspData.getString("F_MESSAGE"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                    ResultItembean result = new ResultItembean(task.getVchrKey(),"网络连接故障");
                    approveComplete++;
                    resultList.add(result);
                    if (approveComplete == approveCount){
                        loadDialog.dismiss();
                        showResult();
                    }
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
            Log.e("approveTask", "", e);
        }

        return false;
    }

    @Override
    public void approveTask(View view, int position) {
        /*showPopupWindow(positions,0);*/
        approveCount = 1;
        try {
            loadDialog = new LoadingDialog(getContext());
            loadDialog.setMsg("审批提交中...");
            loadDialog.show();
            TaskItembean task = list.get(position);
            task.setOptMsg("同意");
            approve(task,0,position);
            System.out.println("taskName:"+task.getpTaskName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void rejectTask(View view, int position) {
        Set<Integer> positions = new HashSet();
        positions.add(position);
        showPopupWindow(positions,1);
    }
    private void showPopupWindow(final Set<Integer> positions, final int mode) {

        approveCount = positions.size();
        if(approveCount==0){
            AnimationToast.makeText(
                    getActivity(),
                    "请选择至少一个任务",true,
                    AnimationToast.LENGTH_LONG,
                    getActivity().getWindow().getDecorView(), Gravity.CENTER
            ).show();
        }else{
            Button bt_submit;
            Button bt_cancel;
            LinearLayout ll_pop;
            final EditText et_approve;
            // 一个自定义的布局，作为显示的内容
            View contentView = LayoutInflater.from(getContext()).inflate(
                    R.layout.popupwindow_editview, null);
            ll_pop = (LinearLayout) contentView.findViewById(R.id.ll_pop);
            ll_pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            });
            et_approve = (EditText) contentView.findViewById(R.id.et_approve);
            if (mode == 0){
                et_approve.setText("同意");
            }else {
                et_approve.setText("退回");
            }

            // 设置按钮的点击事件
            bt_submit = (Button) contentView.findViewById(R.id.bt_submit);
            bt_submit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        loadDialog = new LoadingDialog(getContext());
                        loadDialog.setMsg("审批提交中...");
                        loadDialog.show();
                        for (int position : positions)
                        {
                            TaskItembean task = list.get(position);
                            task.setOptMsg(et_approve.getText().toString());
                            approve(task,mode,position);
                            System.out.println("taskName:"+task.getpTaskName());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    popupWindow.dismiss();
                }
            });
            bt_cancel = (Button) contentView.findViewById(R.id.bt_cancel);
            bt_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            popupWindow = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            popupWindow.setTouchable(true);
            popupWindow.setFocusable(true);
            // 设置好参数之后再show
            popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
        }
    }

    private void showResult(){
        adapter.clearSelectState();
        mListView.setAdapter(adapter);
        View contentView = LayoutInflater.from(getContext()).inflate(
                R.layout.popupwindow_task_approve, null);
        TextView tv_approveResult = (TextView) contentView.findViewById(R.id.tv_approveResult);
        ListView lv_approveResult = (ListView) contentView.findViewById(R.id.lv_approveResult);

        resultItemAdapter = new ResultItemAdapter(getContext(),resultList);
        if (approveCount == 1) {
            if (resultList.size() != 0){
                tv_approveResult.setText("审批失败");
                lv_approveResult.setAdapter(resultItemAdapter);
            }else{
                tv_approveResult.setText("审批成功");
            }
        }else{
            lv_approveResult.setAdapter(resultItemAdapter);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("成功审批");
            stringBuffer.append(approveCount-resultList.size());
            stringBuffer.append("个，失败");
            stringBuffer.append(resultList.size());
            stringBuffer.append("个");
            tv_approveResult.setText(stringBuffer.toString());
        }
        resultItemAdapter.notifyDataSetChanged();
        LinearLayout ll_pop;
        ll_pop = (LinearLayout) contentView.findViewById(R.id.ll_pop);
        ll_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                initResult();
            }
        });
        popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }
    public void initResult(){
        approveCount = 0;
        approveComplete = 0;
        resultList.clear();
        resultItemAdapter.notifyDataSetChanged();
    }
}
