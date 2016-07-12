package com.example.douglaspfeifer.cinematch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User {
    private String profilePic;
    private String first_name;
    private String last_name;
    private float rating;
    private int numOfRates;
    private String description;
    private String gender;
    private String email;
    private String idFacebook;

    public User() {

    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }


    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
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


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }

}