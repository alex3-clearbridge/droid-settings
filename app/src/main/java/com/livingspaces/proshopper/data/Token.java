package com.livingspaces.proshopper.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alexeyredchets on 2017-08-17.
 */

public class Token {
    public String access_token, type, refresh_token;
    public int expires;

    public Token(String JsonString) {
        if (JsonString == null) return;


        try {
            JSONObject jOBJ = new JSONObject(JsonString);

            access_token = jOBJ.getString(Token.JSONKey.access_token.name());
            refresh_token = jOBJ.getString(Token.JSONKey.refresh_token.name());
            type = jOBJ.getString(JSONKey.token_type.name());
            expires = jOBJ.getInt(JSONKey.expires_in.name());
        } catch (JSONException e) {
            e.printStackTrace(); }
    }

    public enum JSONKey {
        access_token, expires_in, token_type, refresh_token
    }

}
