package com.livingspaces.proshopper.interfaces;

/**
 * Created by rugvedambekar on 15-09-25.
 */
public interface IREQCallback {
    void onRSPSuccess(String rsp);
    void onRSPFail();

    String getURL();
}