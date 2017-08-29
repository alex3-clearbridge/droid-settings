package com.livingspaces.proshopper.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.Item;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.interfaces.IWishlistCallback;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Utility;
import com.google.android.gms.analytics.HitBuilders;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFrag extends BaseStackFrag {
    private static final String TAG = WebViewFrag.class.getSimpleName();

    private Drawable d_cart;
    private String title, url;

    private Item item;
    private boolean fromWishlist;
    private IWishlistCallback WLCallback;

    private WebView rootView;

    public static WebViewFrag newInstance(String t, String u) {
        WebViewFrag wvf = new WebViewFrag();
        wvf.title = t;
        wvf.url = u;

        return wvf;
    }

    public static WebViewFrag newInstance(String u) {
        return newInstance("", u);
    }

    public WebViewFrag() {
    }

    public WebViewFrag withProduct(Item i) {
        title = "Product";
        item = i;
        return this;
    }

    public WebViewFrag forWishlist(IWishlistCallback cb) {
        WLCallback = cb;
        return this;
    }

    public WebViewFrag fromWishlist(IWishlistCallback cb) {
        fromWishlist = true;
        return forWishlist(cb);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        d_cart = ContextCompat.getDrawable(getContext(), R.drawable.ls_h_btn_cart);

        rootView = (WebView) inflater.inflate(R.layout.fragment_webview, container, false);

        rootView.getSettings().setJavaScriptEnabled(true);
        rootView.getSettings().setLoadWithOverviewMode(true);
        rootView.getSettings().setUseWideViewPort(true);
        rootView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
            }
        });
        if (item != null && item.sku != null && !item.sku.equals("")) {
            Log.d(TAG, "onCreateView: load url with false");
            rootView.loadUrl(url + item.sku, NetworkManager.getDefHeaders(false));
        } else {
            Log.d(TAG, "onCreateView: load url with true");
            rootView.loadUrl(url, NetworkManager.getDefHeaders(false));
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url1) {
                Log.d(TAG, "shouldOverrideUrlLoading: ");
                view.loadUrl(url1, NetworkManager.getDefHeaders(false));
                /*if (url1.equals("http://dev.livingspaces.com/VerifyZip.aspx?productId=")){

                    if (!Global.Prefs.hasToken()){
                        Log.d(TAG, "onClick: has no Token");
                        Global.FragManager.stackFrag(LoginFrag.newInstance());
                        Toast.makeText(getContext(), "You need to login first", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    else if (!Global.Prefs.hasStore()){
                        Log.d(TAG, "onClick: has no store");
                        Global.FragManager.stackFrag(AccountFrag.newInstance());
                        Toast.makeText(getContext(), "You need to choose store first", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                }*/

                Log.e(TAG, "shouldOverrideUrlLoading: " + url1);
                return super.shouldOverrideUrlLoading(view, url1);
            }
        });
    }

    @Override
    public boolean setTopRight(ImageView topRight) {
        if (item == null) return false;

        topRight.setRotation(0);
        topRight.setImageDrawable(d_cart);
        topRight.setOnClickListener(v -> {

            if (!Global.Prefs.hasToken()){
                Log.d(TAG, "onClick: has no Token");
                Global.FragManager.stackFrag(LoginFrag.newInstance());
                Toast.makeText(getContext(), "You need to login first", Toast.LENGTH_SHORT).show();
                return;
            }
            else if (!Global.Prefs.hasStore()){
                Log.d(TAG, "onClick: has no store");
                Global.FragManager.stackFrag(AccountFrag.newInstance());
                Toast.makeText(getContext(), "You need to choose store first", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                Global.FragManager.popToHome();
                Global.FragManager.stackFrag(WebViewFrag.newInstance("Cart", Services.URL.Cart.get()));
            /* Google Analytics -- home_button_click */
                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("ui_action")
                        .setAction("cart_button_click")
                        .setLabel("View Cart")
                        .build()
                );
                return;
            }

        });
        return true;
    }

    @Override
    public boolean setTopRightEx(final ImageView topRightEx) {
        if (item == null) return false;

        final boolean inWishlist = Global.Prefs.hasWishItem(item.sku);
        topRightEx.setRotation(inWishlist ? 135 : 0);
        topRightEx.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ls_s_btn_remove_00));
        topRightEx.setOnClickListener(new View.OnClickListener() {
            boolean added = inWishlist;

            @Override
            public void onClick(View v) {

                added = !added;
                if (!added && WLCallback != null) {
                    WLCallback.getWishlist().remove(item);
                    Global.Prefs.editWishItem(item.sku, false);
                    //Toast.makeText(getActivity(), "Removed from WishList", Toast.LENGTH_SHORT).show();
                    Global.FragManager.popToFrag(fromWishlist ? "WISHLIST" : NavigationFrag.NavItem.SCAN.title());
                } else {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    topRightEx.animate().setDuration(250).rotationBy(added ? 135 : -135).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            Global.Prefs.editWishItem(item.sku, added);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }).start();

                    /** Google Analytics - product_details_add_wishlist */
                    Utility.gaTracker.send(
                            new HitBuilders.EventBuilder()
                                    .setCategory("ui_action")
                                    .setAction("product_details_add_wishlist")
                                    .setLabel(item.sku)
                                    .build()
                    );
                }
            }
        });
        return true;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.hideSoftKeyboard(rootView);
    }
}
