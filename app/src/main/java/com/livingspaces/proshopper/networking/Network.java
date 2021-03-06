package com.livingspaces.proshopper.networking;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.livingspaces.proshopper.data.response.CustomerInfoResponse;
import com.livingspaces.proshopper.data.response.Store;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.data.request.LoginRequest;
import com.livingspaces.proshopper.data.request.RefreshTokenRequest;
import com.livingspaces.proshopper.data.response.MessageResponse;
import com.livingspaces.proshopper.data.response.LoginResponse;
import com.livingspaces.proshopper.data.response.Product;
import com.livingspaces.proshopper.data.response.ProductResponse;
import com.livingspaces.proshopper.data.response.WishlistResponse;
import com.livingspaces.proshopper.utilities.Global;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alexeyredchets on 2017-08-30.
 */

public class Network {

    private static final String TAG = Network.class.getSimpleName();
    private static final String baseUrl = Services.API.Main.get();

    private static Retrofit mRetrofit;
    private static Network mNetwork;
    private Context mContext;
    private EndPoint mApiService;

    private Network(Context context) {
        mContext = context;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiService = mRetrofit.create(EndPoint.class);
    }

    public static void Init(Context context){
        mNetwork = new Network(context);
    }

    public static void makeLoginREQ(String user, String pass, IRequestCallback.Login cb){
        if (mNetwork == null) return;
        mNetwork.sendLoginREQ(user, pass, cb);
    }

    public static void makeRefreshTokenREQ(IRequestCallback.Login cb){
        if (mNetwork == null) return;
        mNetwork.sendRefreshTokenREQ(cb);
    }

    public static void makeCreateAccREQ(String fname,
                                        String lname,
                                        String email,
                                        String zip,
                                        String pass,
                                        IRequestCallback.Message cb){
        if (mNetwork == null) return;
        mNetwork.sendCreateAccREQ(fname, lname, email, zip, pass, cb);
    }

    public static void makeResetPassREQ(String email, IRequestCallback.Message cb){
        if (mNetwork == null) return;
        mNetwork.sendResetPassREQ(email, cb);
    }

    public static void makeGetWishlistREQ(IRequestCallback.Wishlist cb){
        if (mNetwork == null) return;
        mNetwork.sendGetWishlistREQ(cb);
    }

    public static void makeGetProductREQ(String id, IRequestCallback.Product cb){
        if (mNetwork == null) return;
        mNetwork.sendGetProductREQ(id, cb);
    }

    public static void makeAddToWishREQ(String itemId, IRequestCallback.Message cb){
        if (mNetwork == null) return;
        mNetwork.sendAddToWishREQ(itemId, cb);
    }

    public static void makeDeleteItemWishlistREQ(String itemId, IRequestCallback.Message cb){
        if (mNetwork == null) return;
        mNetwork.sendDeleteItemWishlistREQ(itemId, cb);
    }

    public static void makeGetStoresREQ(IRequestCallback.Stores cb){
        if (mNetwork == null) return;
        mNetwork.sendGetStoresREQ(cb);
    }

    public static void makeGetStoreByZip(String zip, IRequestCallback.Stores cb){
        if (mNetwork == null) return;
        mNetwork.sendGetStoreByZipREQ(zip, cb);
    }

    public static void makeGetCartCountREQ(IRequestCallback.Message cb){
        if (mNetwork == null) return;
        mNetwork.sendGetCartCountREQ(cb);
    }

    public static void makeGetInfoREQ(IRequestCallback.Customer cb){
        if (mNetwork == null) return;
        mNetwork.sendGetCustomerREQ(cb);
    }

    public static void makeGetProductsREQ(String items, IRequestCallback.ProductList cb){
        if (mNetwork == null) return;
        mNetwork.sendGetProductsREQ(items, cb);
    }

    private void sendLoginREQ(String user, String pass, IRequestCallback.Login cb){
        Log.d(TAG, "sendLoginREQ: ");
        LoginRequest body = new LoginRequest(user, pass);
        Call<LoginResponse> login = mApiService.loginUser(body.loginMap());
        login.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "sendLoginREQ::onResponse: ");
                if (response.code() == 200) cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "sendLoginREQ::onFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendRefreshTokenREQ(IRequestCallback.Login cb){
        Log.d(TAG, "sendRefreshTokenREQ: ");
        String refToken = Global.Prefs.getRefreshToken();
        RefreshTokenRequest body = new RefreshTokenRequest(refToken);
        Call<LoginResponse> refresh = mApiService.refreshToken(body.refreshTokenMap());
        refresh.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "sendRefreshTokenREQ::onResponse: ");
                if (response.code() == 200) cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "sendRefreshTokenREQ::onFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendCreateAccREQ(String fname,
                                  String lname,
                                  String email,
                                  String zip,
                                  String pass,
                                  IRequestCallback.Message cb){
        Log.d(TAG, "sendCreateAccREQ: ");
        Call<MessageResponse> crAcc = mApiService.createAccount(fname, lname, email, pass, pass, zip, false, KeyValues.X_AUTH.second);
        crAcc.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Log.d(TAG, "sendCreateAccREQ::onResponse: ");
                if (response.code() == 200) cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d(TAG, "sendCreateAccREQ::onFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendResetPassREQ(String email, IRequestCallback.Message cb){
        Log.d(TAG, "sendResetPassREQ: ");
        Call<MessageResponse> resetPass = mApiService.resetEmail(email, KeyValues.X_AUTH.second);
        resetPass.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Log.d(TAG, "sendResetPassREQ::onResponse: ");
                if (response.code() == 200) cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d(TAG, "sendResetPassREQ::onFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendGetWishlistREQ(IRequestCallback.Wishlist cb){
        Log.d(TAG, "sendGetWishlistREQ: ");
        String username = Global.Prefs.getUserId();
        String token = Global.Prefs.getAccessToken();
        Call<WishlistResponse> getWishlist = mApiService.getWishlist(username, KeyValues.X_AUTH.second, token);
        getWishlist.enqueue(new Callback<WishlistResponse>() {

            @Override
            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                Log.d(TAG, "sendGetWishlistREQ::onResponse: " + response.body());

                if (response.code() == 200){
                    List<Product> wishlist = new ArrayList<>();
                    for (int i = 0; i < response.body().getCount(); i++){
                        wishlist.add(response.body().getWishlists().get(i).getProduct());
                    }
                    cb.onSuccess(wishlist);
                }
                else {
                    cb.onFailure(String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<WishlistResponse> call, Throwable t) {
                Log.d(TAG, "sendGetWishlistREQ::onFailure: " + t.getMessage());
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendAddToWishREQ(String itemId, IRequestCallback.Message cb){
        Log.d(TAG, "sendAddToWishREQ: ");
        String username = Global.Prefs.getUserId();
        String token = Global.Prefs.getAccessToken();
        Call<MessageResponse> addWish = mApiService.addToWishlist(username, itemId, KeyValues.X_AUTH.second, token);
        addWish.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Log.d(TAG, "sendAddToWishREQ::onResponse: ");
                if (response.code() == 200) cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d(TAG, "sendAddToWishREQ::onFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendGetProductREQ(String id, IRequestCallback.Product cb){
        Log.d(TAG, "sendGetProductREQ: ");
        Call<ProductResponse> product = mApiService.getProduct(id, KeyValues.X_AUTH.second);
        product.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                Log.d(TAG, "sendGetProductREQ::onResponse: " + response.body());
                if (response.code() == 200)cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.d(TAG, "sendGetProductREQ::onFailure: " + t.getMessage());
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendDeleteItemWishlistREQ(String itemId, IRequestCallback.Message cb){
        Log.d(TAG, "sendDeleteItemWishlistREQ: ");
        String username = Global.Prefs.getUserId();
        String token = Global.Prefs.getAccessToken();
        Call<MessageResponse> deleteItem = mApiService.deleteItem(KeyValues.X_AUTH.second, token, username, itemId);
        deleteItem.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Log.d(TAG, "sendDeleteItemWishlistREQ::onResponse: ");
                if (response.code() == 200)cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d(TAG, "sendDeleteItemWishlistREQonFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendGetStoresREQ(IRequestCallback.Stores cb){
        Log.d(TAG, "sendGetStoresREQ: ");
        Call<List<Store>> stores = mApiService.getStorelist(KeyValues.X_AUTH.second);
        stores.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                Log.d(TAG, "sendGetStoresREQ::onResponse: " + response.body());
                if (response.code() == 200)cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                Log.d(TAG, "sendGetStoresREQ::onFailure: " + t.getMessage());
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendGetStoreByZipREQ(String zip, IRequestCallback.Stores cb){
        Log.d(TAG, "sendGetStoreByZipREQ: ");
        Call<List<Store>> stores = mApiService.getStoreByZip(zip, KeyValues.X_AUTH.second);
        stores.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                Log.d(TAG, "sendGetStoreByZipREQ::onResponse: " + response.body());
                if (response.code() == 200)cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                Log.d(TAG, "sendGetStoreByZipREQ::onFailure: " + t.getMessage());
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendGetCartCountREQ(IRequestCallback.Message cb){
        Log.d(TAG, "sendGetCartCountREQ: ");
        String username = Global.Prefs.getUserId();
        String token = Global.Prefs.getAccessToken();
        Call<MessageResponse> getCount = mApiService.getCartCount(username, KeyValues.X_AUTH.second, token);
        getCount.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                Log.d(TAG, "sendGetCartCountREQ::onResponse: ");
                if (response.code() == 200)cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d(TAG, "sendGetCartCountREQ::onFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendGetCustomerREQ(IRequestCallback.Customer cb){
        Log.d(TAG, "sendGetCustomerREQ: ");
        String username = Global.Prefs.getUserId();
        String token = Global.Prefs.getAccessToken();
        Call<CustomerInfoResponse> getInfo = mApiService.getInfo(username, KeyValues.X_AUTH.second, token);
        getInfo.enqueue(new Callback<CustomerInfoResponse>() {
            @Override
            public void onResponse(Call<CustomerInfoResponse> call, Response<CustomerInfoResponse> response) {
                Log.d(TAG, "sendGetCustomerREQ::onResponse: ");
                if (response.code() == 200)cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<CustomerInfoResponse> call, Throwable t) {
                Log.d(TAG, "sendGetCustomerREQ::onFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    private void sendGetProductsREQ(String items, IRequestCallback.ProductList cb){
        Log.d(TAG, "sendGetProductsREQ: ");
        Call<List<Product>> getItems = mApiService.getProducts(items, KeyValues.X_AUTH.second);
        getItems.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                Log.d(TAG, "sendGetProductsREQ::onResponse: ");
                if (response.code() == 200)cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.d(TAG, "sendGetProductsREQ::onFailure: ");
                cb.onFailure(t.getMessage());
            }
        });
    }

    public static Map<String, String> getDefHeaders(boolean productPage) {

        Log.d(TAG, "getDefHeaders:");

        Map<String, String> headers = new Hashtable<>();

        headers.put(KeyValues.X_AUTH.first, KeyValues.X_AUTH.second);
        headers.put(KeyValues.MOB_APP.first, KeyValues.MOB_APP.second);

        /*headers.put("Authorization", "Bearer erFmkfRECzp3ItwYtlh1rNuUARRiqPiV07DODMx8_OEP5vnhFIqwOCD7kmnHiBkMqgkmcKfPUM1ZBEomTbEA9wLgghg14yQBvc09BL66jehtjODIGcK2-KEYjMLTmmAu9jqtIE0ORPUi6BBlODkQRbntVxh8gG7sHc1f5gy6BJY8L0tbXQ98K382LBpYi36ZpjQzOiElCfeTGzLvq-oSTtMdOwAPBB1HOOLokpPoHRIXOGCh68JsL-DUls5xCKSlIb6EdChBmedaE4pqjVUI7HOWpmDonx5-3NckDfis9CaBVFM7TJR3g8W13qGx119dXQg6nvXKYebke2bej2yylw");
        headers.put("username", "azvk89@yahoo.com");
        headers.put("storeId", "01");
        headers.put("zipCode", "90803");*/

        if (Global.Prefs.hasToken()) {
            String token = Global.Prefs.getAccessToken();
            headers.put("Authorization", token);
        }
        else headers.put("Authorization", "");

        if (Global.Prefs.hasToken()) {
            String username = Global.Prefs.getUserId();
            headers.put("username", username);
        }
        else headers.put("username", "");

        if (Global.Prefs.hasUserZip()) {
            String zip = Global.Prefs.getUserZip();
            headers.put("zipCode", zip);
        }
        else headers.put("zipCode", "");

        String storeId;
        if (Global.Prefs.hasCurrentStore()) {
            storeId = Global.Prefs.getCurrentStoreId();
        }
        else if (Global.Prefs.hasStore()){
            storeId = Global.Prefs.getStore().getId();
        }
        else storeId = "";
        headers.put("storeId", storeId);

        return headers;
    }

    public static class KeyValues {
        public static Pair<String, String> X_AUTH = new Pair<>("X-Auth-Token", "3CCE9BEB-AC66-4F12-BF37-B3FA66E08325");
        public static Pair<String, String> MOB_APP = new Pair<>("mobileApp", "android");
    }
}
