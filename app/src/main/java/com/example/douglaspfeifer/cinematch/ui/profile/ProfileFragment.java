package com.example.douglaspfeifer.cinematch.ui.profile;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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
import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.realtime.util.StringListReader;
import com.firebase.geofire.GeoFire;

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
    Button excludeAccountButton;
    public static String [] prgmNameList={"Animação","Detetive","Drama","Fantasia",
            "Guerra","Histórico","Romance","Scifi",
            "Terror"
    };
    public static int [] prgmImages={R.drawable.animacao,R.drawable.detetive, R.drawable.drama,R.drawable.fantasia,
            R.drawable.guerra,R.drawable.historico, R.drawable.romance,R.drawable.scifi,
            R.drawable.terror
    };
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
        mLoggedUser.setEmail(sharedPref.getString("userEmail", null));
        // Instantiate the views
        myProfileImage_imageView = (ImageView) rootView.findViewById(R.id.myProfileImage_imageView);
        myProfileName_textView = (TextView) rootView.findViewById(R.id.myProfileName_textView);
        gridView = (GridView) rootView.findViewById(R.id.genre_gridView);
        gridView.setAdapter(new CustomAdapter(getContext(), prgmNameList,prgmImages));
        // Get a reference to our user
        mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mLoggedUser.getEmail());

        excludeAccountButton = (Button) rootView.findViewById(R.id.buttonExcludeAccount);
        excludeAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                LoginManager.getInstance().logOut();
                                SharedPreferences settings = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                                settings.edit().clear().commit();
                                getActivity().finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                mFirebaseUsersRef.removeValue();


            }
        });
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
    public class CustomAdapter extends BaseAdapter{
        String [] result;
        Context context;
        int [] imageId;
        private LayoutInflater inflater=null;
        public CustomAdapter(Context mainActivity, String[] prgmNameList, int[] prgmImages) {
            // TODO Auto-generated constructor stub
            result=prgmNameList;
            context=mainActivity;
            imageId=prgmImages;
            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return result.length;
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        public class Holder
        {
            TextView tv;
            ImageView img;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.grid_item_genre, null);
            holder.tv=(TextView) rowView.findViewById(R.id.genre_textView);
            holder.img=(ImageView) rowView.findViewById(R.id.genre_imageView);
            holder.tv.setText(result[position]);
            holder.img.setImageResource(imageId[position]);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", 0);
                    final String mLoggedUserEmail = sharedPref.getString("userEmail", null);
                    mFirebaseUsersRef = new Firebase(Constants.FIREBASE_URL_USERS).child(mLoggedUserEmail);
                    mFirebaseUsersRef.child("genero").setValue(prgmImages[position]);
                }
            });
            return rowView;
        }
    }
}