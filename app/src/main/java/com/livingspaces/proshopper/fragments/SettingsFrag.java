package com.livingspaces.proshopper.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.livingspaces.proshopper.R;

/**
 * Created by alexeyredchets on 2017-08-15.
 */

public class SettingsFrag extends BaseStackFrag {

    private static final String TAG = SettingsFrag.class.getSimpleName();

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



        return view;
    }

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.SETTINGS.title();
    }
}
