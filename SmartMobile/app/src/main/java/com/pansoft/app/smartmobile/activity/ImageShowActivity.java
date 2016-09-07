package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pansoft.app.smartmobile.R;
import com.pansoft.app.smartmobile.loader.ImageInfo;
import com.pansoft.app.smartmobile.loader.ImageLoader;

public class ImageShowActivity extends Activity {
    private TextView tv_image_name;
    private ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        initView();
        initEvent();
    }

    private void initView() {
        tv_image_name = (TextView) findViewById(R.id.tv_image_name);
        iv_image = (ImageView) findViewById(R.id.iv_image_canvas);
    }

    private void initEvent() {
        //暂无事件
    }

    private void initData() {
        ImageInfo imageInfo = (ImageInfo) getIntent().getSerializableExtra("image");
        Glide.with(this).using(new ImageLoader()).load(imageInfo).into(iv_image);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //加载数据，显示影像
        initData();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back :
                overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
                break;
        }
    }
}
