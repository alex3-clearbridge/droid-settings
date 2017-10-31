package com.livingspaces.proshopper.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexeyredchets on 2017-08-31.
 */

public class Image {

    @SerializedName("fullPath")
    String imgUrl;

    public Image() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
