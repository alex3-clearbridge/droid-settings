package com.livingspaces.proshopper.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexeyredchets on 2017-08-31.
 */

public class ProductResponse {

    @SerializedName("product")
    Product product;
    @SerializedName("currentProductLocation")
    String curStoreId;

    public ProductResponse() {
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getCurStoreId() {
        return curStoreId;
    }

    public void setCurStoreId(String curStoreId) {
        this.curStoreId = curStoreId;
    }
}
