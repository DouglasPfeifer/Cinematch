package com.example.douglaspfeifer.cinematch.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.models.User;
import com.example.douglaspfeifer.cinematch.ui.MainActivity;
import com.example.douglaspfeifer.cinematch.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.realtime.util.StringListReader;

import java.io.InputStream;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    // Layout
    ImageView myProfileImage_imageView;
    TextView myProfileName_textView;

    // Logged User
    private User mLoggedUser;

    // Firebase
    private Firebase mFirebaseUsersRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeScreen(rootView);

        attachFirebaseListener();

        // Inflate the layout for this fragment
        return rootView;
    }

    public void initializeScreen (View rootView) {
        // Set user email
        mLoggedUser = new User();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", 0);
        mLoggedUser.setEmail(sharedPref.getString("userEmail", ""));

        // Instantiate the views
        myProfileImage_imageView = (ImageView) rootView.findViewById(R.id.myProfileImage_imageView);
        myProfileName_textView = (TextView) rootView.findViewById(R.id.myProfileName_textView);

        // Get a reference to our user
        mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mLoggedUser.getEmail());
    }

    public void attachFirebaseListener () {
        // Attach an listener to read the data at our users reference
        // Firebase listener for everything that we need to get from the database
        // Every time something changes on the database, the listener will change on the profile
        mFirebaseUsersRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLoggedUser = dataSnapshot.getValue(User.class);
                updateProfileData();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void updateProfileData () {
        new DownloadImageTask(myProfileImage_imageView)
                .execute(mLoggedUser.getProfileImageURL());

        myProfileName_textView.setText(mLoggedUser.getName());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}