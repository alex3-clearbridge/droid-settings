package com.livingspaces.proshopper.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Camera;
import android.util.Log;
import android.widget.Toast;

import com.livingspaces.proshopper.MainActivity;
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

        private static SharedPreferences sharedPrefs;
        private static String d_wishList;

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
    }
}
