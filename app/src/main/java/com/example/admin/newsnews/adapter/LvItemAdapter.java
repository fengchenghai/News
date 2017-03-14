package com.example.admin.newsnews.adapter;

import com.example.admin.newsnews.tool.AnimateFirstDisplayListener;
import com.example.admin.newsnews.R;
import com.example.admin.newsnews.bean.DataEntity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * 自定义适配器绑定数据
 *
 */
public class LvItemAdapter extends BaseAdapter {
	DisplayImageOptions options;

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	private List<DataEntity> datas;
	Context context;
	/**
	 *写构造方法是为了实力化对象的时候1传递数据2提供context参数 
	 *
	 */
	public LvItemAdapter(List<DataEntity> datas, Context context) {
		super();
		this.datas = datas;
		this.context = context;
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.mipmap.loading)//设置图片在下载期间显示的图片
		.showImageForEmptyUri(R.mipmap.loading)//设置图片Uri为空或是错误的时候显示的图片
		 .showImageOnFail(R.mipmap.loading)//设置图片加载/解码过程中错误时候显示的图片
		 .cacheInMemory(true)//是否緩存都內存中
		 .cacheOnDisc(true)//是否緩存到sd卡上
		 .displayer(new RoundedBitmapDisplayer(20))
				.build();
	}
	private class ViewHolder {
		ImageView img;
		TextView title;
		TextView degist;
	}


	@Override
	public int getCount() {
	
		return datas.size();
	}

	@Override
	public DataEntity getItem(int position) {
		
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item, parent, false);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.img_item);
			holder.title = (TextView) convertView.findViewById(R.id.title_item);
			holder.degist = (TextView) convertView.findViewById(R.id.degist_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DataEntity data = getItem(position);
		holder.title.setText(data.getTitle());
		holder.degist.setText(data.getDigest());
		// ImageLoader
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(data.getImgsrc(), holder.img, options, animateFirstListener);
        
		return convertView;
	}

}
