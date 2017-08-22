package com.livingspaces.proshopper.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.DataModel;
import com.livingspaces.proshopper.data.Store;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.views.LSTextView;
import com.livingspaces.proshopper.views.StoreDialog;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class AccountFrag extends BaseStackFrag implements StoreDialog.ICallback{

    private static final String TAG = AccountFrag.class.getSimpleName();

    private LSTextView tv_selectStore;
    private StoreDialog mStoreDialog;
    private View overlay;

    public static AccountFrag newInstance(){
        return new AccountFrag();
    }

    public AccountFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");

        View view = inflater.inflate(R.layout.fragment_account, container, false);
        tv_selectStore = (LSTextView)view.findViewById(R.id.tv_select_store);
        mStoreDialog = (StoreDialog)view.findViewById(R.id.dialog_store);
        mStoreDialog.setVisibility(View.GONE);
        mStoreDialog.setCallback(this);


        overlay = view.findViewById(R.id.shade_account);
        overlay.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_selectStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Select store clicked");

                NetworkManager.makeREQ(new IREQCallback() {
                    @Override
                    public void onRSPSuccess(String rsp) {

                        Store[] stores = DataModel.parseStores(rsp);
                        if (stores == null) {
                            onRSPFail();
                            return;
                        }

                        overlay(true);
                        mStoreDialog.updateAdapter(stores);
                        mStoreDialog.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onRSPFail() {

                    }

                    @Override
                    public String getURL() {
                        return "http://api.livingspaces.com/api/v1/store/getAllStores";
                    }
                });


                Toast.makeText(getContext(), "Select store clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void overlay(final boolean show) {
        if (overlay == null || (show && isViewShowing(overlay)) || (!show && !isViewShowing(overlay)) )
            return;
        Log.d(TAG, "OVERLAY");

        if (show) overlay.setVisibility(View.INVISIBLE);
        overlay.animate().setDuration(500).alpha(show ? 1 : 0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (show) overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) overlay.setVisibility(View.GONE);
            }
        }).start();
    }

    private boolean isViewShowing(View view) {
        return view.getVisibility() != View.GONE;
    }

    @Override
    public boolean setTopRight(TextView topRight) {

        topRight.setText("Logout");
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.Prefs.clearToken();
                Global.FragManager.popToHome();
            }
        });

        return true;

    }

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.ACCOUNT.title();
    }

    @Override
    public void onStoreSelected() {

    }
}
