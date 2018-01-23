package com.livingspaces.proshopper.interfaces;

import android.support.v4.app.FragmentManager;

import com.livingspaces.proshopper.fragments.BaseStackFrag;

/**
 * Created by rugvedambekar on 15-09-21.
 */
public interface IMainFragManager {
    FragmentManager getFragMan();
    void onBackPressed();
    void refreshActionBar();

    void startFrag(BaseStackFrag frag);
    void stackFrag(BaseStackFrag frag);
    void swapFrag(BaseStackFrag frag);

    void popToFrag(String fragTitle);
    void popToHome();

    boolean popFrag(boolean quickPop);
}
