package com.example.admin.newsnews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.admin.newsnews.R;


/**
 * Created by ZhangYang on 2016/10/28.
 */

public class WelcomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (isFirstRun())
                {
                    //应用程序第一次运行，加载引导页面
                    startActivity(new Intent(WelcomeActivity.this,GuideActivity.class));
                }else {
                    //如果应用程序不是第一次运行，就跳转到主界面
                    startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                }
                //把自己关了
                finish();
                //设置Activity的跳转动画
                overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                return false;
            }
        }).sendEmptyMessageDelayed(0,2000);
    }

    /**
     * 判断应用是否是第一次运行
     */
    private boolean isFirstRun()
    {
        //获取preferences，如果没有则自动创建
        SharedPreferences preferences = getSharedPreferences("app",MODE_PRIVATE);
        boolean isFirst = preferences.getBoolean("first_run",true);
        //如果是第一次运行
        if (isFirst)
        {
            //改变为不是第一次
            preferences.edit().putBoolean("first_run",false).commit();
        }
        return isFirst;
    }
}
