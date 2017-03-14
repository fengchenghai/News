package com.example.admin.newsnews.tool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class BitmapUtil {

	private static final String CACHE_PATH = 
			Environment.getExternalStorageDirectory().getPath()+"/image_cache/";

	/**
	 * 根据图片地址从本地获取Bitmap
	 * @param imgUrl
	 * @return
	 */
	public static Bitmap getBitmap(String imgUrl){
		String filePath = CACHE_PATH + MD5(imgUrl); 
		File file = new File(filePath);
		if(file.exists()){
			//��һ���ļ�·�������õ�һ��Bitmap����
  			Bitmap bitmap = BitmapFactory.decodeFile(filePath);
			if(bitmap != null){
				return bitmap;
			}
		}
		return null;
	}
	/**
	 * 保存图片
	 * @param is 输入流
	 * @param imgUrl 图片地址
	 */
	public static void saveBitmap(InputStream is,String imgUrl){
		String filePath = CACHE_PATH + MD5(imgUrl); 
		File file = new File(filePath);
		//从一个文件路径解析得到一个Bitmap对象
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			byte[] buff = new byte[1024*4];
			int len;
			while((len = is.read(buff)) != -1){
				fos.write(buff, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * MD5加密算法
	 * 在这里主要是为了格式化保存的图片的文件名（将Http://.........jpg 转化成不含特殊字符的文件名）
	 * 加密后得到的文件名是唯一的
	 * @param s
	 * @return
	 */
	public static String MD5(String s) {
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < md.length; i++) {
				int val = ((int) md[i]) & 0xff;
				if (val < 16)
					sb.append("0");
				sb.append(Integer.toHexString(val));
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
