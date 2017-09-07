package com.livingspaces.proshopper.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.livingspaces.proshopper.interfaces.IWishlistCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.data.response.Product;
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

    private Product item;
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

    public WebViewFrag withProduct(Product i) {
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
        d_home = ContextCompat.getDrawable(getContext(), R.drawable.ls_s_btn_home);

        rootView = (WebView) inflater.inflate(R.layout.fragment_webview, container, false);

        rootView.getSettings().setJavaScriptEnabled(true);
        rootView.getSettings().setLoadWithOverviewMode(true);
        rootView.getSettings().setUseWideViewPort(true);
        rootView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
            }
        });
        if (item != null && item.getSku() != null && !item.getSku().equals("")) {
            Log.d(TAG, "onCreateView: load url with false " + url + item.getSku());
            rootView.loadUrl(url + item.getSku(), Network.getDefHeaders(false));
        } else {
            Log.d(TAG, "onCreateView: load url with true");
            rootView.loadUrl(url, Network.getDefHeaders(false));
        }
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url1) {
                Log.e(TAG, "shouldOverrideUrlLoading: " + url1);

                if (url1.equals("http://dev.livingspaces.com/VerifyZip.aspx?productId=")){
                    //view.loadUrl("http://dev.livingspaces.com/Test/TestBrad.aspx", Network.getDefHeaders(false));
                    //url1 = "http://dev.livingspaces.com/Views/Mobile/VerifyZip.aspx?productId=" + item.getSku();
                    Log.d(TAG, "shouldOverrideUrlLoading: GOT IT");
                    if (!Global.Prefs.hasToken()){
                        Global.FragManager.stackFrag(LoginFrag.newInstance());
                        Toast.makeText(getContext(), "You have to login first", Toast.LENGTH_SHORT).show();
                    }
                    else if (!Global.Prefs.hasStore()){
                        Global.FragManager.stackFrag(AccountFrag.newInstance());
                        Toast.makeText(getContext(), "You have to choose store first", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        view.loadUrl(url1, Network.getDefHeaders(false));
                        Log.d(TAG, "shouldOverrideUrlLoading: " + url1);
                        return super.shouldOverrideUrlLoading(view, url1);
                    }
                }

                //view.loadUrl(url1, Network.getDefHeaders(false));

                return super.shouldOverrideUrlLoading(view, url1);
            }
        });
    }

    @Override
    public boolean setTopRight(ImageView topRight) {
        if (item == null) return false;

        topRight.setRotation(0);
        topRight.setImageDrawable(d_home);
        topRight.setOnClickListener(v -> {

            Global.FragManager.popToHome();

        });
        return true;
    }

    @Override
    public boolean setTopRightEx(final ImageView topRightEx) {
        if (item == null) return false;

        final boolean inWishlist = Global.Prefs.hasWishItem(item.getSku());
        topRightEx.setRotation(inWishlist ? 135 : 0);
        topRightEx.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ls_s_btn_remove_00));
        topRightEx.setOnClickListener(new View.OnClickListener() {
            boolean added = inWishlist;

            @Override
            public void onClick(View v) {

                added = !added;
                if (!added && WLCallback != null) {
                    WLCallback.getWishlist().remove(item);
                    Global.Prefs.editWishItem(item.getSku(), false);
                    //Toast.makeText(getActivity(), "Removed from WishList", Toast.LENGTH_SHORT).show();
                    Global.FragManager.popToFrag(fromWishlist ? "WISHLIST" : NavigationFrag.NavItem.SCAN.title());
                } else {
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    topRightEx.animate().setDuration(250).rotationBy(added ? 135 : -135).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            Global.Prefs.editWishItem(item.getSku(), added);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }).start();

                    /** Google Analytics - product_details_add_wishlist */
                    Utility.gaTracker.send(
                            new HitBuilders.EventBuilder()
                                    .setCategory("ui_action")
                                    .setAction("product_details_add_wishlist")
                                    .setLabel(item.getSku())
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
