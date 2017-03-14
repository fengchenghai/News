package com.example.admin.newsnews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.admin.newsnews.R;
import com.example.admin.newsnews.adapter.StaggeredAdapter;
import com.example.admin.newsnews.bean.NewsClassify;
import com.example.admin.newsnews.tool.Constans;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by admin on 2016/10/27.
 */
public class ChannelChange extends AppCompatActivity {
    private RecyclerView mRecyclerViewAdd,mRecyclerViewRemove;
    private ArrayList<NewsClassify> mDatasAdd,mDatasRemove,allNews;
    private StaggeredAdapter mStaggeredAdapter,mStaggeredAdapterRemove;
    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_activity);
        initData();

        mRecyclerViewAdd = (RecyclerView) findViewById(R.id.id_recyclerview_add);
        mRecyclerViewRemove = (RecyclerView) findViewById(R.id.id_recyclerview_remove);
        bt = (Button) findViewById(R.id.btn_channel);
        mStaggeredAdapter = new StaggeredAdapter(this,mDatasAdd);
        mStaggeredAdapterRemove = new StaggeredAdapter(this,mDatasRemove);

        mRecyclerViewAdd.setLayoutManager(new StaggeredGridLayoutManager(4,
                StaggeredGridLayoutManager.VERTICAL));
        mRecyclerViewRemove.setLayoutManager(new StaggeredGridLayoutManager(4,
                StaggeredGridLayoutManager.VERTICAL));
        mRecyclerViewAdd.setAdapter(mStaggeredAdapter);
        mRecyclerViewRemove.setAdapter(mStaggeredAdapterRemove);
        //设置动画
        mRecyclerViewAdd.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewRemove.setItemAnimator(new DefaultItemAnimator());

        initEvent();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("addChannel",mDatasAdd);
                setResult(2,intent);
                finish();
            }
        });
    }

    private void initEvent() {
        mStaggeredAdapter.setOnItemClickLitener(new StaggeredAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                mStaggeredAdapterRemove.addData(mDatasAdd.get(position));
                mStaggeredAdapter.removeData(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(ChannelChange.this, "长按点击了" + position, Toast.LENGTH_SHORT).show();
            }
        });
        
        mStaggeredAdapterRemove.setOnItemClickLitener(new StaggeredAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                mStaggeredAdapter.addData(mDatasRemove.get(position));
                mStaggeredAdapterRemove.removeData(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void initData() {
        mDatasAdd = new ArrayList<>();
        mDatasRemove = new ArrayList<>();

        allNews = Constans.removeData();
        Intent intent1 = getIntent();
        mDatasAdd = (ArrayList<NewsClassify>) intent1.getSerializableExtra("datas");

        //给删除频道集合传值
        for (int i=0;i<mDatasAdd.size();i++){
            for (int j=0;j<allNews.size();j++){
                if (allNews.get(j).getTitle().equals(mDatasAdd.get(i).getTitle())){
                    allNews.remove(j);
                    j--;
                }
            }
        }
        mDatasRemove = allNews;
    }

    //保存改变后的频道
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            FileOutputStream fos=openFileOutput("fch",MODE_PRIVATE);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(mDatasAdd);
            fos.close();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
