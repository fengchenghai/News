package com.example.admin.newsnews.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.admin.newsnews.tool.AnimateFirstDisplayListener;
import com.example.admin.newsnews.adapter.LvItemAdapter;
import com.example.admin.newsnews.R;
import com.example.admin.newsnews.activity.Show;
import com.example.admin.newsnews.bean.DataEntity;
import com.example.admin.newsnews.tool.Url;
import com.google.gson.Gson;
import com.grumoon.pulllistview.PullListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Adamlambert on 2016/10/19.
 */

/**
 * 新闻显示界面
 */
public class NewsFragment extends Fragment implements AdapterView.OnItemClickListener{

    private String text;
    private int index ;
    private List<DataEntity> datas;
    private ListView listview;
    private LvItemAdapter adapter;
    private PullListView plv;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        //拿到新闻ID
        text = args != null ?args.getString("text"):"";
        Log.e("kkkk", "onCreate: "+Url.TopUrl+text+"/"+index+Url.endUrl);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment,null);
        plv = (PullListView) view.findViewById(R.id.pull_down_view);
        listview = (ListView) view.findViewById(R.id.pull_down_view);
        listview.setOnItemClickListener(this);
        datas = new ArrayList<>();
            index = 20;
            volleyGetJson(index);
            //执行刷新
            plv.performRefresh();
        iniview();
        adapter = new LvItemAdapter(datas,context);
        listview.setAdapter(adapter);
        return view;
    }

    private void iniview() {
        //下拉刷新
        plv.setOnRefreshListener(new PullListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                plv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //保留20条新闻
                        for (int i = 0;i<datas.size();i++)
                        {
                            if (i==20)
                            {
                                datas.remove(i);
                                i--;
                            }
                        }
                        index=20;
                        volleyGetJson(index);
                    }
                },2000);
            }
        });

        //上拉加载
            plv.setOnGetMoreListener(new PullListView.OnGetMoreListener() {
                @Override
                public void onGetMore() {
                            index+=20;
                            volleyGetJson(index);
                }});
    }

    //解析获取新闻添加到集合
    private void volleyGetJson(final int index) {
                //获取一个RequestQueue对象
                RequestQueue mQueue = Volley.newRequestQueue(context);
                JsonObjectRequest jor = new JsonObjectRequest(Url.TopUrl+text+"/"+index+Url.endUrl,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        gson(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        adapter.notifyDataSetChanged();
                        plv.refreshComplete();
                        plv.getMoreComplete();
                        Toast.makeText(context,
                                "加载失败,请检查网络",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                mQueue.add(jor);
            }
    //解析
    private void gson(JSONObject jsonObject) {
        Gson gson = new Gson();
        try {
            JSONArray ja = jsonObject.getJSONArray(text);
            for (int i = 0; i < ja.length(); i++) {
                if (ja.length() != 0) {
                    datas.add(gson.fromJson(ja.getJSONObject(i).toString(), DataEntity.class));
                } else {
                    continue;
                }
            }
            //刷新适配器
            adapter.notifyDataSetChanged();
            plv.refreshComplete();
            plv.getMoreComplete();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //点击跳转到webView
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(datas.get(position-1).getUrl() == null)
        {
            Toast.makeText(context,"该链接已失效",Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(context,Show.class);
            intent.putExtra("3g",datas.get(position-1).getUrl());
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimateFirstDisplayListener.displayedImages.clear();
    }

    @Override
    public void onAttach(Context context) {
        this.context=context;
        super.onAttach(context);
    }
}
