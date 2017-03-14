package com.example.admin.newsnews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Adamlambert on 2016/10/19.
 */
public class NewsFragmentPagerAdpter extends FragmentStatePagerAdapter{
    //声明集合，代表Fragment
    private ArrayList<Fragment> fragments;

    public NewsFragmentPagerAdpter(FragmentManager fm,ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
