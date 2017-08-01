package com.livingspaces.proshopper.interfaces;

import com.livingspaces.proshopper.data.Item;

import java.util.List;

/**
 * Created by rugvedambekar on 15-10-02.
 */
public interface IWishlistCallback {
    List<Item> getWishlist();
    void updateView();
    void onEditStateChanged(boolean edit);
    void closeItem();
}
