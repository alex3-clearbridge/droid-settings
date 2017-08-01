package com.livingspaces.proshopper.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.livingspaces.proshopper.interfaces.IWishlistCallback;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Utility;
import com.google.android.gms.analytics.HitBuilders;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFrag extends BaseStackFrag {
    private static final String TAG = WebViewFrag.class.getSimpleName();

    private Drawable d_home;
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

        d_home = ContextCompat.getDrawable(getActivity(), R.drawable.ls_s_btn_home);

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
            rootView.loadUrl(url + item.sku, NetworkManager.getDefHeaders(false));
        } else {
            rootView.loadUrl(url, NetworkManager.getDefHeaders(true));
        }

        return rootView;
    }

    @Override
    public boolean setTopRight(ImageView topRight) {
        if (item == null) return false;

        topRight.setRotation(0);
        topRight.setImageDrawable(d_home);
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.FragManager.popToHome();

                /** Google Analytics - product_details_home */
                Utility.gaTracker.send(
                        new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("product_details_home")
                                .build()
                );
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
