package com.livingspaces.proshopper.interfaces;

import android.support.v4.app.FragmentManager;

import com.livingspaces.proshopper.fragments.BaseLoginFrag;
import com.livingspaces.proshopper.fragments.BaseStackFrag;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public interface ILogin {
    FragmentManager getFragMan();
    void onBackPressed();
    void refreshActionBar();

    void startFrag(BaseLoginFrag frag);
    void stackFrag(BaseLoginFrag frag);
    void swapFrag(BaseLoginFrag frag);

    void popToFrag(String fragTitle);
    void popToHome();

    boolean popFrag(boolean quickPop);
}
