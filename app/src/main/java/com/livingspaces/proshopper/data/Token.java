package com.livingspaces.proshopper.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alexeyredchets on 2017-08-17.
 */

public class Token {
    public String access_token, type, refresh_token, userName;
    public int expires;

    public Token(String JsonString) {
        if (JsonString == null) return;

        try {
            JSONObject jOBJ = new JSONObject(JsonString);

            access_token = jOBJ.getString(Token.JSONKey.access_token.name());
            refresh_token = jOBJ.getString(Token.JSONKey.refresh_token.name());
            type = jOBJ.getString(JSONKey.token_type.name());
            expires = jOBJ.getInt(JSONKey.expires_in.name());
            userName = jOBJ.getString(JSONKey.userName.name());
        } catch (JSONException e) {
            e.printStackTrace(); }
    }

    public enum JSONKey {
        access_token, token_type, expires_in, refresh_token, userName
    }

}
