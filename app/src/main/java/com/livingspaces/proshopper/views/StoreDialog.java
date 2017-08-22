package com.livingspaces.proshopper.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.adapters.StoreDialogAdapter;
import com.livingspaces.proshopper.data.Store;

/**
 * Created by alexeyredchets on 2017-08-21.
 */

public class StoreDialog extends LinearLayout implements StoreDialogAdapter.ClickListener{

    private RecyclerView mRecyclerView;
    private StoreDialogAdapter mStoreDialogAdapter;
    private ICallback callback;

    public StoreDialog(Context context) {
        super(context);
    }

    public StoreDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public StoreDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize(){
        inflate(getContext(), R.layout.dialog_storelist, this);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_storelist);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mStoreDialogAdapter = new StoreDialogAdapter(getContext(), this);
        mRecyclerView.setAdapter(mStoreDialogAdapter);
    }

    public void updateAdapter(Store[] stores){
        mStoreDialogAdapter.updateAdapter(stores);
    }

    public void setCallback(ICallback cb) {
        callback = cb;
    }


    @Override
    public void onClick(Store store) {

    }

    public interface ICallback {
        void onStoreSelected();
    }
}
