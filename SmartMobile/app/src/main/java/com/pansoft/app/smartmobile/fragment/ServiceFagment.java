package com.pansoft.app.smartmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.activity.LoginActivity;
import com.pansoft.app.smartmobile.activity.ServiceActivity;
import com.pansoft.app.smartmobile.constant.SmartHttpConstant;
import com.pansoft.app.smartmobile.http.SessionValidator;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.FixedSpeedScroller;
import com.pansoft.app.smartmobile.view.ServiceItemAdapter;
import com.pansoft.app.smartmobile.view.ServiceItembean;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by linlin on 2016/8/9.
 */
public class ServiceFagment extends Fragment{
    private ServiceItemAdapter serviceItemAdapter;
    private List<ServiceItembean> serviceList = new ArrayList<ServiceItembean>();
    private GridView gridView;
    private ImageView glideView;
    private ImageView[] glideViews = null;
    private boolean isContinue = true;
    private ViewPager top_pager;
    private ViewGroup viewGroup;
    FixedSpeedScroller mScroller = null;
    private AtomicInteger what = new AtomicInteger(0);
    private List<View> topImage = new ArrayList<View>();
    private Thread thread;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_service, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }


    private void initEvent() {
        initTopPageEvent();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceItembean service = serviceList.get(position);

                Intent serviceIntent = new Intent();
                serviceIntent.setClass(getActivity(), ServiceActivity.class);
                serviceIntent.putExtra("service", service);
//                serviceIntent.putExtra("serviceUrl", service.getServiceUrl());
                startActivity(serviceIntent);
            }
        });

    }

    private void initTopPageEvent() {
        top_pager.setOnPageChangeListener(new GuidePageChangeListener());
        controlViewPagerSpeed(top_pager);
        top_pager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    if (isContinue) {
                        viewHandler.sendEmptyMessage(what.get());
                        whatOption();
                    }
                }
            }
        });
        thread.start();
    }

    /*获取服务列表*/
    private void initData() {
        for (int i = 0; i<3; i++){
            ImageView img = new ImageView(getActivity());
            img.setBackgroundResource(R.drawable.login_logo);
            topImage.add(img);
        }
        glideViews = new ImageView[topImage.size()];
        for (int i = 0; i < topImage.size(); i++) {
            glideView = new ImageView(getActivity());
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(20, 20);
            layout.setMargins(5,0,5,0);
            glideView.setLayoutParams(layout);
            glideView.setPadding(5, 5, 5, 5);
            glideViews[i] = glideView;
            if (i == 0) {
                glideViews[i]
                        .setBackgroundResource(R.drawable.shape_dot_focus);
            } else {
                glideViews[i]
                        .setBackgroundResource(R.drawable.shape_dot_blur);
            }
            viewGroup.addView(glideViews[i]);
        }
        top_pager.setAdapter(new TopImageAdapter(topImage));

        /**
         * 加载服务
         */
        try {
            loadServices(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView(View view) {
        top_pager = (ViewPager) view.findViewById(R.id.top_pager);
        viewGroup = (ViewGroup) view.findViewById(R.id.viewGroup);
        gridView = (GridView) view.findViewById(R.id.gridView);
        serviceItemAdapter = new ServiceItemAdapter(this,LayoutInflater.from(getActivity()),serviceList);
        gridView.setAdapter(serviceItemAdapter);
    }

    //重写setMenuVisibility方法，不然会出现叠层的现象
    @Override
    public void setMenuVisibility(boolean menuVisibile) {
        super.setMenuVisibility(menuVisibile);
        if (this.getView() != null) {
            this.getView().setVisibility(menuVisibile ? View.VISIBLE : View.GONE);
        }
    }
    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > glideViews.length - 1) {
            what.getAndAdd(-glideViews.length);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }
    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            top_pager.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }

    };
    private void controlViewPagerSpeed(ViewPager viewPager) {
        try {
            Field mField;

            mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);

            mScroller = new FixedSpeedScroller(
                    viewPager.getContext(),
                    new AccelerateInterpolator());
            mScroller.setmDuration(500); // 2000ms
            mField.set(viewPager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            what.getAndSet(arg0);
            for (int i = 0; i < glideViews.length; i++) {
                glideViews[arg0]
                        .setBackgroundResource(R.drawable.shape_dot_focus);
                if (arg0 != i) {
                    glideViews[i]
                            .setBackgroundResource(R.drawable.shape_dot_blur);
                }
            }

        }

    }
    private final class TopImageAdapter extends PagerAdapter {
        private List<View> views = null;

        public TopImageAdapter(List<View> views) {
            this.views = views;
        }


        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(views.get(position), 0);
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

    }

    private void loadServices(String userId, String sqlWhere) throws Exception {
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
            reqObj.put("method", "queryServiceModule");
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
                            serviceList.clear();

                            fillServiceList(rspData.getJSONArray("SERVICE_LIST"));
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
            Log.e("LoadTask", "", e);
        }
    }

    private void fillServiceList(JSONArray services) throws Exception {
        String appPath = SmartMobileUtil.getAppPath(getActivity());
        String imageFolder = appPath + "mobile/images/";
        int taskCount = services.length();

        JSONObject service = null;
        ServiceItembean serviceItem = null;
        String serviceImage = "";
        String serviceId = "";
        String serviceName = "";
        String serviceMsg = "";
        String servcieUrl = "";
        for (int iIndex = 0; iIndex < taskCount; iIndex++) {
            service = services.getJSONObject(iIndex);

            serviceImage = service.getString("F_SERVICE_ICON");
            serviceId = service.getString("F_SERVICE_ID");
            serviceName = service.getString("F_SERVICE_NAME");
            serviceMsg = service.getString("F_SERVICE_DESC");
            servcieUrl = service.getString("F_SERVICE_URL");

            serviceItem = new ServiceItembean(imageFolder + serviceImage, serviceId, serviceName, serviceMsg, servcieUrl);
            this.serviceList.add(serviceItem);
        }
    }
}
