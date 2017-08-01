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
import android.widget.ProgressBar;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.adapters.StoreAdapter;
import com.livingspaces.proshopper.data.Store;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreListFrag extends BaseStackFrag implements StoreAdapter.Callback {
    private static final String TAG = StoreListFrag.class.getSimpleName();

    public interface Callback {
        void onTouchStoreListFrag();
    }

    private Callback callback;
    private Store[] stores;

    private FrameLayout rootView;
    private ProgressBar loadingView;
    private ListView lv_stores;
    private StoreAdapter sAdapter;

    public static StoreListFrag newInstance(Store[] s, Callback callback) {
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
        if (stores == null) return;
        Log.e(TAG, "makeStoresList");

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

        if (loadingView != null) {
            rootView.removeView(loadingView);
            loadingView = null;
        }
    }

    public void makeLoadingScreen() {
        loadingView = new ProgressBar(getActivity());
        loadingView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootView.addView(loadingView);
    }

    public void setStores(Store[] s) {
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
