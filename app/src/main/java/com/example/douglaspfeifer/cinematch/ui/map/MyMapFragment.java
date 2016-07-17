package com.example.douglaspfeifer.cinematch.ui.map;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.douglaspfeifer.cinematch.R;
import com.example.douglaspfeifer.cinematch.utils.Constants;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    // private OnMapReadyCallback callback;

    public MyMapFragment() {
        // Required empty public constructor
    }

    MapView mMapView;
    GoogleMap googleMap;
    private BottomSheetBehavior mBottomSheetBehavior;
    double longitude, latitude;


    SharedPreferences sharedPref;
    String mLoggedUserEmail;
    Firebase usersRef, myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPref = getContext().getSharedPreferences("settings", 0);
        mLoggedUserEmail = sharedPref.getString("userEmail", null);
        usersRef = new Firebase(Constants.FIREBASE_URL_USERS);
        myRef = usersRef.child(mLoggedUserEmail);

        View v = inflater.inflate(R.layout.fragment_map, container, false);

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
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

                    setMyPos();
                }
                return;
            }
        }
    }

    public void setMyPos()
    {

        LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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

    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)requestPermissions(LOCATION_PERMS, REQUEST_LOCATION);
        else {
            setMyPos();
            getContext().startService(new Intent(getContext(), MyLocationListenerService.class));
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        googleMap.setOnMarkerClickListener(this);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(mLoggedUserEmail, new GeoLocation(latitude, longitude));
        geoFire.setLocation("douglaspfeifer@gmail,com", new GeoLocation(latitude+0.001, longitude));

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), 2);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(key)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.me_map_icon)));
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public boolean onMarkerClick(Marker marker) {
        mBottomSheetBehavior.setPeekHeight(500);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Log.i("Marker clicked", marker.getTitle());
        return false;
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        googleMap.moveCamera(update);
        //googleMap.animateCamera(update, callback);
    }
}
