package com.livingspaces.proshopper.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rugvedambekar on 15-09-21.
 */
public class Store {
    public String id, name, zipCode, address, city, state;
    public String latitude, longitude;

    public String currDist;

    public Store(JSONObject jOBJ) {
        if (jOBJ == null) return;

        try {
            id = jOBJ.getString(JSONKey.storeId.name());
            name = jOBJ.getString(JSONKey.storeName.name());
            zipCode = jOBJ.getString(JSONKey.zipCode.name());
            latitude = jOBJ.getString(JSONKey.latitude.name());
            longitude = jOBJ.getString(JSONKey.longitude.name());
            currDist = jOBJ.getString(JSONKey.actualDistance.name());

            JSONObject addressOBJ = jOBJ.getJSONObject(JSONKey.address.name());
            address = addressOBJ.getString(JSONKey.address.name());
            city = addressOBJ.getString(JSONKey.city.name());
            state = addressOBJ.getString(JSONKey.state.name());

        } catch (JSONException e) {
            Log.e("[STORE]", e.toString());
        }
    }

    public String cityStateZip() {
        return city + ", " + state + " " + zipCode;
    }

    public String distance() {
        if (currDist.equals("") || currDist.equals("null") || currDist.equals("9999")) return "";

        return currDist + " miles";
    }

    public enum JSONKey {
        storeId, storeName, zipCode, latitude, longitude,
        address, city, state, actualDistance
    }
}
