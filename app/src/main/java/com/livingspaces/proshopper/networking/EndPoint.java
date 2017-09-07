package com.livingspaces.proshopper.networking;

import com.livingspaces.proshopper.data.Store;
import com.livingspaces.proshopper.data.response.MessageResponse;
import com.livingspaces.proshopper.data.response.LoginResponse;
import com.livingspaces.proshopper.data.response.ProductResponse;
import com.livingspaces.proshopper.data.response.WishlistResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by alexeyredchets on 2017-08-30.
 */

public interface EndPoint {

    @FormUrlEncoded
    @POST("token")
    Call<LoginResponse> loginUser(
            @FieldMap Map<String, String> body
    );

    @FormUrlEncoded
    @POST("token")
    Call<LoginResponse> refreshToken(
            @FieldMap Map<String, String> body
    );

    @POST("api/account/createAccount")
    Call<MessageResponse> createAccount(
            @Query("firstName") String firstName,
            @Query("lastName") String lastName,
            @Query("emailAddress") String email,
            @Query("password") String pass,
            @Query("confirmPass") String repass,
            @Query("wantsNews") boolean news
    );

    @PUT("api/account/forgotPassword")
    Call<MessageResponse> resetEmail(
            @Query("email") String email,
            @Header("X-Auth-Token") String authToken
    );

    @GET("api/Product/getWishlist")
    Call<WishlistResponse> getWishlist(
            @Query("username") String email,
            @Header("X-Auth-Token") String authToken,
            @Header("Authorization") String token
    );

    @POST("api/Product/addItemToWishlist")
    Call<MessageResponse> addToWishlist(
            @Query("username") String customerId,
            @Query("itemId") String itemId,
            @Header("X-Auth-Token") String authToken,
            @Header("Authorization") String token
    );

    @GET("api/Product/{id}")
    Call<ProductResponse> getProduct(
            @Path("id") String productId,
            @Header("X-Auth-Token") String authToken
    );

    @POST("api/Product/removeItemFromWishlist")
    Call<MessageResponse> deleteItem(
            @Header("X-Auth-Token") String authToken,
            @Header("Authorization") String token,
            @Query("username") String customerId,
            @Query("itemId") String itemId
    );

    @GET("api/Store/getAllStores")
    Call<List<Store>> getStorelist(
            @Header("X-Auth-Token") String authToken
    );

    @GET("api/Store/getLocateByZip")
    Call<Store> getStoreByZip(
            @Query("zipcode") String zip,
            @Header("X-Auth-Token") String authToken
    );
}
