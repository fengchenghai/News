package com.example.admin.newsnews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.admin.newsnews.adapter.GuidePagerAdapter;
import com.example.admin.newsnews.R;
import com.example.admin.newsnews.jazzyviewpager.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangYang on 2016/10/28.
 * 引导页面
 */

public class GuideActivity extends AppCompatActivity{
    private ViewPager viewPager;
    private List<View> views;
    private int[] images;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initViews();
    }

    /**
     * 初始化View Pager
     */
    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        images = new int[]{R.mipmap.yin_dao1,R.mipmap.yin_dao2,R.mipmap.yin_dao3};
        views = new ArrayList<>();
        for (int i = 0;i<images.length;i++)
        {
            ImageView imageView = new ImageView(this);
            if (i != images.length)
            {
                imageView.setImageResource(images[i]);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            views.add(imageView);
        }
        //创建适配器，初始化数据
        final GuidePagerAdapter adapter = new GuidePagerAdapter(views);
        viewPager.setAdapter(adapter);

        //给ViewPager设置动画
        viewPager.setPageTransformer(true,new DepthPageTransformer());
        //给页面设置间隙
        viewPager.setOffscreenPageLimit(2);
        //监听View Pager的页面改变
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                //如果View Pager选中最后一页，就跳转到Activity
                if (position == adapter.getCount()-1)
                {
                    startActivity(new Intent(GuideActivity.this,MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.activity_enter,R.anim.activity_exit);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
