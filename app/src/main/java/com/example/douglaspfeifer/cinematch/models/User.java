package com.example.douglaspfeifer.cinematch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User {

    private String email;
<<<<<<< Updated upstream
    private Double latitude;
    private Double longitude;
    private String name;
    private String profileImageURL;
=======
    private Object chats;
    private Double latitude;
    private Double longitude;
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
=======

    public Object getChats() {
        return chats;
    }

    public void setChats(Object chats) {
        this.chats = chats;
    }

>>>>>>> Stashed changes
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}