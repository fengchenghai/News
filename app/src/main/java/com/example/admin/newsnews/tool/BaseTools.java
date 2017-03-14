package com.example.admin.newsnews.tool;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by Adamlambert on 2016/10/19.
 */
public class BaseTools {
    //获取屏幕宽度
    public final static int getWindowsWidth(Activity activity)
    {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
