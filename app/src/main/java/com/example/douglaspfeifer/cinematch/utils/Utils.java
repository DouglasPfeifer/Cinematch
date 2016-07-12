package com.example.douglaspfeifer.cinematch.utils;

public class Utils {

    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail"
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static void updateSharedPreferences () {

    }
}
