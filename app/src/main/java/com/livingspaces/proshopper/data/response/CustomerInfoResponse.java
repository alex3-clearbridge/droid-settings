package com.livingspaces.proshopper.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexeyredchets on 2017-09-22.
 */

public class CustomerInfoResponse {

    @SerializedName("shippingAddress")
    private ShippingAddress shippingAddress;

    public CustomerInfoResponse() {
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
