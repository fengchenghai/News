package com.example.admin.newsnews.bean;

/**
 * 新闻
 */
public class DataEntity {

	private String title;//标题
	private String digest;//关键字
	private String imgsrc;//图片
	private String url;//3g网址

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public DataEntity(String title, String digest, String imgsrc, String url) {
		this.title = title;
		this.imgsrc = imgsrc;
		this.digest = digest;
		this.url = url;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgsrc() {
		return imgsrc;
	}
	public void setImgSc(String imgsrc) {
		this.imgsrc = imgsrc;
	}
	public DataEntity(String title, String imgsrc) {
		super();
		this.title = title;
		this.imgsrc = imgsrc;
	}
	public DataEntity() {
		super();
	}
	
}
