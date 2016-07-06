package com.example.douglaspfeifer.cinematch.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.models.User;
import com.example.douglaspfeifer.cinematch.ui.MainActivity;
import com.example.douglaspfeifer.cinematch.utils.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{

    // Layout
    TextView myProfileDescription_textView;
    Button editButton;

    // Logged User Email
    private User mLoggedUser;
    private String mLoggedUserEmail;

    private Firebase mFirebaseUsersRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Instantiate the views
        myProfileDescription_textView = (TextView) rootView.findViewById(R.id.myProfileDescription_textView);
        editButton = (Button) rootView.findViewById(R.id.editProfile_Button);
        editButton.setOnClickListener(this);

        // Get a reference to our user
        mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS);

        final Bundle args = getArguments();
        if (args != null) {
            mLoggedUser = args.getParcelable("loggedUser");
        }
        mLoggedUserEmail = mLoggedUser.getEmail();

        // Attach an listener to read the data at our users reference
        // Firebase listener for everything that we need to get from the database
        // Every time something changes on the database, the listener will change on the profile
        mFirebaseUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLoggedUser = dataSnapshot.child(mLoggedUserEmail).getValue(User.class);
                myProfileDescription_textView.setText(mLoggedUser.getDescription());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        Button editButton = (Button) rootView.findViewById(R.id.editProfile_Button);
        editButton.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editProfile_Button:
                editProfile();
                break;
        }
    }

    public void editProfile() {
        Intent intent = new Intent(getActivity(), EditableProfileActivity.class);
        startActivity(intent);
    }
}
