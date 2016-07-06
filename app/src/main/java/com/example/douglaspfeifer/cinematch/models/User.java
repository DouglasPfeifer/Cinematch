package com.example.douglaspfeifer.cinematch.models;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;
import java.util.Date;

/**
 * Created by douglaspfeifer on 11/06/16.
 */
public class User implements Parcelable {
    private String first_name;
    private String last_name;
    private String description;
    private String email;
    private String idFacebook;
    private String gender;
    private String birthday;
    private URL profilePic;

    public User() {

    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String firstName) {
        this.first_name = first_name;
    }


    public String getLastName() {
        return last_name;
    }

    public void setLastName(String lastName) {
        this.last_name = last_name;
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


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public URL getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(URL profilePic) {
        this.profilePic = profilePic;
    }


    public String getFacebookId() {
        return idFacebook;
    }

    public void setFacebookId(String facebookId) {
        this.idFacebook = idFacebook;
    }


    /*
     * Métodos para permitir o parcelable
     */
    protected User(Parcel in) {
        first_name = in.readString();
        last_name = in.readString();
        description = in.readString();
        email = in.readString();
        idFacebook = in.readString();
        gender = in.readString();
        birthday = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(description);
        dest.writeString(email);
        dest.writeString(idFacebook);
        dest.writeString(gender);
        dest.writeString(birthday);
    }
}
