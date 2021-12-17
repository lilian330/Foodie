package com.comp4905.foodie.Models;
//@IgnoreExtraProperties
public class User {
    private String id;         //user id
    private String name;       //username
    private String email;      //email
    private String profile;    //profile URL of the user


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String name,String email, String url) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profile= url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profileURL) {
        this.profile = profileURL;
    }
}
