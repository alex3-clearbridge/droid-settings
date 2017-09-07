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
}
