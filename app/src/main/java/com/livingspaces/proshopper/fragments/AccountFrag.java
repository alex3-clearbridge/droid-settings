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

        mStoreDialog = new StoreDialog();
        mStoreDialog.setCallback(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_selectStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Select store clicked");

                mStoreDialog.show(getFragmentManager(), "storeDialogFragment");

            }
        });
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
