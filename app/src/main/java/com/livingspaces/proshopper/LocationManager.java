package com.livingspaces.proshopper;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by alexeyredchets on 2017-08-28.
 */

public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, MainActivity.LocationCallback {

    private static final String TAG = LocationManager.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private LocationManager mLocationManager;
    private MainActivity mMainActivity;
    private int LOCATION_SERVICES_ACCESS_CODE;

    private LocationManager(Context context, MainActivity activity){

        mMainActivity = activity;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // 10 sec
                .setFastestInterval(5000); // 5 sec
    }

    public void Initialize(Context context, MainActivity activity){
        mLocationManager = new LocationManager(context, activity);
    }

    @Override
    public void onActivityResume() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityPause() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: Location services connected");

        if (canAccessLocation()){

        }
        else {
            requestPermission();
        }
    }

    private boolean canAccessLocation(){
        return ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(mMainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(mContext, "Should have access to location Services", Toast.LENGTH_SHORT).show();
        }
        else {
            LOCATION_SERVICES_ACCESS_CODE = 133;
            ActivityCompat.requestPermissions(mMainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_SERVICES_ACCESS_CODE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
