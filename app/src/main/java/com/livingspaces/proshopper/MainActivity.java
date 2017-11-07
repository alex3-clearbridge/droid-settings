package com.livingspaces.proshopper;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.livingspaces.proshopper.data.response.CustomerInfoResponse;
import com.livingspaces.proshopper.data.response.Store;
import com.livingspaces.proshopper.fragments.AccountFrag;
import com.livingspaces.proshopper.fragments.BaseStackFrag;
import com.livingspaces.proshopper.fragments.LoginFrag;
import com.livingspaces.proshopper.fragments.NavigationFrag;
import com.livingspaces.proshopper.interfaces.IMainFragManager;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.networking.GpsManager;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.data.response.LoginResponse;
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

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ActionBar actionBar;
    private Stack<BaseStackFrag> fragStack;
    private int LOCATION_SERVICES_ACCESS_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Network.Init(this);
        Global.Init(this);
        Layout.Init(this);
        Init();

        InitLocationServices();
    }

    private void Init() {
        fragStack = new Stack<>();
        actionBar = (ActionBar) findViewById(R.id.actionbar);

        getSupportFragmentManager().beginTransaction().add(R.id.container_main, NavigationFrag.newInstance()).commit();

        if (!isGpsAvailable() && !Global.Prefs.hasStore()) {
            Log.d(TAG, "No GPS --> Call Account Page to choose store");
            Global.FragManager.stackFrag(AccountFrag.newInstance());
        }

        if (!Global.Prefs.hasToken()) {
            // Not logged yet
            Global.FragManager.stackFrag(LoginFrag.newInstance(false, ""));
        } else {
            if (isConnectedToNetwork()) {
                updateToken();
                getCustomerInfo();
            }
        }

        Utility.activity = this;
        GpsManager.getInstance().setContext(this);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Utility.gaTracker = application.getDefaultTracker();
    }

    private void InitLocationServices() {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // permission granted
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000) // 10 sec
                    .setFastestInterval(5000); // 5 sec
        } else {
            // ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                LOCATION_SERVICES_ACCESS_CODE = 161;
                Toast.makeText(this, "Should have access to location Services", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_SERVICES_ACCESS_CODE);
            } else {
                LOCATION_SERVICES_ACCESS_CODE = 161;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_SERVICES_ACCESS_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!fragStack.isEmpty() && requestCode != 161)
            fragStack.peek().onRequestPermissionsResult(requestCode, permissions, grantResults);
        else {
            if (requestCode == LOCATION_SERVICES_ACCESS_CODE) {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    InitLocationServices();
                } else {
                    // permission denied --> do nothing
                }
            }
        }
    }

    private void updateToken() {
        Network.makeRefreshTokenREQ(new IRequestCallback.Login() {
            @Override
            public void onSuccess(LoginResponse response) {
                if (response.getAccess_token() != null
                        && response.getRefresh_token() != null
                        && response.getUser_name() != null) {
                    Global.Prefs.editToken(response.getAccess_token(), response.getRefresh_token(), response.getUser_name());
                    actionBar.updateCartCount();
                } else {
                    onFailure("null message");
                }
            }

            @Override
            public void onFailure(String message) {
                // Probably refresh token is expired. Ask user to login
                Global.Prefs.clearToken();
                Global.FragManager.stackFrag(LoginFrag.newInstance(false, ""));
            }
        });
    }

    private void getCustomerInfo() {
        Network.makeGetInfoREQ(new IRequestCallback.Customer() {
            @Override
            public void onSuccess(CustomerInfoResponse response) {
                if (response.getShippingAddress().getZipCode() != null && !response.getShippingAddress().getZipCode().isEmpty()) {
                    Global.Prefs.saveUserZip(response.getShippingAddress().getZipCode());
                }
            }

            @Override
            public void onFailure(String message) {
                // error --> do nothing
            }
        });
    }

    private void updateViewsForFrag() {
        if (fragStack.isEmpty()) {
            actionBar.update(null);
        } else {
            actionBar.update(fragStack.peek());
            fragStack.peek().onResurface();
        }
    }

    @Override
    public FragmentManager getFragMan() {
        return getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        if (!fragStack.isEmpty() && fragStack.peek().handleBackPress()) {
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            popFrag(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void refreshActionBar() {
        if (!fragStack.isEmpty()) {
            actionBar.update(fragStack.peek());
        }
    }

    @Override
    public boolean popFrag(boolean quickPop) {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return false;
        }
        getSupportFragmentManager().popBackStackImmediate();
        BaseStackFrag poppedFrag = fragStack.pop();
        Log.d(TAG, "Popped Fragment :: " + poppedFrag.getTitle());
        if (!quickPop) {
            updateViewsForFrag();
        }
        return true;
    }

    @Override
    public void popToFrag(String fragTitle) {
        while (!fragStack.isEmpty() && !fragStack.peek().getTitle().equals(fragTitle) && popFrag(true));
        updateViewsForFrag();
    }

    @Override
    public void popToHome() {
        while (popFrag(true)) ;
        updateViewsForFrag();
    }

    @Override
    public void stackFrag(final BaseStackFrag frag) {
        if (fragStack.size() > 0 && fragStack.peek().getClass().equals(frag.getClass())) {
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        new Handler().post(() -> {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
                    .add(R.id.container_main, frag)
                    .addToBackStack(frag.getTitle())
                    .commitAllowingStateLoss();

            fragStack.push(frag);
            actionBar.post(() -> actionBar.update(frag));
        });
    }

    @Override
    public void startFrag(BaseStackFrag frag) {
        popToHome();
        stackFrag(frag);
    }

    @Override
    public void swapFrag(BaseStackFrag frag) {
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
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
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

    private boolean isGpsAvailable(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void handleNewLocation(Location location){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String zip = "";
        if (addressList != null && addressList.size() != 0){
            Address address = addressList.get(0);
            zip = address.getPostalCode();
            if (zip != null){
                zip = zip.replaceAll("\\s+", "");
                findClosestStore(zip);
            }
        }
    }

    private void findClosestStore(String zip){
        Network.makeGetStoreByZip(zip, new IRequestCallback.Stores() {
            @Override
            public void onSuccess(List<Store> storeList) {
                if (storeList == null) {
                    onFailure("null message");
                    return;
                }

                Global.Prefs.saveStore(storeList.get(0));
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "makeGetStoresREQ::onFailure: ");
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

    private boolean isConnectedToNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!Global.Prefs.hasToken() && Global.Prefs.hasUserZip()){
            Global.Prefs.removeUserZip();
        }
    }
}