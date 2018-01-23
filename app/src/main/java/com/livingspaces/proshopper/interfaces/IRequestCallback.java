package com.livingspaces.proshopper.interfaces;

import com.livingspaces.proshopper.data.response.CustomerInfoResponse;
import com.livingspaces.proshopper.data.response.Product;
import com.livingspaces.proshopper.data.response.Store;
import com.livingspaces.proshopper.data.response.MessageResponse;
import com.livingspaces.proshopper.data.response.LoginResponse;
import com.livingspaces.proshopper.data.response.ProductResponse;

import java.util.List;

/**
 * Created by alexeyredchets on 2017-08-30.
 */

public interface IRequestCallback {

    interface Login {
        void onSuccess(LoginResponse response);
        void onFailure(String message);
    }

    interface Message {
        void onSuccess(MessageResponse response);
        void onFailure(String message);
    }

    interface Wishlist {
        void onSuccess(List<com.livingspaces.proshopper.data.response.Product> wishlist);
        void onFailure(String message);
    }

    interface Product {
        void onSuccess(ProductResponse product);
        void onFailure(String message);
    }

    interface Stores {
        void onSuccess(List<Store> storeList);
        void onFailure(String message);
    }

    interface Customer {
        void onSuccess(CustomerInfoResponse response);
        void onFailure(String message);
    }

    interface ProductList {
        void onSuccess(List<com.livingspaces.proshopper.data.response.Product> response);
        void onFailure(String message);
    }
}
