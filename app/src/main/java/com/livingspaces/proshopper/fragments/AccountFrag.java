package com.livingspaces.proshopper.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.response.Store;
//import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.views.LSTextView;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class AccountFrag extends BaseStackFrag implements StoreDialog.ICallback{

    private static final String TAG = AccountFrag.class.getSimpleName();

    private LSTextView tv_accountName, tv_selectStore, tv_storeName, tv_storeAddress, tv_storeCity, tv_storeState, tv_storeZip, tv_storeDistance_unit, tv_callBtn, tv_changeStoreBtn, tv_storeDistance;
    private StoreDialog mStoreDialog;
    private View rootView, hasStoreView, noStoreView;
    private Store mStore;
    private boolean hasStore = false, isDialogShowing = false;

    public static AccountFrag newInstance(){
        return new AccountFrag();
    }

    public AccountFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");

        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        tv_selectStore = (LSTextView)rootView.findViewById(R.id.tv_select_store);

        hasStoreView = rootView.findViewById(R.id.v_has_store);
        noStoreView = rootView.findViewById(R.id.v_no_store);
        noStoreView.setVisibility(View.GONE);

        tv_accountName = (LSTextView) rootView.findViewById(R.id.tv_account_name);
        tv_storeName = (LSTextView) rootView.findViewById(R.id.tv_storename_accountfrag);
        tv_storeAddress = (LSTextView) rootView.findViewById(R.id.tv_storeaddress_accountfrag);
        tv_storeCity = (LSTextView) rootView.findViewById(R.id.tv_storecity_accountfrag);
        tv_storeState = (LSTextView) rootView.findViewById(R.id.tv_storestate_accountfrag);
        tv_storeZip = (LSTextView) rootView.findViewById(R.id.tv_storezip_accountfrag);
        tv_storeDistance = (LSTextView) rootView.findViewById(R.id.tv_storedistance_accountfrag);
        tv_storeDistance_unit = (LSTextView) rootView.findViewById(R.id.tv_storedistanceUnit_accountfrag);
        tv_callBtn = (LSTextView) rootView.findViewById(R.id.tv_call_accountfrag);
        tv_changeStoreBtn = (LSTextView) rootView.findViewById(R.id.tv_changestore_accountfrag);

        mStoreDialog = new StoreDialog();
        mStoreDialog.setCallback(this);

        hasStore = Global.Prefs.hasStore();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (hasStore) {
            mStore = Global.Prefs.getStore();
            if (mStore == null) {
                return;
            }
            showStore();
        } else {
            chooseStore();
        }

        if (Global.Prefs.hasUserId()) {
            tv_accountName.setText(Global.Prefs.getUserId());
            tv_accountName.setVisibility(View.VISIBLE);
        } else {
            tv_accountName.setText("");
            tv_accountName.setVisibility(View.GONE);
        }

        view.findViewById(R.id.v_terms).setOnClickListener(view1 -> Global.FragManager.stackFrag(WebViewFrag.newInstance("Terms of use", Services.URL.Terms.get())));

        view.findViewById(R.id.v_policy).setOnClickListener(view1 -> Global.FragManager.stackFrag(WebViewFrag.newInstance("Privacy policy", Services.URL.StorePolicy.get())));

        view.findViewById(R.id.v_about).setOnClickListener(view1 -> Global.FragManager.stackFrag(WebViewFrag.newInstance("About out ads", Services.URL.About.get())));
    }

    private void showStore(){
        noStoreView.setVisibility(View.GONE);
        hasStoreView.setVisibility(View.VISIBLE);

        Typeface fontLight = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Light.ttf");
        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Bold.otf");
        tv_storeName.setTypeface(fontBold);
        tv_storeAddress.setTypeface(fontLight);
        tv_storeCity.setTypeface(fontLight);
        tv_storeState.setTypeface(fontLight);
        tv_storeZip.setTypeface(fontLight);
        tv_storeDistance.setTypeface(fontLight);
        tv_storeDistance_unit.setTypeface(fontLight);

        tv_storeName.setText(mStore.getName());
        tv_storeAddress.setText(mStore.getStoreAddresses().getAddress());
        tv_storeCity.setText(mStore.getStoreAddresses().getCity() + ", ");
        tv_storeState.setText(mStore.getStoreAddresses().getState() + " ");
        tv_storeZip.setText(mStore.getZipCode());

        if (mStore.getDistance() == null || mStore.getDistance().isEmpty()) {
            tv_storeDistance.setVisibility(View.INVISIBLE);
            tv_storeDistance_unit.setVisibility(View.INVISIBLE);
        }
        else {
            tv_storeDistance.setVisibility(View.VISIBLE);
            tv_storeDistance_unit.setVisibility(View.VISIBLE);
            tv_storeDistance.setText(mStore.getDistance());
        }

        tv_callBtn.setOnClickListener(onCallBtnCLicked);
        tv_changeStoreBtn.setOnClickListener(onSelectStoreClicked);
    }

    private void chooseStore(){
        hasStoreView.setVisibility(View.GONE);
        noStoreView.setVisibility(View.VISIBLE);
        tv_selectStore.setOnClickListener(onSelectStoreClicked);
    }

    @Override
    public boolean setTopRight(TextView topRight) {

        if (Global.Prefs.hasToken()){
            topRight.setText("Logout");
            topRight.setOnClickListener(view -> {
                Global.Prefs.clearToken();
                Global.Prefs.clearWishList();
                Global.FragManager.popToHome();
            });
        }
        else {
            topRight.setText("Log In");
            topRight.setOnClickListener(view -> Global.FragManager.stackFrag(LoginFrag.newInstance(false, "")));
        }

        return true;

    }

    private View.OnClickListener onSelectStoreClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isConnectedToNetwork()) {
                Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                return;
            }
            isDialogShowing = true;
            mStoreDialog.show(getFragmentManager(), "storeDialogFragment");
        }
    };

    private View.OnClickListener onCallBtnCLicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String phoneNumber = getResources().getString(R.string.phoneNumber);
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
            startActivity(intent);
        }
    };

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.ACCOUNT.title();
    }

    @Override
    public void onStoreSelected(Store store) {

        if (mStoreDialog == null) return;

        if (store == null) return;

        mStore = store;

        Global.Prefs.saveStore(store);
        hasStore = true;

        mStoreDialog.dismiss();

        showStore();
    }

    @Override
    public void onStoreCancel() {
        mStoreDialog.dismiss();
    }


    @Override
    public boolean handleBackPress() {
        if (isDialogShowing){
            mStoreDialog.dismiss();
        }
        return super.handleBackPress();
    }

    private boolean isConnectedToNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnected();
    }
}
