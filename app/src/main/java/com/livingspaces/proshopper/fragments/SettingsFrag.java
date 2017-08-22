package com.livingspaces.proshopper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class SettingsFrag extends BaseStackFrag {

    private static final String TAG = SettingsFrag.class.getSimpleName();

    private View v_terms, v_policy, v_about;

    public static SettingsFrag newInstance(){
        return new SettingsFrag();
    }

    public SettingsFrag() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        v_terms = view.findViewById(R.id.v_terms);
        v_policy = view.findViewById(R.id.v_policy);
        v_about = view.findViewById(R.id.v_about);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        v_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Global.FragManager.stackFrag(WebViewFrag.newInstance("Terms of use", Services.URL.Terms.get()));

            }
        });

        v_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.FragManager.stackFrag(WebViewFrag.newInstance("Privacy policy", Services.URL.Policy.get()));
            }
        });

        v_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.FragManager.stackFrag(WebViewFrag.newInstance("About out ads", Services.URL.About.get()));
            }
        });

    }

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.SETTINGS.title();
    }

}
