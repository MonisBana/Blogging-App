package com.mab.bloggingapp;

import android.widget.EditText;

/**
 * Created by montur on 9/29/2017.
 */

public class Blog {
    private String title;
    private String desc;
    private String image;
    private String Name;
    private String likeLabel;
    public Blog(){

    }

    public String getLikeLabel() {
        return likeLabel;
    }

    public void setLikeLabel(String  likeLabel) {
        this.likeLabel = likeLabel;
    }

    public Blog(String title, String desc, String image,String mLikes,String Name) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.Name = Name;
        this.likeLabel = likeLabel;

    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
