package com.example.admin.newsnews.jazzyviewpager;

import android.support.v4.view.ViewPager;
import android.view.View;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Adamlambert on 2015/8/12.
 */
public class CubeOutTransformer implements ViewPager.PageTransformer {

    /**
     * position����ָ������ҳ���������Ļ���ĵ�λ�á�����һ����̬���ԣ�������ҳ��Ĺ������ı䡣��һ��ҳ�����������Ļ�ǣ�����ֵ��0��
     * ��һ��ҳ��ո��뿪��Ļ���ұ�ʱ������ֵ��1��������Ҳҳ��ֱ������һ��ʱ������һ��ҳ���λ����-0.5����һ��ҳ���λ����0.5��������Ļ��ҳ���λ��
     * ��ͨ��ʹ������setAlpha()��setTranslationX()����setScaleY()����������ҳ������ԣ��������Զ���Ļ���������
     */
    @Override
    public void transformPage(View view, float position) {
        if (position < -1) {
            view.setAlpha(1);
        } else if (position <= 0) {
            //�������󻬶�Ϊ��ǰView
            //������ת���ĵ㣻
            view.setAlpha(1 + position);
            ViewHelper.setPivotX(view, view.getMeasuredWidth());
            ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);

            //ֻ��Y������ת����
            ViewHelper.setRotationY(view, 90f * position);
        } else if (position <= 1) {
            //�������һ���Ϊ��ǰView
            view.setAlpha(1 - position);
            ViewHelper.setPivotX(view, 0);
            ViewHelper.setPivotY(view, view.getMeasuredHeight() * 0.5f);
            ViewHelper.setRotationY(view, 90f * position);
        }
        else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }
    }
}