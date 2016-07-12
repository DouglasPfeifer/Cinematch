package com.example.douglaspfeifer.cinematch.utils;

import com.example.douglaspfeifer.cinematch.BuildConfig;

/**
 * Constants class store most important strings and paths of the app
 */
public final class Constants {

    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where user lists are stored (ie "userLists")
     */
    public static final String FIREBASE_LOCATION_USERS = "users";

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_EMAIL = "email";

    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;

    /**
     * Constants for bundles, extras and shared preferences keys
     */
    public static final String LOGIN_BUNDLE_FIRST_NAME = "first_name";
    public static final String LOGIN_BUNDLE_LAST_NAME = "last_name";
    public static final String LOGIN_BUNDLE_EMAIL = "email";
    public static final String LOGIN_BUNDLE_GENDER = "gender";

    /**
     * Constants for Firebase login
     */
    public static final String PASSWORD_PROVIDER = "password";
    public static final String FACEBOOK_PROVIDER = "facebook";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";
}
