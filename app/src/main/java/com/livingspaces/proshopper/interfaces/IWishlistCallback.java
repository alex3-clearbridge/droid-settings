package com.livingspaces.proshopper.interfaces;

//import com.livingspaces.proshopper.data.Item;
import com.livingspaces.proshopper.data.response.Product;

import java.util.List;

/**
 * Created by rugvedambekar on 15-10-02.
 */
public interface IWishlistCallback {
    List<Product> getWishlist();
    void updateView();
    void onEditStateChanged(boolean edit);
    void closeItem();
}
