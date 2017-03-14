package com.example.admin.newsnews.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.newsnews.R;
import com.example.admin.newsnews.bean.NewsClassify;
import com.example.admin.newsnews.tool.DensityUtil;

public class StaggeredAdapter extends
        RecyclerView.Adapter<StaggeredAdapter.MyViewHolder>
{

    private List<NewsClassify> mDatas;
    private LayoutInflater mInflater;

    private List<Integer> mHeights;
    private Context context;
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public StaggeredAdapter(Context context, List<NewsClassify> datas)
    {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
        this.context = context;

        mHeights = new ArrayList<Integer>();
        for (int i = 0; i < mDatas.size(); i++)
        {
            mHeights.add( DensityUtil.dip2px(context,50));
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(
                R.layout.item_staggered_home, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {
        LayoutParams lp = holder.tv.getLayoutParams();
        lp.height = mHeights.get(position);

        holder.tv.setLayoutParams(lp);
        holder.tv.setText(mDatas.get(position).getTitle());

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
//                    int pos = holder.getLayoutPosition();
//                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
//                    removeData(pos);
                    Toast.makeText(context,"啊~", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    public void addData(NewsClassify data)
    {
        mDatas.add(mDatas.size(),data);
        mHeights.add( DensityUtil.dip2px(context,50));
        notifyItemInserted(mDatas.size());
    }

    public void removeData(int position)
    {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends ViewHolder
    {

        TextView tv;

        public MyViewHolder(View view)
        {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_num);

        }
    }
}