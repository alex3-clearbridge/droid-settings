package com.livingspaces.proshopper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.views.LSTextView;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class AccountFrag extends BaseStackFrag {

    private static final String TAG = AccountFrag.class.getSimpleName();

    private LSTextView tv_selectStore;

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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_selectStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Select store clicked");

                Toast.makeText(getContext(), "Select store clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean setTopRight(TextView topRight) {

        topRight.setText("Logout");
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Logout Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return true;

    }

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.ACCOUNT.title();
    }
}
