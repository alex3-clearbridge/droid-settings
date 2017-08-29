package com.livingspaces.proshopper;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.livingspaces.proshopper.analytics.AnalyticsApplication;
import com.livingspaces.proshopper.data.DataModel;
import com.livingspaces.proshopper.data.Store;
import com.livingspaces.proshopper.data.Token;
import com.livingspaces.proshopper.fragments.AccountFrag;
import com.livingspaces.proshopper.fragments.BaseStackFrag;
import com.livingspaces.proshopper.fragments.LoginFrag;
import com.livingspaces.proshopper.fragments.NavigationFrag;
import com.livingspaces.proshopper.interfaces.IMainFragManager;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.networking.GpsManager;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.views.ActionBar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements IMainFragManager, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActionBar actionBar;
    private Stack<BaseStackFrag> fragStack;
    private boolean hasToken = false;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int LOCATION_SERVICES_ACCESS_CODE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkManager.Init(this);
        Global.Init(this);
        Layout.Init(this);
        Init();
        InitLocationServices();
    }

    private void InitLocationServices(){

        if (!isGpsAvailable()) return;

        Log.d(TAG, "InitLocationServices: GPS is available");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // 10 sec
                .setFastestInterval(5000); // 5 sec
    }

    private void Init() {
        fragStack = new Stack<>();
        actionBar = (ActionBar) findViewById(R.id.actionbar);

        hasToken = Global.Prefs.hasToken();

        getSupportFragmentManager().beginTransaction().add(R.id.container_main, NavigationFrag.newInstance()).commit();

        if (!isGpsAvailable() && !Global.Prefs.hasStore()){
            Log.d(TAG, "No GPS and no saved store --> Call Account Page to choose store");
            Global.FragManager.stackFrag(AccountFrag.newInstance());
        }

        if (!hasToken) {
            // Not logged yet
            callLogin();
        }
        else {
            updateToken();
        }

        Utility.activity = this;
        GpsManager.getInstance().setContext(this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Utility.gaTracker = application.getDefaultTracker();
    }

    private void callLogin(){
        Log.d(TAG, "callLogin: ");
        Global.FragManager.stackFrag(LoginFrag.newInstance());
    }

    private void updateToken(){
        Log.d(TAG, "updateToken: ");
        NetworkManager.refreshTokenREQ(new IREQCallback() {
            @Override
            public void onRSPSuccess(String rsp) {
                Log.d(TAG, "onRSPSuccess");

                if (rsp.contains("access_token") && (rsp.contains("refresh_token"))) {
                    Token token = new Token(rsp);
                    Global.Prefs.editToken(token.access_token, token.refresh_token, token.userName);
                }
                else {
                    onRSPFail();
                }
            }

            @Override
            public void onRSPFail() {
                // Probably refresh token is expired. Ask user to login
                Log.d(TAG, "onRSPFail");
                callLogin();
            }

            @Override
            public String getURL() {
                return Services.API.Token.get();
            }
        });
    }

    private void updateViewsForFrag() {
        Log.d(TAG, "updateViewsForFrag");
        if (fragStack.isEmpty()){
            Log.d(TAG, "fragStack.isEmpty()");
            actionBar.update(null);
        }
        else {
            Log.d(TAG, "fragStack.is not Empty()");
            actionBar.update(fragStack.peek());
            fragStack.peek().onResurface();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: " + Integer.toString(requestCode));
        if (!fragStack.isEmpty() && requestCode != 161)
            fragStack.peek().onRequestPermissionsResult(requestCode, permissions, grantResults);
        else {
            if (requestCode == LOCATION_SERVICES_ACCESS_CODE){
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e(TAG, "onRequestPermissionsResult GRANTED");
                    // permission was granted, yay! Do the
                    // camera-related task you need to do.


                } else {
                    Log.e(TAG, "onRequestPermissionsResult DENIED");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;

            }
        }
    }

    @Override
    public FragmentManager getFragMan() {
        return getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (!fragStack.isEmpty() && fragStack.peek().handleBackPress()){
            Log.d(TAG, "fragStack is not Empty() & handleBackPress");
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            Log.d(TAG, "getBackStackEntryCount >= 1");
            popFrag(false);
        }
        else {
            Log.d(TAG, "super.OnBackPress");
            super.onBackPressed();
        }
    }

    @Override
    public void refreshActionBar() {
        Log.d(TAG, "refreshActionBar");
        if (!fragStack.isEmpty()){
            Log.d(TAG, "fragStack is not Empty()");
            actionBar.update(fragStack.peek());
        }
    }

    @Override
    public boolean popFrag(boolean quickPop) {
        Log.d(TAG, "popFrag");
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Log.d(TAG, "getBackStackEntryCount() == 0");
            return false;
        }
        getSupportFragmentManager().popBackStackImmediate();

        BaseStackFrag poppedFrag = fragStack.pop();
        Log.d(TAG, "Popped Fragment :: " + poppedFrag.getTitle());

        if (!quickPop) {
            Log.d(TAG, "!quickPop");
            updateViewsForFrag();
        }

        return true;
    }

    @Override
    public void popToFrag(String fragTitle) {
        Log.d(TAG, "popToFrag");
        while (!fragStack.isEmpty() && !fragStack.peek().getTitle().equals(fragTitle) && popFrag(true))
            ;
        updateViewsForFrag();
    }

    @Override
    public void popToHome() {
        Log.d(TAG, "popToHome");
        while (popFrag(true)) ;
        updateViewsForFrag();
    }

    @Override
    public void stackFrag(final BaseStackFrag frag) {
        Log.d(TAG, "stackFrag");
        if (fragStack.size() > 0 && fragStack.peek().getClass().equals(frag.getClass())) {
            Log.d(TAG, "fragStack.size() > 0 && fragStack.peek().getClass().equals(frag.getClass())");
            return;
        }
        Log.d(TAG, "set NotTouchable");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        new Handler().post(new Runnable() {
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
                        .add(R.id.container_main, frag)
                        .addToBackStack(frag.getTitle())
                        .commitAllowingStateLoss();

                fragStack.push(frag);
                Log.d(TAG, "Pushed fragment :: " + frag.getTitle());
                actionBar.post(new Runnable() {
                    @Override
                    public void run() {
                        actionBar.update(frag);
                    }
                });
            }
        });
    }

    @Override
    public void startFrag(BaseStackFrag frag) {
        Log.d(TAG, "startFrag");
        popToHome();
        stackFrag(frag);
    }

    @Override
    public void swapFrag(BaseStackFrag frag) {
        Log.d(TAG, "swapFrag");
        popFrag(true);
        stackFrag(frag);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: Location services connected");

        if (canAccessLocation()){
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null){
                Log.d(TAG, "onConnected: location == null");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest,
                        this);
            }
            else {
                handleNewLocation(location);
            }
        }
        else {
            requestPermission();
        }
    }

    private boolean isGpsAvailable(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean canAccessLocation(){
        return ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)){
            LOCATION_SERVICES_ACCESS_CODE = 161;
            Toast.makeText(this, "Should have access to location Services", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_SERVICES_ACCESS_CODE);
        }
        else {
            LOCATION_SERVICES_ACCESS_CODE = 161;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_SERVICES_ACCESS_CODE);
        }
    }

    private void handleNewLocation(Location location){
        Double currentLatitude = location.getLatitude();
        Double currentLongitude = location.getLongitude();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String zip = "";

        if (addressList != null && addressList.size() != 0){
            Address address = addressList.get(0);
            zip = address.getPostalCode();
            zip = zip.replaceAll("\\s+", "");
            Global.Prefs.saveUserZip(zip);
        }

        Global.Prefs.saveUserZip(zip);

        findClosestStore(location);
    }

    private void findClosestStore(Location location){
        Log.d(TAG, "findClosestStore: ");

        NetworkManager.makeREQ(new IREQCallback() {
            @Override
            public void onRSPSuccess(String rsp) {
                Log.d(TAG, "onRSPSuccess: ");
                Store[] stores = DataModel.parseStores(rsp);
                if (stores == null) {
                    onRSPFail();
                    return;
                }
                double currentDis;
                double min = 7000;
                Store closestStore = new Store();

                for (Store store : stores) {
                    Location storeLocation = new Location("");
                    storeLocation.setLatitude(Double.parseDouble(store.getLatitude()));
                    storeLocation.setLongitude(Double.parseDouble(store.getLongitude()));

                    currentDis = location.distanceTo(storeLocation)/1000;

                    if (currentDis < min) {
                        min = currentDis;
                        closestStore = store;
                        Log.d(TAG, "onRSPSuccess: Current min distance :: " + store.getName() + String.valueOf(currentDis));
                    }
                }

                Global.Prefs.saveStore(closestStore);
            }

            @Override
            public void onRSPFail() {
                Log.d(TAG, "onRSPFail: ");
            }

            @Override
            public String getURL() {
                return "http://api.livingspaces.com/api/v1/store/getAllStores";
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
         /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        9000);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
                Log.e(TAG, "onConnectionFailed: ", e);
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG,"Location services connection failed with code "
                    + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
        handleNewLocation(location);
    }
}
