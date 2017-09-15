package com.livingspaces.proshopper.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.adapters.StoreDialogAdapter;
import com.livingspaces.proshopper.data.response.Store;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.views.LSEditText;
import com.livingspaces.proshopper.views.LSTextView;

import java.util.List;

/**
 * Created by alexeyredchets on 2017-08-21.
 */

public class StoreDialog extends DialogFragment implements StoreDialogAdapter.ClickListener{

    private RecyclerView mRecyclerView;
    private StoreDialogAdapter mStoreDialogAdapter;
    private LSTextView tv_header;
    private LSEditText et_zip;
    private View pd_loading;
    private ICallback callback;
    private String zipCode;

    private boolean isLoading = false;

    public StoreDialog newInstance(){
        return new StoreDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_storelist, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_storelist);
        et_zip = (LSEditText) view.findViewById(R.id.et_choose_zip);
        et_zip.setText("");
        et_zip.setOnClickListener(v -> et_zip.setCursorVisible(true));

        et_zip.setOnEditorActionListener((v, actionId, event) -> {
            et_zip.setCursorVisible(false);
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_NULL
                    || (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {

                zipCode = et_zip.getText().toString();
                makeStoreRequests(zipCode);

                /** Google Analytics -- search_by_zip */
                Utility.gaTracker.send(new HitBuilders.EventBuilder().
                        setCategory("ui_action")
                        .setAction("search_by_zip")
                        .setLabel(zipCode)
                        .build()
                );
            }
            return false;
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mStoreDialogAdapter = new StoreDialogAdapter(getContext(), this);
        mRecyclerView.setAdapter(mStoreDialogAdapter);

        tv_header = (LSTextView)view.findViewById(R.id.tv_head_storelist_dialog);
        pd_loading = view.findViewById(R.id.pBar_dialog_store);
        pd_loading.setVisibility(View.GONE);

        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Bold.otf");
        tv_header.setTypeface(fontBold);

        return view;
    }

    private void makeStoreRequests(String zip){
        mRecyclerView.setVisibility(View.GONE);
        pd_loading.setVisibility(View.VISIBLE);

        Network.makeGetStoreByZip(zip, new IRequestCallback.Stores() {
            @Override
            public void onSuccess(List<Store> storeList) {
                if (storeList == null) {
                    onFailure("null message");
                    return;
                }
                isLoading = false;
                pd_loading.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mStoreDialogAdapter.updateAdapter(storeList, true);
            }

            @Override
            public void onFailure(String message) {
                isLoading = false;
                pd_loading.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isLoading = true;
        mRecyclerView.setVisibility(View.GONE);
        pd_loading.setVisibility(View.VISIBLE);

        requestStores();
    }

    private void requestStores() {

        mRecyclerView.setVisibility(View.GONE);
        pd_loading.setVisibility(View.VISIBLE);

        Network.makeGetStoresREQ(new IRequestCallback.Stores() {
            @Override
            public void onSuccess(List<Store> storeList) {
                if (storeList == null) {
                    onFailure("null message");
                    return;
                }
                isLoading = false;
                pd_loading.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mStoreDialogAdapter.updateAdapter(storeList, false);
            }

            @Override
            public void onFailure(String message) {
                isLoading = false;
                pd_loading.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

        /*NetworkManager.makeREQ(new IREQCallback() {
            @Override
            public void onRSPSuccess(String rsp) {

                Store[] stores = DataModel.parseStores(rsp);
                if (stores == null) {
                    onRSPFail();
                    return;
                }
                isLoading = false;
                pd_loading.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mStoreDialogAdapter.updateAdapter(stores);
            }

            @Override
            public void onRSPFail() {
                isLoading = false;
                pd_loading.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public String getURL() {
                return "http://api.livingspaces.com/api/v1/store/getAllStores";
            }
        });*/
    //}



    public void setCallback(ICallback cb) {
        callback = cb;
    }


    @Override
    public void onClick(Store store) {
        callback.onStoreSelected(store);
    }

    public interface ICallback {
        void onStoreSelected(Store store);
    }
}
