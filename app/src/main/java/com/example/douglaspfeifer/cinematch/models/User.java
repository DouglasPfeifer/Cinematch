package com.example.douglaspfeifer.cinematch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User {
    private String profileImageURL;
    private String name;
    private String email;

    public User() {

    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
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

}