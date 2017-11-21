package com.livingspaces.proshopper.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Utility;
import com.google.android.gms.analytics.HitBuilders;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFrag extends Fragment implements View.OnClickListener {
    private static final String TAG = NavigationFrag.class.getSimpleName();

    public enum NavItem {
        SCAN("Scan Barcode", R.id.nav_scan, R.drawable.ls_h_icon_scan),
        FIND("Find A Store", R.id.nav_find, R.drawable.ls_h_icon_find),
        ADD("Add to Wishlist", R.id.nav_add, R.drawable.ls_h_icon_add),
        VIEW("View Wishlist", R.id.nav_view, R.drawable.ls_h_icon_view),
        SUB("Subscribe", R.id.nav_sub, R.drawable.ls_h_icon_sub),
        SOCIAL("Social Media", R.id.nav_social, R.drawable.ls_h_icon_social),
        ACCOUNT("My Account", R.id.nav_account, R.drawable.is_h_btn_account),
        SETTINGS("Browse Website", R.id.nav_browse, R.drawable.ls_h_btn_web);

        private int resId, imgId;
        private String title;

        private NavItem(String t, int r, int i) {
            title = t;
            resId = r;
            imgId = i;
        }

        public String title() {
            return title;
        }
    }

    public static NavigationFrag newInstance() {
        return new NavigationFrag();
    }

    public NavigationFrag() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nav, container, false);

        for (NavItem navItem : NavItem.values()) {
            RelativeLayout navButton = (RelativeLayout) rootView.findViewById(navItem.resId);
            ((ImageView) navButton.findViewById(R.id.iv_nav)).setImageResource(navItem.imgId);
            ((TextView) navButton.findViewById(R.id.tv_nav)).setText(navItem.title);
            navButton.setOnClickListener(this);
        }

        return rootView;
    }

    private boolean clickAddressed = false;

    @Override
    public void onClick(View v) {
        if (clickAddressed) return;
        clickAddressed = true;

        String label = "";

        switch (v.getId()) {
            case R.id.nav_scan:
                label = "Scan Barcode";
                Global.FragManager.stackFrag(CodeScanFrag.newInstance());
                break;
            case R.id.nav_find:
                label = "Find Store";
                Global.FragManager.stackFrag(FindStoreFrag.newInstance());
                break;
            case R.id.nav_add:
                label = "Add to Wishlist";
                Global.FragManager.stackFrag(CodeScanFrag.newInstance().forWishList(null));
                break;
            case R.id.nav_view:
                label = "View Wishlist";
                Global.FragManager.stackFrag(WishlistFrag.newInstance());
                break;
            case R.id.nav_sub:
                label = "Subscribe";
                Global.FragManager.stackFrag(WebViewFrag.newInstance(getResources().getString(R.string.subscribe_page_title), Services.URL.Subscribe.get()));
                break;
            case R.id.nav_social:
                label = "Social Media";
                Global.FragManager.stackFrag(SocialFrag.newInstance());
                break;
            case R.id.nav_account:
                label = "My Account";
                Global.FragManager.stackFrag(AccountFrag.newInstance());
                break;
            case R.id.nav_browse:
                label = "Browse";
                Global.FragManager.stackFrag(WebViewFrag.newInstance("LivingSpaces", Services.URL.Website.get()));
                break;
        }

        /** Google Analytics -- home_button_click */
        Utility.gaTracker.send(new HitBuilders.EventBuilder().
                        setCategory("ui_action")
                        .setAction("home_button_click")
                        .setLabel(label)
                        .build()
        );

        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                clickAddressed = false;
            }
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
