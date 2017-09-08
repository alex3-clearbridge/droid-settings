package com.livingspaces.proshopper.networking;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.livingspaces.proshopper.data.Store;
import com.livingspaces.proshopper.data.response.NetLocation;
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
    private static final String baseUrl = "http://mobileapidev.livingspaces.com/";

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
                                        String pass,
                                        IRequestCallback.Message cb){
        if (mNetwork == null) return;
        mNetwork.sendCreateAccREQ(fname, lname, email, pass, cb);
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

    public static void makeNetLocation(IRequestCallback.NetLocation cb){
        if (mNetwork == null) return;
        mNetwork.sendGetNetLocationREQ(cb);
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
                                  String pass,
                                  IRequestCallback.Message cb){
        Log.d(TAG, "sendCreateAccREQ: ");
        Call<MessageResponse> crAcc = mApiService.createAccount(fname, lname, email, pass, pass, false);
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
        Call<List<Store>> stores = mApiService.getStorelist("3CCE9BEB-AC66-4F12-BF37-B3FA66E08325");
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

    private void sendGetNetLocationREQ(IRequestCallback.NetLocation cb){
        Log.d(TAG, "sendGetNetLocationREQ: ");
        Call<NetLocation> getLoc = mApiService.getLoc();
        getLoc.enqueue(new Callback<NetLocation>() {
            @Override
            public void onResponse(Call<NetLocation> call, Response<NetLocation> response) {
                Log.d(TAG, "sendGetNetLocationREQ::onResponse: " + response.body());
                if (response.code() == 200)cb.onSuccess(response.body());
                else cb.onFailure(String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<NetLocation> call, Throwable t) {
                Log.d(TAG, "sendGetNetLocationREQ::onFailure: " + t.getMessage());
                cb.onFailure(t.getMessage());
            }
        });
    }

    public static Map<String, String> getDefHeaders(boolean cart) {

        Log.d(TAG, "getDefHeaders:");

        Map<String, String> headers = new Hashtable<>();

        headers.put(KeyValues.X_AUTH.first, KeyValues.X_AUTH.second);
        headers.put(KeyValues.MOB_APP.first, KeyValues.MOB_APP.second);
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
        if (Global.Prefs.hasStore()) {
            String storeId = Global.Prefs.getStore().getId();
            headers.put("storeId", storeId);
        }
        else headers.put("storeId", "");
        if (Global.Prefs.hasUserZip()) {
            String zip = Global.Prefs.getUserZip();
            headers.put("zipCode", "90803");
        }
        else headers.put("zipCode", "");

        return headers;
    }


    public static class KeyValues {
        public static Pair<String, String> X_AUTH = new Pair<>("X-Auth-Token", "3CCE9BEB-AC66-4F12-BF37-B3FA66E08325");
        public static Pair<String, String> MOB_APP = new Pair<>("mobileApp", "android");
    }
}
