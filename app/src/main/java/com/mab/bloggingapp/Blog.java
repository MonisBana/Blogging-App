package com.mab.bloggingapp;

/**
 * Created by montur on 9/29/2017.
 */

public class Blog {
    private String title,desc,image;
    private String likeLabel;
    public Blog(){

    }

    public String getLikeLabel() {
        return likeLabel;
    }

    public void setLikeLabel(String  likeLabel) {
        this.likeLabel = likeLabel;
    }

    public Blog(String title, String desc, String image,String mLikes) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.likeLabel = likeLabel;

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
