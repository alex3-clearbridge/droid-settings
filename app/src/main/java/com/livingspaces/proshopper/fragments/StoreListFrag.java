package com.livingspaces.proshopper.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.adapters.StoreAdapter;
import com.livingspaces.proshopper.data.response.Store;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreListFrag extends BaseStackFrag implements StoreAdapter.Callback {
    private static final String TAG = StoreListFrag.class.getSimpleName();

    public interface Callback {
        void onTouchStoreListFrag();
    }

    private Callback callback;
    private List<Store> stores;

    private FrameLayout rootView;
    private View loadingView;
    private ListView lv_stores;
    private StoreAdapter sAdapter;

    public static StoreListFrag newInstance(List<Store> s, Callback callback) {
        StoreListFrag slf = new StoreListFrag(callback);
        slf.stores = s;
        return slf;
    }

    public StoreListFrag(Callback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (FrameLayout) inflater.inflate(R.layout.fragment_storelist, container, false);
        lv_stores = (ListView) rootView.findViewById(R.id.lv_stores);
        loadingView = rootView.findViewById(R.id.pBar_frag_storeList);

        if (stores != null) {
            makeStoresList();
            setStores(stores);
        } else {
            makeLoadingScreen();
        }

        Log.d(TAG, "onCreateView");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lv_stores.getAdapter() == null) makeStoresList();
    }

    private void makeStoresList() {
        Log.e(TAG, "makeStoresList");
        if (stores == null) return;

        sAdapter = new StoreAdapter(getActivity(), this);
        lv_stores.setAdapter(sAdapter);
        lv_stores.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Log.d(TAG, "onScrollStateChanged:" + scrollState);
                callback.onTouchStoreListFrag();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                for (int i = firstVisibleItem; i < visibleItemCount; i++) {
//                    ((StoreAdapter.StoreVManager) view.getChildAt(i).getTag()).showHrs(false);
//                }
            }
        });

        if (loadingView != null) loadingView.setVisibility(View.GONE);
    }

    public void makeLoadingScreen() {
        if (loadingView != null) loadingView.setVisibility(View.VISIBLE);
    }

    public void setStores(List<Store> s) {
        stores = s;
        if (sAdapter != null) sAdapter.setStoreList(s);
    }

    @Override
    public String getTitle() {
        return "Find A Store";
    }

    @Override
    public void onStoreClick() {
        callback.onTouchStoreListFrag();
    }

}
