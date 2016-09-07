package com.pansoft.app.smartmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.pansoft.app.smartmobile.R;

public class NavigatorActivity extends Activity {
    private ImageView ivNavigator;

    public NavigatorActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        initView();
        initData();
    }

    private void initView() {
        ivNavigator = (ImageView) findViewById(R.id.iv_navigator);

        AlphaAnimation animation = new AlphaAnimation(0.3f, 10.f);
        Resources resources = getResources();
        int duration = resources.getInteger(R.integer.anim_navigator_duration);
        animation.setDuration(duration);
        ivNavigator.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivNavigator.setBackgroundResource(R.drawable.bg_navigator);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onAnimationFinish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnimationFinish();
            }
        });
    }

    private void initData() {

    }

    private void onAnimationFinish() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
