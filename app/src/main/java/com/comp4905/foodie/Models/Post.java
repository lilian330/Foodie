package com.comp4905.foodie.Models;

import com.google.firebase.database.ServerValue;

public class Post {
    private String postKey;     //post key
    private String postTitle;   //title
    private String postContent; //content
    private String postImg;     //post photo URL
    private String uName;       //post UserName
    private String uID;         //user id
    private String uProfile;    //user profile URL
    private Object timeStamp;   //post time

    public Post(){}




    public Post(String title, String text, String photo, String id, String profile, String name){
        this.postTitle = title;
        this.postContent = text;
        this.postImg= photo;
        this.uID = id;
        this.uName = name;
        this.uProfile = profile;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

    public String getPostKey(){return postKey;}
    public String getPostTitle(){return postTitle;}
    public String getPostContent(){return postContent;}
    public String getPostImg(){return postImg;}
    public String getUID(){return uID;}
    public String getUProfile(){return uProfile;}
    public Object getTimeStamp(){return timeStamp;}
    public String getuName() {return uName;}

    public void setuName(String uName) {this.uName = uName;}
    public void setPostKey(String key){this.postKey = key;}
    public void setPostTitle(String title){this.postTitle = title;}
    public void setPostContent(String text){this.postContent= text;}
    public void setPostImg(String img){this.postImg= img;}
    public void setUID(String name){this.uID = name;}
    public void setUProfile(String p){this.uProfile = p;}
    public void setTimeStamp(Object time){this.timeStamp = time;}
}
