package com.rtech.threadly.models;

public class Preview_Post_model {
    public String image_url;
   public int postid;
    public Preview_Post_model(int postid, String image_url){
        this.postid=postid;
        this.image_url=image_url;

    }
}
