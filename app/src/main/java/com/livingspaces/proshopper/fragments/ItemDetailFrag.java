package com.livingspaces.proshopper.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.livingspaces.proshopper.R;
//import com.livingspaces.proshopper.data.Item;
import com.livingspaces.proshopper.interfaces.IWishlistCallback;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.data.response.Product;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Utility;
import com.google.android.gms.analytics.HitBuilders;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDetailFrag extends BaseStackFrag {

    private Product item;
    private boolean forWishlist, fromWishlist;
    private IWishlistCallback WLCallback;

    public static ItemDetailFrag newInstance(Product i) {
        ItemDetailFrag idFrag = new ItemDetailFrag();
        idFrag.item = i;
        return idFrag;
    }

    public ItemDetailFrag() {
    }

    public ItemDetailFrag forWishlist(IWishlistCallback cb) {
        forWishlist = true;
        WLCallback = cb;
        return this;
    }

    public ItemDetailFrag fromWishlist(IWishlistCallback cb) {
        fromWishlist = true;
        return forWishlist(cb);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        ((TextView) rootView.findViewById(R.id.tv_itemTitle)).setText(item.getTitle());
        ((TextView) rootView.findViewById(R.id.tv_itemPrice)).setText("$ " + item.getPrice());
        ((TextView) rootView.findViewById(R.id.tv_itemSKU)).setText(item.getSku());
        if (item.getImages() != null && item.getImages().size() > 0 && item.getImages().get(0) != null && item.getImages().get(0).getImgUrl() != null) {
            Picasso.with(getContext()).load(item.getImages().get(0).getImgUrl()).into(((NetworkImageView) rootView.findViewById(R.id.niv_itemImg)));

            //((NetworkImageView) rootView.findViewById(R.id.niv_itemImg)).setImageUrl(item.getImages().get(0).getImgUrl(), NetworkManager.getIMGLoader());
        } else {
            ((NetworkImageView) rootView.findViewById(R.id.niv_itemImg)).setDefaultImageResId(R.drawable.ls_w_img_default);
        }

        rootView.findViewById(R.id.rl_moreDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewFrag wvfInst = WebViewFrag.newInstance(Services.URL.Product.get()).withProduct(item);

                if (fromWishlist) wvfInst.fromWishlist(WLCallback);
                else wvfInst.forWishlist(WLCallback);

                Global.FragManager.stackFrag(wvfInst);

                /** Google Analytics - more_details */
                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("more_details")
                                .setLabel(item.getSku())
                                .build()
                );
            }
        });

        rootView.findViewById(R.id.tv_addMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fromWishlist)
                    Global.FragManager.popToFrag(NavigationFrag.NavItem.SCAN.title());
                else
                    Global.FragManager.startFrag(CodeScanFrag.newInstance().forWishList(WLCallback));

                /** Google Analytics - add_another_product */
                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("add_another_product")
                                .build()
                );
            }
        });

        TextView tv_toWishlist = (TextView) rootView.findViewById(R.id.tv_toWL);
        tv_toWishlist.setText(WLCallback != null ? "Back To Wishlist" : "View Wishlist");
        tv_toWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromWishlist) Global.FragManager.popToFrag("WISHLIST");
                else Global.FragManager.startFrag(WishlistFrag.newInstance());
            }
        });

        if (forWishlist) {
            final boolean inWishlist = Global.Prefs.hasWishItem(item.getSku());
            if (!inWishlist) {
                Global.Prefs.editWishItem(item.getSku(), true);
                if (WLCallback != null) WLCallback.getWishlist().add(item);
            }

            if(!fromWishlist && inWishlist) {
                Toast.makeText(getActivity(), "Already in Wishlist", Toast.LENGTH_SHORT).show();
            }
        }
        return rootView;
    }

    @Override
    public boolean setTopRight(final ImageView topRight) {
        topRight.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ls_s_btn_remove_00));
        topRight.setRotation(-135);
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.Prefs.editWishItem(item.getSku(), false);
                if (WLCallback != null) WLCallback.getWishlist().remove(item);

                if (fromWishlist) Global.FragManager.popToFrag("WISHLIST");
                else if (forWishlist) Global.FragManager.stackFrag(WishlistFrag.newInstance());
                else Global.FragManager.popToFrag(NavigationFrag.NavItem.SCAN.title());
            }
        });
        return true;
    }

    @Override
    public String getTitle() {
        return "Item Details";
    }
}
