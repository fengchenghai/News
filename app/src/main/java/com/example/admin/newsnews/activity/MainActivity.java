package com.example.admin.newsnews.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.admin.newsnews.R;
import com.example.admin.newsnews.adapter.NewsFragmentPagerAdpter;
import com.example.admin.newsnews.bean.NewsClassify;
import com.example.admin.newsnews.fragment.NewsFragment;
import com.example.admin.newsnews.jazzyviewpager.RotateDownPageTransformer;
import com.example.admin.newsnews.tool.BaseTools;
import com.example.admin.newsnews.tool.Constans;
import com.example.admin.newsnews.view.CircleImageView;
import com.example.admin.newsnews.view.ColumnHorizontalScrollView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView name;
    private Button land;

    //登录头像
    private CircleImageView civ;
    //声明第三方平台引用
    private Platform qq;
    //// 声明用户名和用户头像地址引用
    private String userName,userIconUrl;
    // Handler消息类型
    private static final int MSG_AUTH_CANCEL = 0;
    private static final int MSG_AUTH_ERROR = 1;
    private static final int MSG_AUTH_COMPLETE = 2;

    private RequestQueue rq;

    //自定义的视图
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    LinearLayout mRadioGroup_content;
    LinearLayout ll_more_columns;
    RelativeLayout rl_column;
    private ViewPager mViewPager;
    private ImageView button_more_colums;
    //新闻的分类列表
    private ArrayList<NewsClassify> newsClassify = new ArrayList<>();
    private ArrayList<NewsClassify> adddatas = new ArrayList<>();
    private boolean isFirst = true;
    //当前选中的栏目
    private int columnSelectIndex = 0;
    //左阴影部分
    private ImageView shade_left;
    //右阴影部分
    private ImageView shade_right;
    //屏幕的宽度
    private int mScreenWidth = 0;
    //item宽度
    private int mItemWidth = 0;
    private ArrayList<Fragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取屏幕宽度
        mScreenWidth = BaseTools.getWindowsWidth(this);
        //一个item宽度为屏幕的1/7
        mItemWidth = mScreenWidth/7;

        init();
        initView();

        rq = Volley.newRequestQueue(this);
        initShareSdk();
    }

    //设置标题栏
    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("新闻新闻");
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);

        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerView.setClickable(true);
                toolbar.setTitle("登录");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                toolbar.setTitle("新闻新闻");
            }
        });
    }


    /*初始化Layout控件*/
    private void initView() {
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView)
                findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout)
                findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_colums);
        rl_column = (RelativeLayout) findViewById(R.id.rl_colum);
        button_more_colums = (ImageView) findViewById(R.id.button_more_colums);
        civ = (CircleImageView) findViewById(R.id.head_civ);
        name = (TextView) findViewById(R.id.name_civ);
        land = (Button) findViewById(R.id.land_civ);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setOnPageChangeListener(pageListener);
        shade_left = (ImageView) findViewById(R.id.shade_left);
        shade_right = (ImageView) findViewById(R.id.shade_right);
        button_more_colums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把当前频道的集合传过去
                Intent intent = new Intent(MainActivity.this,ChannelChange.class);
                intent.putExtra("datas",newsClassify);
                startActivityForResult(intent,1);
            }
        });

        // TODO: 2016/11/2
        land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (land.getText().toString().equals("登   录"))
                {
                    login();
                }else {
                    logout();
                    land.setText("登   录");
                }
            }
        });

        //第一次默认八个频道，之后获取改变后的频道
        if (isFirstRun())
        {
            setChangeView(Constans.getData());
        }else {
            setChangeView(getChannel());
        }
    }

    //获取改变后的频道
    private ArrayList<NewsClassify> getChannel()
    {
        ArrayList<NewsClassify> list = new ArrayList<>();
        try {
            FileInputStream fis=openFileInput("fch");
            ObjectInputStream ois=new ObjectInputStream(fis);
            list= (ArrayList<NewsClassify>) ois.readObject();
            fis.close();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 判断应用是否是第一次运行
     */
    private boolean isFirstRun()
    {
        //获取preferences，如果没有则自动创建
        SharedPreferences preferences = getSharedPreferences("channel",MODE_PRIVATE);
        boolean isFirst = preferences.getBoolean("first_channel",true);
        //如果是第一次运行
        if (isFirst)
        {
            //改变为不是第一次
            preferences.edit().putBoolean("first_channel",false).commit();
        }
        return isFirst;
    }

    //获取返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1 && resultCode==2){
            adddatas = (ArrayList<NewsClassify>) data.getSerializableExtra("addChannel");
            selectTab(0);
            setChangeView(adddatas);
        }
    }

    /**
     * 当栏目发生变化时调用
     */
    private void setChangeView(ArrayList<NewsClassify> list)
    {
        initColumnData(list);
        initTabColumn();
        initFragment();
    }
    /**
     * 获取Column栏目数据
     */
    private void initColumnData(ArrayList list)
    {
        newsClassify = list;
    }
    /**
     * 初始化栏目项
     */
    private void initTabColumn()
    {
        mRadioGroup_content.removeAllViews();
        int count = newsClassify.size();
        mColumnHorizontalScrollView.setParam(this,mScreenWidth,mRadioGroup_content,
                shade_left,shade_right,ll_more_columns,rl_column);
        for (int i = 0;i<count;i++)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 10;
            params.rightMargin = 10;
            //创建TextView对象，并对其进行属性设置
            TextView localTextView = new TextView(this);
            localTextView.setTextAppearance(this,R.style.top_category_scroll_view_item_text);

            localTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            localTextView.setGravity(Gravity.CENTER);
            localTextView.setPadding(5,0,5,0);
            localTextView.setId(i);
            localTextView.setText(newsClassify.get(i).getTitle());
            localTextView.setTextColor(getResources().getColorStateList(
                    R.color.top_category_scroll_text_color_day
            ));
            //给TextView设置选中状态
            if (columnSelectIndex == i)
            {
                localTextView.setSelected(true);
            }
            //添加监听
            localTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0;i<mRadioGroup_content.getChildCount();i++)
                    {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                        {
                            localView.setSelected(false);
                        }else
                        {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                    Toast.makeText(MainActivity.this,
                            newsClassify.get(v.getId()).getTitle(),Toast.LENGTH_SHORT).show();
                }
            });
            mRadioGroup_content.addView(localTextView,i,params);
        }
    }
    /**
     * 选择的Column里面的Tab
     */
    private void selectTab(int tab_position)
    {
        columnSelectIndex = tab_position;
        for (int i =0 ;i<mRadioGroup_content.getChildCount();i++)
        {
            //拿到被选中的视图
            View checkView = mRadioGroup_content.getChildAt(tab_position);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l+k/2-mScreenWidth/2;
            //滚动View
            mColumnHorizontalScrollView.smoothScrollTo(i2,0);
        }
        //判断是否选中
        for (int j = 0;j<mRadioGroup_content.getChildCount();j++)
        {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean isCheck;
            if (j == tab_position)
            {
                isCheck = true;
            }else {
                isCheck = false;
            }
            checkView.setSelected(isCheck);
        }
    }
    /**
     * 初始化Fragment
     */
    private void initFragment()
    {
        fragments = new ArrayList<>();
        int count = newsClassify.size();
        for (int i = 0;i<count;i++)
        {
            Bundle data = new Bundle();
            data.putString("text",newsClassify.get(i).getUrlId());
            //创建Fragment对象
            NewsFragment newFragment = new NewsFragment();
            newFragment.setArguments(data);
            fragments.add(newFragment);
        }
        //创建Fragment适配器
        NewsFragmentPagerAdpter mAdapter = new NewsFragmentPagerAdpter(
                getSupportFragmentManager(),fragments
        );
        mViewPager.setAdapter(mAdapter);
        //翻页动画
        // TODO: 2016/10/28
        mViewPager.setPageTransformer(true,new RotateDownPageTransformer());
    }
    /**
     * ViewPager切换监听方法
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }
        @Override
        public void onPageSelected(int i) {
            //设置选中的View
            mViewPager.setCurrentItem(i);
            selectTab(i);
        }
        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    // 初始化分享SDK
    private void initShareSdk() {
        ShareSDK.initSDK(this);
        qq = ShareSDK.getPlatform(QQ.NAME);

        // 如果已经授权则先取消授权
        if (qq.isAuthValid())
            qq.removeAccount(true);
    }

    /**
     * 登录事件
     * */
    //点击头像登录事件
    public void login(){
        if (qq.isAuthValid()){
            Toast.makeText(MainActivity.this,"你已经登录！",Toast.LENGTH_SHORT).show();
            return;
        }
        //登录方法
        qq.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //获取用户名和用户头像地址
                userName = platform.getDb().getUserName();
                userIconUrl = platform.getDb().getUserIcon();
                handler.sendEmptyMessage(MSG_AUTH_COMPLETE);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                handler.sendEmptyMessage(MSG_AUTH_ERROR);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                handler.sendEmptyMessage(MSG_AUTH_CANCEL);
            }
        });
        qq.SSOSetting(false);
        //authorize与showUser单独调用一个即可
        qq.authorize();//单独授权,OnComplete返回的hashmap是空的
    }

    // 注销按钮点击事件
    public void logout() {
        if (qq.isAuthValid()) {
            name.setText("未登录");
            qq.removeAccount(true);
            // 将头像还原
            civ.setImageResource(R.mipmap.land);
            Toast.makeText(this,"已成功退出", Toast.LENGTH_SHORT).show();
        }
    }

    // 登陆后使用Handler更新用户头像和用户名，并更新UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTH_COMPLETE:
                    getUserIcon();
                    // 显示用户名
                    name.setText(userName);
                    land.setText("注   销");
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    break;
                case MSG_AUTH_ERROR:
                    Toast.makeText(MainActivity.this, "授权失败", Toast.LENGTH_LONG).show();
                    break;
                case MSG_AUTH_CANCEL:
                    Toast.makeText(MainActivity.this, "已取消登录", Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
//     获取头像
    private void getUserIcon(){
        ImageRequest imageRequest = new ImageRequest(userIconUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {

                        civ.setImageBitmap(bitmap);
                    }
                },0,0, Bitmap.Config.RGB_565,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TAG",volleyError.toString() );
            }
        }
        );
        rq.add(imageRequest);
    }

    // 程序关闭时清理缓存
    @Override
    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        super.onDestroy();
    }

}
