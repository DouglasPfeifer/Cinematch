package com.example.douglaspfeifer.cinematch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User {
    private String profileImageURL;
    private String name;
    private float rating;
    private int numOfRates;
    private String description;
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

    public void setName(String first_name) {
        this.name = name;
    }


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    public int getNumOfRates() {
        return numOfRates;
    }

    public void setNumOfRates(int numbOfRates) {
        this.numOfRates = numbOfRates;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}