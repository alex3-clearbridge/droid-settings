package com.livingspaces.proshopper.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rugvedambekar on 15-09-21.
 */
public class Store {
    private String id, name, zipCode, address, city, state;
    private String latitude, longitude;

    public String currDist;

    public Store() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCurrDist() {
        return currDist;
    }

    public void setCurrDist(String currDist) {
        this.currDist = currDist;
    }

    public Store(JSONObject jOBJ) {
        if (jOBJ == null) return;

        try {
            setId(jOBJ.getString(JSONKey.storeId.name()));
            setName(jOBJ.getString(JSONKey.storeName.name()));
            setZipCode(jOBJ.getString(JSONKey.zipCode.name()));
            setLatitude(jOBJ.getString(JSONKey.latitude.name()));
            setLongitude(jOBJ.getString(JSONKey.longitude.name()));
            setCurrDist(jOBJ.getString(JSONKey.actualDistance.name()));

            JSONObject addressOBJ = jOBJ.getJSONObject(JSONKey.address.name());
            setAddress(addressOBJ.getString(JSONKey.address.name()));
            setCity(addressOBJ.getString(JSONKey.city.name()));
            setState(addressOBJ.getString(JSONKey.state.name()));

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
