package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.loader.BaseImageInfo;
import com.pansoft.app.smartmobile.loader.ImageInfo;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.ImageItemAdapter;
import com.pansoft.app.smartmobile.view.ImageItemBean;
import com.pansoft.app.smartmobile.view.LoadingDialog;
import com.pansoft.app.smartmobile.view.NoScrollViewPager;
import com.pansoft.app.smartmobile.view.TaskPagerAdapter;
import com.pansoft.app.smartmobile.view.TaskItembean;
import com.pansoft.app.smartmobile.view.TaskNodeItemAdapter;
import com.pansoft.app.smartmobile.view.TaskNodeItembean;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDetailActivity extends Activity{
    /* 经办人*/
    private TextView tv_originator;
    /* 金额*/
    private TextView tv_amount;
    /* 部门*/
    private TextView tv_department;
    /* 事由*/
    private TextView tv_reason;
    /* 备注*/
    private TextView tv_remarks;
    /* 单据*/
    private TextView tv_taskname;
    /* 时间*/
    private TextView tv_time;
    private ImageView iv_proccess;
    private ImageView iv_image;
    private List<TaskNodeItembean> nodeList = new ArrayList<TaskNodeItembean>();
    private List<ImageItemBean> imageList = new ArrayList<ImageItemBean>();
    private TaskPagerAdapter pagerAdapter;
    private TaskNodeItemAdapter nodeAdapter;
    private ImageItemAdapter imageAdapter;
    private ListView nodeListView;
    private ListView imageListView;
    private PopupWindow popupWindow;
    private View nodeView;
    private View imageView;
    private LoadingDialog loadDialog;
    private LinearLayout ll_op;
    private NoScrollViewPager viewpager;
    private List<View> viewList = new ArrayList<View>();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        //影像点击事件，触发后显示真是影像图片
        imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItemBean imageItem = imageList.get(position);

                ImageInfo imageInfo = new BaseImageInfo(imageItem.getImageKey(), imageItem.getImageName(), "", imageItem.getImageUrl(), ImageInfo.IMAGE_PURPOSE_NORMAL);
                Intent imageIntent = new Intent();
                imageIntent.putExtra("image", imageInfo);
                imageIntent.setClass(TaskDetailActivity.this, ImageShowActivity.class);
                startActivity(imageIntent);
            }
        });
    }

    private void initData() {
        try {
            Intent dataIntent = getIntent();
            TaskItembean task = (TaskItembean)dataIntent.getSerializableExtra("TASK");

            if(task.getVisibility()){
                ll_op.setVisibility(View.VISIBLE);
            }else{
                ll_op.setVisibility(View.GONE);
            }

            String userId = SmartHttpClient.getInstance().getLoginUserId();
            String djlx = task.getDjlx();
            String djbh = task.getVchrKey();
            String djmx = task.getVchrId();
            String appPath = SmartMobileUtil.getAppPath(this);
            String attType = "";
            String nodeId = task.getNodeId();

            loadTaskDetail(userId, djlx, djbh, djmx, appPath, attType, nodeId);
        } catch (Exception e) {

        }
    }

    private void initView() {

        tv_originator = (TextView) findViewById(R.id.tv_originator);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        tv_department = (TextView) findViewById(R.id.tv_department);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        tv_remarks = (TextView) findViewById(R.id.tv_remarks);
        tv_taskname = (TextView) findViewById(R.id.tv_taskname);
        tv_time = (TextView) findViewById(R.id.tv_time);

        iv_proccess = (ImageView) findViewById(R.id.iv_process);
        iv_image = (ImageView) findViewById(R.id.iv_image);

        ll_op = (LinearLayout) findViewById(R.id.ll_op);
        LayoutInflater inflater=getLayoutInflater();

        //审批流程
        nodeView = inflater.inflate(R.layout.listview_task_detail,null);
        nodeListView = (ListView) nodeView.findViewById(R.id.lv_taskDetail);
        nodeAdapter = new TaskNodeItemAdapter(this, LayoutInflater.from(this),nodeList);
        nodeListView.setAdapter(nodeAdapter);

        //单据影像
        imageView = inflater.inflate(R.layout.listview_task_detail,null);
        imageListView = (ListView) imageView.findViewById(R.id.lv_taskDetail);
        imageAdapter = new ImageItemAdapter(this,LayoutInflater.from(this),imageList);
        imageListView.setAdapter(imageAdapter);


        viewpager = (NoScrollViewPager) findViewById(R.id.viewpager);
        viewList.add(nodeView);
        viewList.add(imageView);
        pagerAdapter = new TaskPagerAdapter(viewList);
        viewpager.setAdapter(pagerAdapter);

    }



    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_approve_agree :
                break;
            case R.id.btn_approve_reject :
                break;
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
            case R.id.tv_process:
                iv_proccess.setImageResource(R.drawable.task_detail_checked);
                iv_image.setImageResource(R.drawable.task_detail_nochecked);
                viewpager.setCurrentItem(0);
                break;
            case R.id.tv_image:
                iv_proccess.setImageResource(R.drawable.task_detail_nochecked);
                iv_image.setImageResource(R.drawable.task_detail_checked);
                viewpager.setCurrentItem(1);
                break;
            default :
                break;
        }
    }

    private void approveTask(int mode) {
        Button bt_submit;
        Button bt_cancel;
        LinearLayout ll_pop;
        final EditText et_approve;
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.popupwindow_editview, null);
        ll_pop = (LinearLayout) contentView.findViewById(R.id.ll_pop);
        ll_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        TaskDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    loadDialog = new LoadingDialog(TaskDetailActivity.this);
                    loadDialog.setMsg("审批提交中...");
                    loadDialog.show();


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
//        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }

    private void loadTaskDetail(String userId, String djlx, String djbh, String djmx, String appPath, String attType, String nodeId) throws Exception {
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

            String reqUrl = appPath + "mobileGeneralAction.do";

            Map<String, String> reqData = new HashMap<String, String>();
            JSONObject reqObj = new JSONObject();
            reqObj.put("service", "SmartMobileService");
            reqObj.put("method", "queryTaskDetail");
            reqObj.put("F_DJLX", djlx);
            reqObj.put("F_DJBH", djbh);
            reqObj.put("F_DJMX", djmx);    //单据类型传空，表示不加载审批流程(加载时间太长)
            reqObj.put("F_CONTEXT", appPath);
            reqObj.put("F_ATT_TYPE", attType);
            reqObj.put("F_NODE_ID", nodeId);

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
                            JSONObject vchr = rspData.getJSONObject("TASK_DETAIL");
                            fillVchrDetail(vchr);

                            JSONArray hisList = rspData.getJSONArray("HIS_LIST");
                            fillHisDetail(hisList);

                            JSONArray imgList = rspData.getJSONArray("ATT_LIST");
                            fillImgDetail(imgList);
                        } else {
                            throw new Exception(rspData.getString("RET_MSG"));
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
            Log.e("LoadTaskDetail", "", e);
        }
    }

    private void fillVchrDetail(JSONObject vchr) {
        try {
            tv_originator.setText(vchr.getString("F_ZDR_MC"));
            tv_amount.setText(vchr.getString("F_JE"));
            tv_department.setText(vchr.getString("F_ZDBM_MC"));
            tv_reason.setText(vchr.getString("F_SY"));
//            tv_remarks.setText(vchr.getString(""));
            tv_taskname.setText(vchr.getString("F_DJLX_MC"));
            tv_time.setText(vchr.getString("F_ZDSJ"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillHisDetail(JSONArray hisList) {
        try {
            if (hisList == null || hisList.length() == 0) {
                return;
            }

            nodeList.clear();

            TaskNodeItembean nodeItem = null;
            JSONObject approve = null;
            String optUID = "";
            String optUser = "";
            String optMsg = "";
            String optTime = "";
            String nodeName = "";
            int nodeState = 0;
            for (int i = 0; i < hisList.length(); i++) {
                approve = hisList.getJSONObject(i);

                nodeName = approve.getString("F_NODE_NAME");
                optUser = approve.getString("F_OPT_UNAME");
                optUID = approve.getString("F_OPT_UID");
                optMsg = approve.getString("F_OPT_MSG");
                optTime = approve.getString("F_OPT_TIME");
                optTime = (optTime == null || "".equals(optTime)) ? "" : SmartMobileUtil.getFormatTime(optTime.substring(0, 14), null, "dd/MM\nHH:mm");
                nodeState = Integer.parseInt(approve.getString("F_NODE_TYPE"));

                nodeItem = new TaskNodeItembean(nodeName + optUser, optMsg, optTime, nodeState, optUID);
                nodeList.add(nodeItem);
            }

            nodeAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillImgDetail(JSONArray attaches) {
        if (attaches == null || attaches.length() == 0) {
            return;
        }

        try {
            imageList.clear();

            ImageItemBean nodeItem = null;
            JSONObject attach = null;
            String imageUrl = "";
            String imageName = "";
            String imageSize = "";
            String imageTime = "";
            String stoKey = "";
            for (int i = 0; i < attaches.length(); i++) {
                attach = attaches.getJSONObject(i);

                imageUrl = attach.getString("F_DOWNLOAD_URL");
                imageName = attach.getString("F_ATT_TITLE");
                imageSize = attach.getString("F_ATT_SIZE");
                imageTime = attach.getString("F_UP_TIME");
                stoKey = attach.getString("F_ATT_STO_KEY");

                nodeItem = new ImageItemBean(imageUrl, imageName, imageSize, imageTime, stoKey);
                imageList.add(nodeItem);
            }

            imageAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
