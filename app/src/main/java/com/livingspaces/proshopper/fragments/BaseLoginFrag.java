package com.livingspaces.proshopper.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public abstract class BaseLoginFrag extends Fragment {

    public abstract String getTitle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void onResurface() { }
    public boolean setTopRight(TextView topRight) { return false; }
    public boolean setTopRight(ImageView topRight) { return false; }
    public boolean setTopRightEx(ImageView topRightEx) { return false; }

    public boolean handleBackPress() { return false; }
}
