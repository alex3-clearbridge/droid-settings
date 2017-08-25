package com.livingspaces.proshopper.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import com.livingspaces.proshopper.MainActivity;
import com.livingspaces.proshopper.data.Store;
import com.livingspaces.proshopper.interfaces.IMainFragManager;

/**
 * Created by rugvedambekar on 15-09-21.
 */
public class Global {

    public static Context context;
    public static Resources Resources;
    public static IMainFragManager FragManager;

    public static void Init(MainActivity mainActivity) {
        Resources = mainActivity.getResources();
        FragManager = mainActivity;
        context = mainActivity;
        Prefs.Init(mainActivity);
    }

    public static Camera getCameraInstance(){
        Camera c = null;

        try { c = Camera.open(); }
        catch (Exception e){ e.printStackTrace(); }

        return c;
    }

    public static class Prefs {
        private static final String TAG = "LivingSpace.Preferences";
        private static final String KEY_wishlist = "wishlist";
        private static final String KEY_access_token = "accessToken";
        private static final String KEY_refresh_token = "refreshToken";
        private static final String KEY_storeName = "storeName",
                KEY_storeId = "storeId",
                KEY_storeZipcode = "storeZipCode",
                KEY_storeAddress = "storeAddress",
                KEY_storeCity = "storeCity",
                KEY_storeState = "storeState";

        private static SharedPreferences sharedPrefs;
        private static String d_wishList,
                store_id,
                store_name,
                store_zip,
                store_address,
                store_city,
                store_state;


        public static void Init(MainActivity mainActivity) {
            sharedPrefs = mainActivity.getSharedPreferences(Prefs.TAG, Context.MODE_PRIVATE);
            getWishListRAW();
        }

        public static String getWishListRAW() {
            if (sharedPrefs == null) return null;
            return d_wishList != null ? d_wishList : (d_wishList = sharedPrefs.getString(KEY_wishlist, ""));
        }

        public static void clear() {
            if (sharedPrefs == null) return;
            sharedPrefs.edit().putString(KEY_wishlist, d_wishList = "").apply();

            Log.d(TAG, "New WishList: " + d_wishList);
        }

        public static void editWishItem(String wishId, boolean add) {
            Toast.makeText(context, (add ? "Added to" : "Removed from") + " Wishlist", Toast.LENGTH_SHORT).show();
            if (sharedPrefs == null) return;

            wishId =   wishId + ","  ;
            if (add && !d_wishList.contains(wishId)){
                d_wishList = wishId + d_wishList;
            }
            else if (!add){
                d_wishList = d_wishList.replace(wishId, "");
            }

            sharedPrefs.edit().putString(KEY_wishlist, d_wishList).apply();
            Log.d(TAG, "New WishList: " + d_wishList);
        }

        public static boolean hasWishItem(String wishId) {
            if (sharedPrefs == null) return false;

            wishId = wishId + ",";
            return d_wishList.contains(wishId);
        }

        public static void clearToken(){
            if (sharedPrefs == null || !sharedPrefs.contains(KEY_access_token)) return;

            sharedPrefs.edit().remove(KEY_access_token).apply();
        }

        public static void editToken(String access_token, String refresh_token){

            if (sharedPrefs == null) return;

            sharedPrefs.edit()
                    .putString(KEY_access_token, access_token)
                    .putString(KEY_refresh_token, refresh_token)
                    .apply();
        }

        public static boolean hasToken(){
            return sharedPrefs != null &&
                    sharedPrefs.contains(KEY_access_token) &&
                    sharedPrefs.contains(KEY_refresh_token);
        }

        public static String getAccessToken(){
            if (sharedPrefs == null && !sharedPrefs.contains(KEY_access_token)) return "";
            return sharedPrefs.getString(KEY_access_token, "");
        }

        public static String getRefreshToken(){
            if (sharedPrefs == null && !sharedPrefs.contains(KEY_refresh_token)) return "";
            return sharedPrefs.getString(KEY_refresh_token, "");
        }

        public static boolean hasStore(){
            return sharedPrefs != null && sharedPrefs.contains(KEY_storeId);
        }

        public static void saveStore(Store store){

            if (sharedPrefs == null || store == null) return;

            sharedPrefs.edit()
                    .putString(KEY_storeId, store.getId())
                    .putString(KEY_storeName, store.getName())
                    .putString(KEY_storeZipcode, store.getZipCode())
                    .putString(KEY_storeAddress, store.getAddress())
                    .putString(KEY_storeCity, store.getCity())
                    .putString(KEY_storeState, store.getState())
                    .apply();


        }

        public static Store getStore(){

            if (sharedPrefs == null && !sharedPrefs.contains(KEY_storeId)) return null;

            store_id = sharedPrefs.getString(KEY_storeId, "");
            store_name = sharedPrefs.getString(KEY_storeName, "");
            store_zip = sharedPrefs.getString(KEY_storeZipcode, "");
            store_address = sharedPrefs.getString(KEY_storeAddress, "");
            store_city = sharedPrefs.getString(KEY_storeCity, "");
            store_state = sharedPrefs.getString(KEY_storeState, "");

            Store store = new Store();
            store.setId(store_id);
            store.setName(store_name);
            store.setZipCode(store_zip);
            store.setAddress(store_address);
            store.setCity(store_city);
            store.setState(store_state);

            return store;
        }
    }
}
