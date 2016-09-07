package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.http.SmartHttpClient;
import com.pansoft.app.smartmobile.util.SmartMobileUtil;
import com.pansoft.app.smartmobile.view.ServiceItembean;

import java.net.HttpCookie;

public class ServiceActivity extends Activity {
    private TextView serviceTitle;
    private WebView serviceDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        initView();
        initData();
    }

    private void initView() {
        serviceDispatcher = (WebView) findViewById(R.id.service_dispatcher);
        serviceDispatcher.getSettings().setJavaScriptEnabled(true);

        serviceDispatcher.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                webView.loadUrl(url);

                return true;
            }
        });

        serviceTitle = (TextView) findViewById(R.id.tv_service_title);
    }

    private void initData() {
        ServiceItembean service = (ServiceItembean) getIntent().getSerializableExtra("service");

        //如果是内部应用，则同步cookie；暂时这样处理
        String type = service.getServiceType();
        if (ServiceItembean.SERVICT_TYPE_INTERNAL.equals(type)) {
            HttpCookie cookie = SmartHttpClient.getInstance().getSessionCookie();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setCookie(SmartMobileUtil.getAppHost(this), "JSESSIONID=" + cookie.getValue());
        }

        String title = service.getServiceName();
        serviceTitle.setText(title);

        String url = service.getServiceUrl();
        serviceDispatcher.loadUrl(url);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.iv_back:
                finish();
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
            default :
                break;
        }
    }

    protected void onDestory() {
        finish();
        overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }
}
