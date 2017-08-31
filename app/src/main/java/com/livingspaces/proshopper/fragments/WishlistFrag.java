package com.livingspaces.proshopper.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livingspaces.proshopper.MainActivity;
import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.adapters.WishlistAdapter;
import com.livingspaces.proshopper.data.DataModel;
import com.livingspaces.proshopper.data.Item;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.interfaces.IWishlistCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.networking.NetworkManager;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.networking.response.MessageResponse;
import com.livingspaces.proshopper.networking.response.Product;
import com.livingspaces.proshopper.networking.response.ProductResponse;
import com.livingspaces.proshopper.swipelistview.BaseSwipeListViewListener;
import com.livingspaces.proshopper.swipelistview.SwipeListView;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.views.WishlistFAB;
import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishlistFrag extends BaseStackFrag implements IWishlistCallback, WishlistFAB.ICallback {

    private static final String TAG = WishlistFrag.class.getSimpleName();

    private RelativeLayout rootView;
    private SwipeListView rv_wishlist;
    private WishlistAdapter wlAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private BaseSwipeListViewListener mBaseSwipeListViewListener;

    private int currentOpenPos = -1;

    private String rawWL;
    //private List<Item> wishlist;
    private List<Product> wishlist;
    private List<String> onlineWishlist;

    private WishlistFAB wishlistFAB;

    private TextView emptyIV;

    private TextView scanProduct;

    private WishlistFrag callback = this;

    public static WishlistFrag newInstance() {
        return new WishlistFrag();
    }

    public WishlistFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkOnlineWishList();

        rawWL = Global.Prefs.getWishListRAW();
        if (rawWL == null) rawWL = "";

        if (!rawWL.isEmpty()) {
            String[] arr = rawWL.split(",");
            for (int i = 0; i < arr.length; i++){
                Network.makeGetProductREQ(arr[i], new IRequestCallback.Product() {
                    @Override
                    public void onSuccess(ProductResponse product) {
                        Log.d(TAG, "onSuccess: ");
                        wishlist.add(product.getProduct());
                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });




                /*NetworkManager.makeREQ(new IREQCallback() {
                    @Override
                    public void onRSPSuccess(String rsp) {
                        wishlist.addAll(DataModel.parseItems(rsp));
                        updateView();
                        wlAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onRSPFail() {
                        Log.d("FAIL", "FAIL");
                    }

                    @Override
                    public String getURL() {
                        return "http://api.livingspaces.com/api/v1/products/" + rawWL;//Services.API.Products.get() + rawWL;
                    }
                });*/
            }
        }
        updateView();
        wlAdapter.notifyDataSetChanged();

    }

    private void checkOnlineWishList(){

        if (Global.Prefs.hasToken()){
            Log.d(TAG, "checkOnlineWishList: ");
            Network.makeGetWishlistREQ(Global.Prefs.getUserId(), new IRequestCallback.Wishlist() {
                @Override
                public void onSuccess(List<String> wishlist) {
                    onlineWishlist = wishlist;
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure: No online wishlist");
                }
            });

            /*NetworkManager.makeWishlistREQ(Global.Prefs.getUserId(), new IREQCallback() {
                @Override
                public void onRSPSuccess(String rsp) {

                    Log.d(TAG, "onRSPSuccess: ");
                }

                @Override
                public void onRSPFail() {

                    Log.d("FAIL", "FAIL");
                }

                @Override
                public String getURL() {
                    return "http://mobileapidev.livingspaces.com/api/Product/getWishlist?customerId=";//Services.API.Products.get() + rawWL;
                }
            });*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_wishlist, container, false);

        if (wishlist != null && onlineWishlist != null){
            syncWishLists();
        }

        if (wishlist == null) wishlist = new ArrayList<>();

        scanProduct = (TextView) rootView.findViewById(R.id.tv_enterCode);
        scanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.FragManager.startFrag(CodeScanFrag.newInstance().forWishList(callback));
            }
        });

        emptyIV = (TextView) rootView.findViewById(R.id.empty_wish_view);

        wishlistFAB = (WishlistFAB) rootView.findViewById(R.id.wishlistFAB);
        wishlistFAB.setCallback(this);

        wlAdapter = new WishlistAdapter(getActivity(), this, wishlist);

        rv_wishlist = (SwipeListView) rootView.findViewById(R.id.rv_wishlist);
        mLayoutManager = new LinearLayoutManager(getActivity());;
        rv_wishlist.setLayoutManager(mLayoutManager);

        mBaseSwipeListViewListener = new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
                currentOpenPos = position;
                wlAdapter.setItemOpen(true);
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                currentOpenPos = -1;
                wlAdapter.setItemOpen(false);
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
                Log.d("onMove", String.format("position %d - x %f", position, x));
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                currentOpenPos = position;
                wlAdapter.setItemOpen(true);
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
                currentOpenPos = -1;
                wlAdapter.setItemOpen(false);
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    wishlist.remove(position);
                }
                wlAdapter.notifyDataSetChanged();
            }

        };

        rv_wishlist.setSwipeListViewListener(mBaseSwipeListViewListener);

        rv_wishlist.setAdapter(wlAdapter);

        if (rawWL.isEmpty()) {
            emptyIV.setVisibility(View.VISIBLE);
            scanProduct.setVisibility(View.VISIBLE);
            wishlistFAB.setVisibility(View.GONE);
        }
        return rootView;
    }

    private void syncWishLists(){
        for (int i = 0; i < wishlist.size(); i++){
            for (int j = 0; j < onlineWishlist.size(); j++){

            }
        }
    }


    @Override
    public void onResurface() {
        Log.d("WISHLIST", "onResurface");
        wlAdapter.notifyDataSetChanged();
        updateView();
    }

    @Override
    public boolean setTopRight(ImageView topRight) {
        if (!wishlistFAB.inEditMode()) return false;

        topRight.setRotation(0);
        topRight.setImageResource(R.drawable.ls_w_btn_delete);
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wlAdapter.commitEdit();
                wishlistFAB.setForMenu();
            }
        });

        return true;
    }

    @Override
    public boolean setTopRight(final TextView topRight) {
        if (wishlistFAB.inEditMode()) return false;
        if (wishlist.size() == 0) {
            return false;
        }
        topRight.setText("Share List");
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.shareUrl(getActivity(), wishlist);

                /** Google Analytics - share_list_action_bar */
                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("share_list_action_bar")
                                .build()
                );
            }
        });

        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public String getTitle() {
        return "WISHLIST";
    }

    @Override
    public void onShare() {
        Log.d("WISHLIST FAB", "onShare");
        Utility.shareUrl(getActivity(), wishlist);

        /** Google Analytics - share_list_menu */
        Utility.gaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("ui_action")
                        .setAction("share_list_menu")
                        .build()
        );
    }

    @Override
    public void onScan() {
        Log.d("WISHLIST FAB", "onScan");
        Global.FragManager.startFrag(CodeScanFrag.newInstance().forWishList(this));
    }

    @Override
    public void onEdit() {
        Log.d("WISHLIST FAB", "onEdit");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 300);
        rv_wishlist.setLayoutParams(lp);
        rv_wishlist.closeOpenedItems();
        rv_wishlist.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
        wlAdapter.setForEdit(true);
    }

    @Override
    public void onEditCancel() {
        Log.d("WISHLIST FAB", "onEditCancel");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        rv_wishlist.setLayoutParams(lp);
        wlAdapter.setForEdit(false);
    }

    @Override
    public void onDeleteAll() {
        Log.d("WISHLIST FAB", "onDeleteAll");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        rv_wishlist.setLayoutParams(lp);
        wlAdapter.deleteAll();
        updateView();
    }

    @Override
    public List<Item> getWishlist() {
        return wishlist;
    }

    @Override
    public void updateView() {
        if (wishlist.size() == 0) {
            emptyIV.setVisibility(View.VISIBLE);
            scanProduct.setVisibility(View.VISIBLE);
            wishlistFAB.setVisibility(View.GONE);
            rv_wishlist.setVisibility(View.GONE);
        } else {
            emptyIV.setVisibility(View.GONE);
            scanProduct.setVisibility(View.GONE);
            wishlistFAB.setVisibility(View.VISIBLE);
            rv_wishlist.setVisibility(View.VISIBLE);
        }
        if(getActivity()!= null) {
            ((MainActivity) getActivity()).refreshActionBar();
        }
    }

    @Override
    public void onEditStateChanged(boolean edit) {
        if(!edit){
            rv_wishlist.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        }
    }

    @Override
    public void closeItem() {
        if(wlAdapter.getItemCount() > 0) {
            currentOpenPos = -1;
            wlAdapter.setItemOpen(false);
            rv_wishlist.closeOpenedItems();
        }
    }
}