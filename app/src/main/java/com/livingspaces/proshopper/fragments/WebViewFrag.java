package com.livingspaces.proshopper.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.response.LoginResponse;
import com.livingspaces.proshopper.data.response.MessageResponse;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
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

    private Drawable d_cart;
    private String title, url;

    private View topUpView;
    private TextView tv_topRightUp;

    private Product item;
    private boolean fromWishlist;
    private IWishlistCallback WLCallback;
    private String[] currentSku;

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
        if (item != null && item.getSku() != null && !item.getSku().equals("")) {
            Log.d(TAG, "onCreateView: load url with false " + url + item.getSku());
            rootView.loadUrl(url + item.getSku(), Network.getDefHeaders(true));
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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
                if (url.contains("http://ark.livingspaces.com/SignInPrompt") && currentSku != null){
                    // user not logged in --> stop loading page and go to login fragment
                    Global.FragManager.stackFrag(LoginFrag.newInstance(true, currentSku[1]));
                    return true;
                }
                /*if (url.contains("http://ark.livingspaces.com/Views/Mobile/productview.aspx?productId=") ||
                        url.contains("http://ark.livingspaces.com/ProductView.aspx?productId=")) {
                    view.loadUrl(url, Network.getDefHeaders(false));
                    return super.shouldOverrideUrlLoading(view, url);
                }*/

                return false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d(TAG, "onLoadResource: " + url);
                if (url.contains("http://ark.livingspaces.com/Views/Mobile/productview.aspx?productId=") ||
                        url.contains("http://ark.livingspaces.com/ProductView.aspx?productId=")){
                    // save product sku to load product page after login process
                    currentSku = url.split("=");
                }
                super.onLoadResource(view, url);
            }
        });
    }

    @Override
    public boolean setTopRight(ImageView topRight) {
        if (item == null) return false;

        topRight.setRotation(0);
        topRight.setImageDrawable(d_cart);
        topRight.setOnClickListener(onCartClicked);
        return true;
    }

    @Override
    public boolean setTopRightUp(View topRightUp) {
        if (item == null) return false;

        tv_topRightUp = (TextView) topRightUp.findViewById(R.id.tv_topRightUp);

        topRightUp.setVisibility(View.VISIBLE);
        topRightUp.setOnClickListener(onCartClicked);

        if (Global.Prefs.hasToken()) updateCartCount();
        else tv_topRightUp.setText("0");

        return true;
    }

    private View.OnClickListener onCartClicked = view -> {
        Log.d(TAG, "onCartClicked: ");
        if (!Global.Prefs.hasToken()){
            Log.d(TAG, "onClick: has no Token");
            Global.FragManager.stackFrag(LoginFrag.newInstance(false,""));
            Toast.makeText(getContext(), "You need to login first", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!Global.Prefs.hasStore()){
            Log.d(TAG, "onClick: has no store");
            Global.FragManager.stackFrag(AccountFrag.newInstance());
            Toast.makeText(getContext(), "You need to choose store first", Toast.LENGTH_SHORT).show();
            return;
        }
        rootView.loadUrl(Services.URL.Cart.get(), Network.getDefHeaders(false));
    };

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
                    Toast.makeText(getActivity(), "Removed from WishList", Toast.LENGTH_SHORT).show();
                    Global.FragManager.popToFrag(fromWishlist ? "WISHLIST" : NavigationFrag.NavItem.SCAN.title());
                } else {
                    Log.d(TAG, "onClick: added || WLCallback == null");
                    getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    topRightEx.animate().setDuration(250).rotationBy(added ? 135 : -135).withEndAction(() -> {
                        Global.Prefs.editWishItem(item.getSku(), added);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }).start();
                    
                    if (!added){
                        Network.makeDeleteItemWishlistREQ(item.getSku(), new IRequestCallback.Message() {
                            @Override
                            public void onSuccess(MessageResponse response) {
                                Log.d(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onFailure(String message) {
                                Log.d(TAG, "onFailure: ");
                            }
                        });
                        Toast.makeText(getActivity(), "Removed from WishList", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getActivity(), "Added to WishList", Toast.LENGTH_SHORT).show();
                    }

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

    public void updateCartCount(){

        Network.makeGetCartCountREQ(new IRequestCallback.Message() {
            @Override
            public void onSuccess(MessageResponse response) {
                Log.d(TAG, "getCartCount::onSuccess: ");
                if (response.getMessage() != null) tv_topRightUp.setText(response.getMessage());
                else onFailure("0");
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "getCartCount::onFailure: ");
                tv_topRightUp.setText("0");
            }
        });
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
