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
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.interfaces.IWishlistCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.data.response.MessageResponse;
import com.livingspaces.proshopper.data.response.Product;
import com.livingspaces.proshopper.data.response.ProductResponse;
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
    private List<Product> localWishlist;
    private List<Product> onlineWishlist;

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

        if (wishlist == null) wishlist = new ArrayList<>();

        rawWL = Global.Prefs.getWishListRAW();
        if (rawWL == null) rawWL = "";

        if (!Global.Prefs.hasToken()) {
            // user not logged in
            if (!rawWL.isEmpty()) {
                getProduct();
            }
            else {
                localWishlist = null;
            }
        }
        else {

            if (!rawWL.isEmpty()) {
                addToWishlist();
            }
            else {
                localWishlist = null;
                getOnlineList();
            }
        }

    }

    private void getProduct(){
        String[] arr = rawWL.split(",");

        for (int i = 0; i < arr.length; i++){
            Network.makeGetProductREQ(arr[i], new IRequestCallback.Product() {
                @Override
                public void onSuccess(ProductResponse product) {
                    Log.d(TAG, "onSuccess: local");
                    localWishlist.add(product.getProduct());
                }

                @Override
                public void onFailure(String message) {
                    Log.d("FAIL", "FAIL");
                }
            });

            if (i+1 == arr.length) {
                update();
                if (localWishlist != null) this.wishlist = localWishlist;
            }
        }
    }

    private void addToWishlist(){

        String[] arr = rawWL.split(",");
        // load product info for local wishlist
        for (int i = 0; i < arr.length; i++) {
            Network.makeAddToWishREQ(arr[i], new IRequestCallback.Message() {
                @Override
                public void onSuccess(MessageResponse response) {
                    Log.d(TAG, "updateOnlineWishlist::onSuccess: " + response.getMessage());
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "updateOnlineWishlist::onFailure: " + message);
                }
            });

            if (i+1 == arr.length) getOnlineList();
        }


    }

    private void getOnlineList(){

        Network.makeGetWishlistREQ(Global.Prefs.getUserId(), new IRequestCallback.Wishlist() {
            @Override
            public void onSuccess(List<Product> wishlist) {
                Log.d(TAG, "onSuccess: online");
                onlineWishlist = wishlist;
                saveWishlist();
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "onFailure: No online wishlist");
                update();
            }

        });
    }

    private void saveWishlist(){
        this.wishlist = onlineWishlist;
        Global.Prefs.clearWishList();
        for (int i = 0; i < onlineWishlist.size(); i++){
            Global.Prefs.editWishItem(onlineWishlist.get(i).getSku(), true);
        }
        update();
    }

    private void update(){
        updateView();
        if (wlAdapter != null) wlAdapter.updateAdapter(wishlist);
    }


            /*Log.d(TAG, "checkOnlineWishList: ");
            Network.makeGetWishlistREQ(Global.Prefs.getUserId(), new IRequestCallback.Wishlist() {
                @Override
                public void onSuccess(List<Product> wishlist) {
                    Log.d(TAG, "onSuccess: online");
                    onlineWishlist = wishlist;
                    checkLocalWishList();
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure: No online wishlist");
                    checkLocalWishList();
                }
            });
        }
        else {
            checkLocalWishList();
        }*/


    /*private void checkLocalWishList(){

        if (rawWL.isEmpty()) syncWishLists();
        else {

            if (onlineWishlist != null && !onlineWishlist.isEmpty()){
                // compare local and online wishlists
                for (int i = 0; i < onlineWishlist.size(); i++){
                    if (rawWL.contains(onlineWishlist.get(i).getSku())){
                        Log.d(TAG, "checkLocalWishList: has Duplicates");
                        onlineWishlist.remove(i);
                        i--;
                    }
                    else {
                        Log.d(TAG, "checkLocalWishList: has not duplicates");
                    }
                }
            }

            Log.d(TAG, "onCreate: !rawWL.isEmpty()");

            localWishlist = new ArrayList<>();
            String[] arr = rawWL.split(",");
            // load product info for local wishlist
            for (String anArr : arr) {
                Network.makeGetProductREQ(anArr, new IRequestCallback.Product() {
                    @Override
                    public void onSuccess(ProductResponse product) {
                        Log.d(TAG, "onSuccess: local");
                        localWishlist.add(product.getProduct());
                        if (localWishlist.size() == arr.length) {
                            syncWishLists();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.d("FAIL", "FAIL");
                    }
                });
            }
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_wishlist, container, false);
        Log.d(TAG, "onCreateView: ");

        //if (wishlist == null) wishlist = new ArrayList<>();

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


   /* private void syncWishLists(){

        if (localWishlist != null && onlineWishlist != null){
            Set<Product> newWishlistSet = new HashSet<>(localWishlist);
            newWishlistSet.addAll(onlineWishlist);

            wishlist = new ArrayList<>(newWishlistSet);
            wlAdapter.updateAdapter(wishlist);
            Global.Prefs.clearWishList();
            *//*for (int i = 0; i < wishlist.size(); i++){
                Global.Prefs.editWishItem(wishlist.get(i).getSku(), true);
            }*//*
        }
        else if (localWishlist == null){
            if (onlineWishlist == null){
                wishlist = new ArrayList<>();
            }
            else {
                wishlist = onlineWishlist;
            }
        }
        else {
            wishlist = localWishlist;
        }
        if (!wishlist.isEmpty()) {
            updateOnlineWishlist();
            Global.Prefs.clearWishList();
            for (int i = 0; i < wishlist.size(); i++){
                Global.Prefs.editWishItem(wishlist.get(i).getSku(), true);
            }
            updateView();
            if (wlAdapter != null) wlAdapter.updateAdapter(wishlist);
        }

    }*/

    /*private void updateOnlineWishlist(){
        if (!Global.Prefs.hasToken()) return;

        for (int i = 0; i < wishlist.size(); i++){
            Network.makeAddToWishREQ(wishlist.get(i).getSku(), new IRequestCallback.Message() {
                @Override
                public void onSuccess(MessageResponse response) {
                    Log.d(TAG, "updateOnlineWishlist::onSuccess: " + response.getMessage());
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "updateOnlineWishlist::onFailure: " + message);
                }
            });
        }
    }*/

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
        if (wishlist != null && wishlist.size() == 0) {
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
    public List<Product> getWishlist() {
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