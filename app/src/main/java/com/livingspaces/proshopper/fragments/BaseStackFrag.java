package com.livingspaces.proshopper.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rugvedambekar on 15-09-21.
 */
public abstract class BaseStackFrag extends DialogFragment {

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
    public boolean setTopRightUp(View topRightUp) { return false; }

    public boolean setTvTopRightUp(TextView tv_topRightUp){
        return false;
    }

    public boolean handleBackPress() { return false; }

}
