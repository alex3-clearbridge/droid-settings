package com.livingspaces.proshopper.data.request;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by alexeyredchets on 2017-08-30.
 */

public class LoginRequest {

    private String grant_type;
    private String username;
    private String password;
    private String Client_id;
    private String Client_secret;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
        this.grant_type = "password";
        this.Client_id = "mobileapidev.livingspaces.com";
        this.Client_secret = "lsfsecret";
    }

    public Map<String, String> loginMap(){
        Map<String, String> params = new Hashtable<>();
        params.put("grant_type", grant_type);
        params.put("Client_id", Client_id);
        params.put("Client_secret", Client_secret);
        params.put("username", username);
        params.put("password", password);
        return params;
    }
}
