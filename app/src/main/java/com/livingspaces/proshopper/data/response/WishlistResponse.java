package com.livingspaces.proshopper.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by alexeyredchets on 2017-08-30.
 */

public class WishlistResponse {

    @SerializedName("count")
    int count;

    @SerializedName("wishlist")
    List<Wishlist> wishlists;

    public WishlistResponse() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Wishlist> getWishlists() {
        return wishlists;
    }

    public void setWishlists(List<Wishlist> wishlists) {
        this.wishlists = wishlists;
    }
}
