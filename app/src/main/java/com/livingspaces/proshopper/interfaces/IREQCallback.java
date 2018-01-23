package com.livingspaces.proshopper.interfaces;

import org.json.JSONObject;

/**
 * Created by rugvedambekar on 15-09-25.
 */
public interface IREQCallback {
    void onRSPSuccess(String rsp);
    void onRSPFail();

    String getURL();
}