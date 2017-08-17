package com.livingspaces.proshopper;

import com.livingspaces.proshopper.interfaces.ILogin;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public class LoginUtility {

    public static ILogin callback;
    public static ILogin FragManager;

    public static void Init(LoginActivity activity){
        callback = activity;
        FragManager = activity;
    }

}
