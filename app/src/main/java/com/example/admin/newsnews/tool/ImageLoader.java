package com.example.admin.newsnews.tool;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
	/**
	 * LruCache用与图片缓存，防止OOM：out of memory内存溢出
	 */
	private LruCache<String, Bitmap> mCache;
	/**线程池**/
	private ExecutorService mExecutor;
	public ImageLoader() {
		//同时执行的线程数最大为5
		mExecutor = Executors.newFixedThreadPool(5);
		//获取系统给应用的最大运行内存大小
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int maxSize = maxMemory/8;
		Log.d("ImageLoader", "����ڴ棺"+maxMemory);
		mCache = new LruCache<String, Bitmap>(maxSize){
			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap value) {
				//如果手机系统版本号在12以上，用这种方式获取Bitmap内存大小
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
					return value.getByteCount();
				}
				return value.getRowBytes()*value.getHeight();
			}
		};
	}
	/**
	 * 显示图片方法
	 * @param imgUrl
	 * @param imageView
	 */
	public void displayImg(String imgUrl,ImageView imageView){
		//为ImageView绑定一个字符串
		imageView.setTag(imgUrl);
		//先从LruCache缓存中获取Bitmap
		Bitmap bitmap = getBitmapFromLruCache(imgUrl);
		if(bitmap != null){
			imageView.setImageBitmap(bitmap);
			log("Bitmap来自LruCache");
			return;
		}
		//如果LruCache缓存中不存在地址对应的Bitmap对象
		//就用线程池执行一个线程任务
		GetBitmapRunnable runnable = new GetBitmapRunnable(imgUrl, imageView);
		mExecutor.execute(runnable);
	}
	/**
	 * 从本地或网络 获取Bitmap的Runnable
	 * @author Administrator
	 *
	 */
	class GetBitmapRunnable implements Runnable{
		private String imgUrl;
		private ImageView imageView;
		public GetBitmapRunnable(String imgUrl, ImageView imageView) {
			super();
			this.imgUrl = imgUrl;
			this.imageView = imageView;
		}
		@Override
		public void run() {
			//先从本地获取Bitmap
			Bitmap bitmap = getBitmapFromLocal(imgUrl);
			if(bitmap != null){
				//如果从本地获取到了对应的Bitmap对象
				if(imgUrl.equals(imageView.getTag())){
					showBitmapOnUiThread(imageView, bitmap);
				}
				log("Bitmap来自本地");
				return;
			}
			//如果从本地没有获取到Bitmap对象，就从网络上下载
			bitmap = getBitmapFromNetwork(imgUrl);
			if(bitmap != null){
				log("Bitmap来自网络");
				if(imgUrl.equals(imageView.getTag())){
					showBitmapOnUiThread(imageView, bitmap);
				}
			}
		}
	}

	private void showBitmapOnUiThread(final ImageView imageView,final Bitmap bitmap){
		imageView.post(new Runnable() {

			@Override
			public void run() {
				imageView.setImageBitmap(bitmap);
			}
		});
	}
	/**
	 * 从LruCache缓存中获取Bitmap
	 * @param imgUrl：图片地址
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String imgsrc){
		return mCache.get(imgsrc);
	}
	/**
	 * 从本地中获取Bitmap对象
	 * @param imgUrl2
	 * @return
	 */
	private Bitmap getBitmapFromLocal(String imgUrl) {
		Bitmap bitmap = BitmapUtil.getBitmap(imgUrl);
		//将Bitmap加入LruCache缓存
		if(bitmap != null){
			mCache.put(imgUrl, bitmap);
		}
		return bitmap;
	}
	/**
	 * 从网络下载图片，并返回Bitmap对象
	 * @param imgUrl2
	 * @return
	 */
	private Bitmap getBitmapFromNetwork(String imgUrl) {
		try {
			URL url = new URL(imgUrl);
			URLConnection conn = url.openConnection();
			conn.setReadTimeout(6000);
			conn.setConnectTimeout(6000);
			InputStream is = conn.getInputStream();
			//保存图片文件到本地
			BitmapUtil.saveBitmap(is, imgUrl);
			//从本地文件获取Bitmap对象
			Bitmap bitmap = BitmapUtil.getBitmap(imgUrl);
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void log(String str){
		Log.d("ImageLoader", "--------------"+str);
	}
}
