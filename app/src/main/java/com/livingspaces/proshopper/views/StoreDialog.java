package com.livingspaces.proshopper.views;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.adapters.StoreDialogAdapter;
import com.livingspaces.proshopper.data.DataModel;
import com.livingspaces.proshopper.data.Store;
import com.livingspaces.proshopper.fragments.DialogFrag;
import com.livingspaces.proshopper.interfaces.IREQCallback;
import com.livingspaces.proshopper.networking.NetworkManager;

/**
 * Created by alexeyredchets on 2017-08-21.
 */

public class StoreDialog extends DialogFragment implements StoreDialogAdapter.ClickListener{

    private RecyclerView mRecyclerView;
    private StoreDialogAdapter mStoreDialogAdapter;
    private LSTextView tv_header;
    private View pd_loading;
    private ICallback callback;

    private boolean isLoading = false;

    public StoreDialog newInstance(){
        return new StoreDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_storelist, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_storelist);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestStores();
    }

    private void requestStores(){

        if (isLoading) return;

        isLoading = true;
        mRecyclerView.setVisibility(View.GONE);
        pd_loading.setVisibility(View.VISIBLE);

        NetworkManager.makeREQ(new IREQCallback() {
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
        });
    }

    public void setCallback(ICallback cb) {
        callback = cb;
    }


    @Override
    public void onClick(Store store) {
        Toast.makeText(getContext(), store.name, Toast.LENGTH_SHORT).show();
    }

    public interface ICallback {
        void onStoreSelected();
    }
}
