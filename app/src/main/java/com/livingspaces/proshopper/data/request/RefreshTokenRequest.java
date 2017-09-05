package com.livingspaces.proshopper.data.request;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by alexeyredchets on 2017-08-30.
 */

public class RefreshTokenRequest {

    private String grant_type;
    private String refresh_token;
    private String Client_id;
    private String Client_secret;

    public RefreshTokenRequest(String refresh_token) {
        this.refresh_token = refresh_token;
        this.grant_type = "refresh_token";
        this.Client_id = "mobileapidev.livingspaces.com";
        this.Client_secret = "lsfsecret";
    }

    public Map<String, String> refreshTokenMap(){
        Map<String, String> params = new Hashtable<>();
        params.put("grant_type", grant_type);
        params.put("Client_id", Client_id);
        params.put("Client_secret", Client_secret);
        params.put("refresh_token", refresh_token);
        return params;
    }

}
