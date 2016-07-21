package com.example.douglaspfeifer.cinematch.ui.profile;

import android.content.Context;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.models.ItemObject;
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
import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    // Layout
    ImageView myProfileImage_imageView;
    TextView myProfileName_textView;
    GridView gridView;

    public static String [] prgmNameList={"Coisa","Coisa","Coisa","Coisa",
            "Coisa","Coisa","Coisa","Coisa",
            "Coisa","Coisa","Coisa","Coisa",
            "Coisa","Coisa","Coisa","Coisa",
            "Coisa","Coisa","Coisa","Coisa",
            "Coisa","Coisa","Coisa","Coisa",
            "Coisa","Coisa","Coisa","Coisa",
            "Coisa","Coisa","Coisa","Coisa"
    };
    public static int [] prgmImages={R.drawable.me_map_icon,R.drawable.me_map_icon, R.drawable.me_map_icon,R.drawable.me_map_icon,
            R.drawable.me_map_icon,R.drawable.me_map_icon, R.drawable.me_map_icon,R.drawable.me_map_icon,
            R.drawable.me_map_icon,R.drawable.me_map_icon, R.drawable.me_map_icon,R.drawable.me_map_icon,
            R.drawable.me_map_icon,R.drawable.me_map_icon, R.drawable.me_map_icon,R.drawable.me_map_icon,
            R.drawable.me_map_icon,R.drawable.me_map_icon, R.drawable.me_map_icon,R.drawable.me_map_icon,
            R.drawable.me_map_icon,R.drawable.me_map_icon, R.drawable.me_map_icon,R.drawable.me_map_icon,
            R.drawable.me_map_icon,R.drawable.me_map_icon, R.drawable.me_map_icon,R.drawable.me_map_icon,
            R.drawable.me_map_icon,R.drawable.me_map_icon, R.drawable.me_map_icon,R.drawable.me_map_icon
    };



    // Logged User
    private User mLoggedUser;

    // Firebase
    private Firebase mFirebaseUsersRef;

    static final String[] MOBILE_OS = new String[] {
            "Android", "iOS","Windows", "Blackberry" };

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
        mLoggedUser.setEmail(sharedPref.getString("userEmail", null));

        // Instantiate the views
        myProfileImage_imageView = (ImageView) rootView.findViewById(R.id.myProfileImage_imageView);
        myProfileName_textView = (TextView) rootView.findViewById(R.id.myProfileName_textView);

        gridView = (GridView) rootView.findViewById(R.id.genre_gridView);

        gridView.setAdapter(new CustomAdapter(getContext(), prgmNameList,prgmImages));

        // Get a reference to our user
        mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mLoggedUser.getEmail());
    }

    private void attachFirebaseListener () {
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

    private List<ItemObject> getAllItemObject(){
        List<ItemObject> items = new ArrayList<>();
        items.add(new ItemObject("Image One", "one"));
        items.add(new ItemObject("Image Two", "two"));
        items.add(new ItemObject("Image Three", "three"));
        items.add(new ItemObject("Image Four", "four"));
        items.add(new ItemObject("Image Five", "five"));
        items.add(new ItemObject("Image Six", "six"));
        items.add(new ItemObject("Image Seven", "seven"));
        items.add(new ItemObject("Image Eight", "eight"));
        items.add(new ItemObject("Image One", "one"));
        items.add(new ItemObject("Image Two", "two"));
        items.add(new ItemObject("Image Three", "three"));
        items.add(new ItemObject("Image Four", "four"));
        items.add(new ItemObject("Image Five", "five"));
        items.add(new ItemObject("Image Six", "six"));
        items.add(new ItemObject("Image Seven", "seven"));
        items.add(new ItemObject("Image Eight", "eight"));
        items.add(new ItemObject("Image One", "one"));
        items.add(new ItemObject("Image Two", "two"));
        items.add(new ItemObject("Image Three", "three"));
        items.add(new ItemObject("Image Four", "four"));
        items.add(new ItemObject("Image Five", "five"));
        items.add(new ItemObject("Image Six", "six"));
        items.add(new ItemObject("Image Seven", "seven"));
        items.add(new ItemObject("Image Eight", "eight"));
        return items;
    }
}