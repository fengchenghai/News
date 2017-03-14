package com.example.admin.newsnews.bean;

import java.io.Serializable;

/**
 * Created by Adamlambert on 2016/10/19.
 */
public class NewsClassify implements Serializable{
    //新闻id
    private int id;
    //新闻标题
    private String title;
    private String urlId;

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
