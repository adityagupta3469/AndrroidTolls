package com.example.andrroidtoll;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class Welcome extends FragmentActivity implements OnMapReadyCallback,LocationListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    //Play Services
    private static final int My_Permission_Request_Code = 7000;
    private static final int Play_Service_Res_Requesst = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiCliet;
    private Location mLastLocation;

    private static int Update_interval = 5000;
    private static int Fatest_interval = 3000;
    private static int Displacement = 10;

    DatabaseReference drivers;
    GeoFire geoFire;

    Marker mCurrent;

    MaterialAnimatedSwitch location_switch;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //Local View
        location_switch = (MaterialAnimatedSwitch)findViewById(R.id.location_switch);
        location_switch.setOnCheckedChangeListener(new MaterialAnimatedSwitch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(boolean IsOnline) {
                if(IsOnline){
                    startLocationUpdates();
                    displayLocation();
                    Snackbar.make(mapFragment.getView(),"You are online",Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    stopLocationUpdates();
                    mCurrent.remove();
                    Snackbar.make(mapFragment.getView(),"You are offline",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void stopLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiCliet, (com.google.android.gms.location.LocationListener) this);
    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliet);
        if(mLastLocation != null)
        {
            if(location_switch.isChecked())
            {
                final double latitude = mLastLocation.getLatitude();
                final double longtitude = mLastLocation.getLongitude();

                //update to Firebase
                geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longtitude), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        //Add Marker
                        if(mCurrent != null)
                            mCurrent.remove();// remove already marker
                        mCurrent = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.car)).position(new LatLng(latitude,longtitude)).title("You"));

                        //move camera to this position
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longtitude),15.0f));
                    }
                });

            }
        }
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiCliet, mLocationRequest , (com.google.android.gms.location.LocationListener) this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
