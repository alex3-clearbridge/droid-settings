package com.livingspaces.proshopper.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by rugvedambekar on 15-09-21.
 */
public class Store {

    @SerializedName("storeId")
    private String id;
    @SerializedName("storeName")
    private String name;
    @SerializedName("zipCode")
    private String zipCode;
    @SerializedName("address")
    private StoreAddress storeAddresses;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("distance")
    private String distance;

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

    public StoreAddress getStoreAddresses() {
        return storeAddresses;
    }

    public void setStoreAddresses(StoreAddress storeAddresses) {
        this.storeAddresses = storeAddresses;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    /*public Store(JSONObject jOBJ) {
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
    }*/
}
