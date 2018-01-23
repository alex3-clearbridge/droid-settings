/*
package com.livingspaces.proshopper.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.response.Store;
import com.livingspaces.proshopper.networking.GpsManager;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.views.LSTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapViewFrag extends BaseStackFrag {
    private static final String TAG = MapViewFrag.class.getSimpleName();
    private int MAP_LOCATION_REQUEST_CODE = 112;

    public interface Callback {
        void onTouchMapFrag();
    }

    private MyStore myStore = new MyStore();
    private List<Store> all_stores = new ArrayList<>();

    private RelativeLayout storeView;
    private Activity mActivity;
    private Callback callback;

    private float mAccuracyLevel = 3000;  // 3 km radius
    private float mZoomLevel = 11;
    private Location mLocation;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFragment;
    private View mapSearchOverlay;

    private int markerClicked = 0;
    private boolean isFirstLaunch = true;
    private boolean showCard = false;

    private int storeCardLevel = 0;
    private LatLng previousCameraLocation;

    public MapViewFrag() {
    }

    public static MapViewFrag newInstance(Store store, Callback callback) {
        MapViewFrag mvf = new MapViewFrag(callback);
        if (store != null) mvf.myStore.Init(store);
        return mvf;
    }

    public MapViewFrag(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Log.e(TAG, "--------------------");
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);
        myStore.Init(rootView, inflater);
        mActivity = getActivity();

        isFirstLaunch = true;
        storeView.setOnClickListener(v -> {
            Log.e("storeView", "storeCardLevel: " + Integer.toString(storeCardLevel % 4));
            if (storeCardLevel % 4 >= 2) {
                myStore.show(3);
            } else {
                myStore.show(2);
            }

            storeCardLevel++;
        });

        mapSearchOverlay = rootView.findViewById(R.id.mapSearchOverlay);
        initializeMap();
        return rootView;
    }

    private SupportMapFragment getGoogleMapFragment() {
        Log.d(TAG, "getMapFragment");
        FragmentManager fm = getChildFragmentManager();
        return (SupportMapFragment) fm.findFragmentById(R.id.map);
    }

    public void initializeMap() {
        Log.d(TAG, "initializeMap");

        mapFragment = getGoogleMapFragment();
        mGoogleMap = mapFragment.getMap();

        if (mGoogleMap != null) {
            setMapType(GoogleMap.MAP_TYPE_NORMAL);

            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.setTrafficEnabled(false);
            mGoogleMap.setBuildingsEnabled(false);
            mGoogleMap.setIndoorEnabled(false);
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        }

        if (canAccessLocation()) {
            Log.e(TAG, "Access Granted");

            // Access Granted
            GpsManager.getInstance().startTracking();
            centerMapCamera();
        } else {

            if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e(TAG, "Location Access Denied");

                // Access Denied
                centerOnZip();
                setStoreMarkers();
            } else {
                MAP_LOCATION_REQUEST_CODE = 112;
                Log.e(TAG, "requestPermissions: " + Integer.toString(MAP_LOCATION_REQUEST_CODE));

                // Request Permission
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MAP_LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e(TAG, "onRequestPermissionsResult: " + Integer.toString(requestCode));
        Log.e(TAG, "GrantResults: " + Integer.toString(grantResults[0])
                + " Permissions: " + permissions[0]
        );

        if (requestCode == MAP_LOCATION_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.e(TAG, "onRequestPermissionsResult GRANTED");
                // permission was granted, yay! Do the
                // map-related task you need to do.

                GpsManager.getInstance().startTracking();
                centerMapCamera();
            } else {
                Log.e(TAG, "onRequestPermissionsResult DENIED");

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                centerOnZip();
                setStoreMarkers();
            }
            return;
        }
    }

    private boolean canAccessLocation() {
        return ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void centerMapCamera() {
        mLocation = GpsManager.getInstance().getLastLocation();
        if (mLocation == null) {
            centerOnZip();
        } else {
            if (previousCameraLocation == null) {
                centerOnCurrentLocation();
            } else {
                centerOnPreviousLocation();
            }
        }

        setStoreMarkers();
    }

    public void centerOnZip() {
        Log.e(TAG, "CENTER ON ZIP");
        double latitude, longitude;

        if (myStore != null && myStore.currStore != null) {
            latitude = Double.parseDouble(myStore.currStore.getLatitude());
            longitude = Double.parseDouble(myStore.currStore.getLongitude());

            Log.e(TAG, "CENTER on Store: " + myStore.currStore.getName() + " @ "
                    + myStore.currStore.getLatitude() + ", " + myStore.currStore.getLongitude());
        } else {
            Log.e(TAG, " CENTER on IRVINE ");
            // default location to Irvine, CA
            latitude = 33.64;
            longitude = -117.74;
        }

        if (mGoogleMap != null) {
            previousCameraLocation = new LatLng(latitude, longitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    previousCameraLocation, mZoomLevel));
        }
    }

    */
/**
     * Center the camera on the current location. It uses the default zoom storeCardLevel
     * for the camera, and it uses the default accuracy storeCardLevel for getting
     * current location.
     *//*

    public void centerOnCurrentLocation() {
        if (mGoogleMap == null || mActivity == null || mLocation == null) {
            return;
        }
        Log.e(TAG, "CENTER ON CURRENT LOCATION");

        LatLng currentPosition = new LatLng(
                mLocation.getLatitude(), mLocation.getLongitude());

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                currentPosition, mZoomLevel));
    }

    public void centerOnPreviousLocation() {
        if (mGoogleMap == null || mActivity == null || mLocation == null || previousCameraLocation == null) {
            return;
        }
        Log.e(TAG, "CENTER ON previous LOCATION: "
                + Double.toString(previousCameraLocation.latitude)
                + " : "
                + Double.toString(previousCameraLocation.longitude)
        );

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                previousCameraLocation, mZoomLevel));
    }

    private void centerOnStore(LatLng markerPosition) {
        if (mGoogleMap == null) return;

        Log.e(TAG, "CENTER on STORE");


        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                markerPosition, mZoomLevel));

//        // Prevents the store card from flipping up & down since camera moves twice
        markerClicked = 1;

        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(
                mZoomLevel), 200, null);
        previousCameraLocation = markerPosition;
    }

    private void setStoreMarkers() {
        Log.d(TAG, "setStoreMarkers: ");
        if (mGoogleMap == null) return;  // Must have Google Maps

        if (all_stores != null && all_stores.size() > 0) {
            clearMap();
            List<MarkerOptions> markerOptionsList = new ArrayList<>();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ls_m_img_mapview);

            int index = 0;
            for (Store s : all_stores) {
                LatLng store_location = new LatLng(
                        Double.parseDouble(s.getLatitude()),
                        Double.parseDouble(s.getLongitude()));

                markerOptionsList.add(new MarkerOptions()
                        .position(store_location)
                        .snippet("" + index)
                        .icon(icon));

                ++index;
            }
            addMarkerList(markerOptionsList);
        }

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.e(TAG, "Moving Camera to: "
                        + Double.toString(cameraPosition.target.latitude)
                        + " : "
                        + Double.toString(cameraPosition.target.longitude)
                );


                if (isFirstLaunch) {
                    myStore.showIgnoreAnimation(0);
                } else {
                    Log.e(TAG, "markerClicked: " + Integer.toString(markerClicked));
                    if (markerClicked <= 0) {
                        if (showCard) {
                            // Show store card after entering zip code
                            myStore.show(2);
                            showCard = false;
                        } else {
                            // Show City Name when panning
                            myStore.show(1);
                        }
                    }
                    markerClicked--;
                }

                previousCameraLocation = cameraPosition.target;
                callback.onTouchMapFrag();
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!isFirstLaunch) {
                    myStore.show(1);
                }
                callback.onTouchMapFrag();
            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int storeNumber = Integer.parseInt(marker.getSnippet());
                Store store = all_stores.get(storeNumber);
                LatLng markerPosition = marker.getPosition();

                Log.d(TAG, "Clicked on storeNumber: " + marker.getSnippet() + " - " + all_stores.get(storeNumber).getName());
                Log.d(TAG, "Lat: " + Double.toString(markerPosition.latitude) + " Long: " + Double.toString(markerPosition.longitude));

                centerOnStore(markerPosition);
                markerClicked++;
                isFirstLaunch = false;
                myStore.Init(store);
                myStore.show(2);
                return false;
            }
        });
    }

    private void clearMap() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();
        }
    }

    */
/**
     * Set the map type for Google Map. Available values for mapType are:
     * GoogleMap.MAP_TYPE_NONE = 0
     * GoogleMap.MAP_TYPE_NORMAL = 1
     * GoogleMap.MAP_TYPE_SATELLITE = 2
     * GoogleMap.MAP_TYPE_TERRAIN = 3
     * GoogleMap.MAP_TYPE_HYBRID = 4
     *
     * @param The type of map to display.
     *//*

    public void setMapType(int mapType) {
        if (mGoogleMap == null)
            return;
        mGoogleMap.setMapType(mapType);
    }

    */
/**
     * Add 1 marker to the map using marker options.
     *
     * @param A marker options object that defines how to render the marker.
     *//*

    public void addMarker(MarkerOptions markerOptions) {
        if (mGoogleMap == null)
            return;
        mGoogleMap.addMarker(markerOptions);
    }

    */
/**
     * Add a list of markers to the map using marker options list.
     *
     * @param A marker options list that defines how to render the markers.
     *//*

    public void addMarkerList(List<MarkerOptions> markerOptionsList) {
        int markerCount;
        if (markerOptionsList == null || (markerCount = markerOptionsList.size()) == 0) return;

        for (int i = 0; i < markerCount; i++) {
            addMarker(markerOptionsList.get(i));
        }
    }

    @Override
    public String getTitle() {
        return "Find A Store";
    }

    public void setStore(List<Store> store) {
        Log.d(TAG, "setStore: ");
        if (store != null) {
            myStore.Init(store.get(0));

            all_stores.clear();
            all_stores = store;
            if (isFirstLaunch) {
                centerOnCurrentLocation();
            } else {
                centerOnZip();
            }
            setStoreMarkers();
        }
    }

    public void updateStoreList() {
        Log.d(TAG, "updateStoreList");
        isFirstLaunch = false;
        showCard = true;
    }

    public void showOverlay() {
        mapSearchOverlay.setVisibility(View.VISIBLE);
    }

    public void hideOverlay() {
        mapSearchOverlay.setVisibility(View.GONE);
    }


    */
/**
     * StoreLevel:
     * 0: HIDDEN
     * 1: CITY
     * 2: STORE_ADDRESS
     * 3: STORE_HRS
     *//*

    private class MyStore {

        private Store currStore;
        private float storeYTop[] = new float[4];

        private View storeHrsView;
        private View tv_storeCall, tv_storeDir;
        private View tv_storeNum_top, tv_storeNum_bottom;
        private TextView tv_storeTitle, tv_storeDist;
        private LSTextView tv_storeOpenTill;
        private View btn_centerMap;
        private View iv_storeHrsOverlay;

        private void Init(Store store) {
            currStore = store;
            if (storeView == null) return;

            tv_storeTitle.setText(currStore.getName());
            ((TextView) storeView.findViewById(R.id.tv_storeAddr)).setText(currStore.getStoreAddresses().getAddress());
            ((TextView) storeView.findViewById(R.id.tv_storeCityStateZIP)).setText(currStore.getStoreAddresses().getZipCode());

            TextView tv_storeDist = ((TextView) storeView.findViewById(R.id.tv_storeDist));
            if (currStore.getDistance() == null) tv_storeDist.setVisibility(View.GONE);
            else tv_storeDist.setText(currStore.getDistance());


            // Store Hour Message
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            final int HOUR = cal.get(Calendar.HOUR_OF_DAY);

            if (HOUR >= 10 && HOUR < 21) {
                tv_storeOpenTill.setTextColor(ContextCompat.getColor(mActivity, R.color.OpenUntilText));
                tv_storeOpenTill.setText("Open Until 9:00 PM");
            } else {
                tv_storeOpenTill.setTextColor(ContextCompat.getColor(mActivity, R.color.ClosedText));
                tv_storeOpenTill.setText("Closed");
            }


            if (tv_storeCall != null) {
                tv_storeCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumber = getResources().getString(R.string.phoneNumber);
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                        startActivity(intent);
                    }
                });
            }
            if (tv_storeDir != null) {
                tv_storeDir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utility.openGoogleMaps(currStore.getLatitude(), currStore.getLongitude(), currStore.getStoreAddresses().getAddress());
                    }
                });
            }

            if (btn_centerMap != null) {
                btn_centerMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currStore != null) {
                            double latitude = Double.parseDouble(currStore.getLatitude());
                            double longitude = Double.parseDouble(currStore.getLongitude());
                            LatLng markerPosition = new LatLng(latitude, longitude);
                            centerOnStore(markerPosition);
                            markerClicked++;
                        }
                    }
                });
            }
        }

        private void Init(View rootView, LayoutInflater inflater) {
            storeView = (RelativeLayout) rootView.findViewById(R.id.rl_store);
            storeView.setBackgroundResource(R.drawable.item_store_background);
            Layout.setPadding(storeView, 10);

            storeHrsView = inflater.inflate(R.layout.item_store_hrs, null);

            storeView.setVisibility(View.INVISIBLE);
            storeHrsView.setVisibility(View.INVISIBLE);

            RelativeLayout.LayoutParams storeHrsParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            storeHrsParams.addRule(RelativeLayout.BELOW, R.id.layout_dirCallButtons);
            storeHrsView.setPadding(5, Layout.dpToPx(10), 5, 0);
            storeHrsView.setLayoutParams(storeHrsParams);

            tv_storeTitle = (TextView) storeView.findViewById(R.id.tv_storeTitle);
            tv_storeOpenTill = (LSTextView) storeView.findViewById(R.id.tv_storeOpenTill);
            tv_storeDist = (TextView) storeView.findViewById(R.id.tv_storeDist);
            tv_storeCall = storeView.findViewById(R.id.tv_storeCall);
            tv_storeDir = storeView.findViewById(R.id.tv_storeDir);
            btn_centerMap = storeView.findViewById(R.id.btn_centerMap);

            tv_storeNum_top = storeHrsView.findViewById(R.id.tv_storeNum_top);
            tv_storeNum_bottom = storeHrsView.findViewById(R.id.tv_storeNum_bottom);
            iv_storeHrsOverlay = storeHrsView.findViewById(R.id.iv_storeHrsOverlay);

            tv_storeNum_top.setVisibility(View.VISIBLE);
            tv_storeNum_bottom.setVisibility(View.GONE);
            iv_storeHrsOverlay.setVisibility(View.GONE);

            storeView.addView(storeHrsView);
            storeView.post(new Runnable() {
                @Override
                public void run() {
                    initValues();
                    if (currStore != null) Init(currStore);
                }
            });
        }

        private void initValues() {
            if (storeView == null) return;

            storeYTop[3] = storeView.getY();
            storeYTop[0] = storeYTop[3] + storeView.getHeight();

            View v_sTitle = storeView.findViewById(R.id.tv_storeTitle);
            View v_sHrs = storeView.findViewById(R.id.rl_storeHours);

            v_sHrs.setPadding(0, 0, 0, 15);

            int height_store = storeView.getHeight() - storeView.getPaddingTop() - storeView.getPaddingBottom();
            int height_sTitle = v_sTitle.getHeight();
            int height_sHrs = v_sHrs.getHeight() + 2 * v_sHrs.getPaddingTop();

            storeYTop[2] = storeYTop[3] + (height_store - height_sHrs);
            storeYTop[1] = storeYTop[3] + (height_store - height_sTitle);
        }

        public void show(int level) {
            if (level < 0 || level > 3) return;

            storeCardLevel = level;
            storeView.setVisibility(View.VISIBLE);
            storeHrsView.setVisibility(View.VISIBLE);

            if (level <= 1) {
                tv_storeTitle.setPadding(15, 5, 5, 15);
                tv_storeDist.setPadding(15, 5, 5, 15);
            } else {
                tv_storeTitle.setPadding(15, 20, 5, 0);
                tv_storeDist.setPadding(15, 20, 5, 0);
            }

            storeView.setVisibility(View.VISIBLE);
            storeView.animate().setDuration(250).y(storeYTop[level]).start();
        }

        public void showIgnoreAnimation(int level) {
            if (level < 0 || level > 3) return;

            storeCardLevel = level;
            storeView.setVisibility(View.VISIBLE);
            storeHrsView.setVisibility(View.VISIBLE);


            if (level <= 1) {
                tv_storeTitle.setPadding(15, 5, 5, 15);
                tv_storeDist.setPadding(15, 5, 5, 15);
            } else {
                tv_storeTitle.setPadding(15, 20, 5, 0);
                tv_storeDist.setPadding(15, 20, 5, 0);
            }
            storeView.setVisibility(View.VISIBLE);
            storeView.setY(storeYTop[level]);
        }
    }
}*/
