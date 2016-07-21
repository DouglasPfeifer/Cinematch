package com.example.douglaspfeifer.cinematch.ui.map;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.models.User;
import com.example.douglaspfeifer.cinematch.ui.MainActivity;
import com.example.douglaspfeifer.cinematch.ui.chat.ConversationActivity;
import com.example.douglaspfeifer.cinematch.ui.login.LoginActivity;
import com.example.douglaspfeifer.cinematch.ui.profile.OtherProfileActivity;
import com.example.douglaspfeifer.cinematch.utils.Constants;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    // private OnMapReadyCallback callback;

    public MyMapFragment() {
        // Required empty public constructor
    }


    GoogleMap googleMap;
    private BottomSheetBehavior mBottomSheetBehavior;
    double longitude, latitude;


    SharedPreferences sharedPref;
    String mLoggedUserEmail;
    Firebase usersRef, myRef;
    ImageView imageBottomSheet;

    String chatId;
    Intent i;

    private Map<String, Marker> Markers;

    private Firebase UserRefer;
    private Firebase newChatRef;
    private Firebase newUserChatRef;
    private Firebase otherUserChatRef;
    private User clickedUser;

    private Button iniciarConversaButton;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPref = getContext().getSharedPreferences("settings", 0);
        mLoggedUserEmail = sharedPref.getString("userEmail", null);
        usersRef = new Firebase(Constants.FIREBASE_URL_USERS);
        myRef = usersRef.child(mLoggedUserEmail);
        Markers = new HashMap<>();
        v = inflater.inflate(R.layout.fragment_map, container, false);

        i = new Intent(getContext(), ConversationActivity.class);
        iniciarConversaButton = (Button) v.findViewById(R.id.iniciarConversaButton);


        SupportMapFragment mSupportMapFragment;

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragmentContainer);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapFragmentContainer, mSupportMapFragment).commitAllowingStateLoss();
        }

        if (mSupportMapFragment != null)
        {
            mSupportMapFragment.getMapAsync(this);
        }


        View bottomSheet = v.findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        imageBottomSheet = (ImageView) view.findViewById(R.id.bottomProfileImage_imageView);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    };


    final static int REQUEST_LOCATION = 1340;
    final static String[] LOCATION_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setFirstSett();
                }
                return;
            }
        }
    }

    public void setMyPos()
    {
        LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if( location != null ) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();


            CameraPosition MyPos =
                    new CameraPosition.Builder().target(new LatLng(latitude, longitude))
                            .zoom(15.5f)
                            .bearing(0)
                            .tilt(25)
                            .build();
            changeCamera(CameraUpdateFactory.newCameraPosition(MyPos), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {

                }
            });
        }
    }


    public void setFirstSett()
    {
        setMyPos();
        getContext().startService(new Intent(getContext(), MyLocationListenerService.class));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geoFire");
        googleMap.setOnMarkerClickListener(this);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(mLoggedUserEmail, new GeoLocation(latitude, longitude));

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), 2);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Marker newMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(key)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.me_map_icon)));
                newMarker.hideInfoWindow();
                Markers.put(key, newMarker);
            }

            @Override
            public void onKeyExited(String key) {
                Markers.get(key).remove();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                LatLng update = new LatLng(location.latitude, location.longitude);
                Marker toUpdate = Markers.get(key);
                if( toUpdate != null )
                    toUpdate.setPosition(update);
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setInfoWindowAdapter(null);
        if(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(LOCATION_PERMS, REQUEST_LOCATION);
        else
            setFirstSett();

    }

    public boolean onMarkerClick(Marker marker) {
        clickedUser = new User();
        UserRefer = new Firebase(Constants.FIREBASE_URL_USERS).child(marker.getTitle());
        attachFirebaseListener();
        Log.i("Marker clicked", marker.getId());
        return false;
    }

    private void attachFirebaseListener () {
        // Attach an listener to read the data at our users reference
        // Firebase listener for everything that we need to get from the database
        // Every time something changes on the database, the listener will change on the profile
        UserRefer.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clickedUser = dataSnapshot.getValue(User.class);
                new DownloadImageTask(imageBottomSheet)
                        .execute(clickedUser.getProfileImageURL());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        googleMap.moveCamera(update);
        //googleMap.animateCamera(update, callback);
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

            mBottomSheetBehavior.setHideable(true);
            mBottomSheetBehavior.setPeekHeight(1000);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            CircleImageView imageGender = (CircleImageView) v.findViewById(R.id.bottomGenderImage_imageView);
            imageGender.setImageResource(clickedUser.getGenero() );
            TextView text = (TextView) v.findViewById(R.id.nameBottom);
            text.setText(clickedUser.getName());
            setButton();

        }
    }


    public void setButton () {
        usersRef.child(mLoggedUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if( clickedUser.getEmail() == null ) return;
                if (snapshot.child("chats").child(clickedUser.getEmail()).exists()) {
                    chatId = snapshot.child("chats").child(clickedUser.getEmail()).getValue().toString();
                    continuarConversa();
                }
                else {
                    iniciarConversa();
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
    public void iniciarConversa () {
        iniciarConversaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newChatRef = new Firebase(Constants.FIREBASE_URL).child("chat").push();
                newUserChatRef = usersRef.child(mLoggedUserEmail).child("chats").child(clickedUser.getEmail());
                otherUserChatRef = usersRef.child(clickedUser.getEmail()).child("chats").child(mLoggedUserEmail);
                // Get the unique ID generated by push()
                chatId = newChatRef.getKey();
                newUserChatRef.setValue(chatId);
                otherUserChatRef.setValue(chatId);
                i.putExtra("chatNode", chatId);
                i.putExtra("chatName", clickedUser.getEmail());
                startActivity(i);
            }
        });
    }
    public void continuarConversa () {
        iniciarConversaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                i.putExtra("chatNode", chatId);
                i.putExtra("chatName", clickedUser.getEmail());
                startActivity(i);
            }
        });
    }
}
