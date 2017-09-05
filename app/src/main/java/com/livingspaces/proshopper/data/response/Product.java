package com.livingspaces.proshopper.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alexeyredchets on 2017-08-30.
 */

public class Product {

    @SerializedName("rowId")
    String sku;
    @SerializedName("description")
    String title;
    @SerializedName("price")
    int price;
    @SerializedName("images")
    List<Image> images;

    public Product() {
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
