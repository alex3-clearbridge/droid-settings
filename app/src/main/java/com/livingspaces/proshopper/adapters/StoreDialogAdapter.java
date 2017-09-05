package com.livingspaces.proshopper.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.Store;
import com.livingspaces.proshopper.views.LSTextView;

import java.util.List;

/**
 * Created by alexeyredchets on 2017-08-21.
 */

public class StoreDialogAdapter extends RecyclerView.Adapter<StoreDialogAdapter.ViewHolder> {

    private static final String TAG = StoreDialogAdapter.class.getSimpleName();
    private List<Store> storeList;
    private Context context;
    private ClickListener clickListener;
    private Drawable d_add;

    public StoreDialogAdapter(Context context, ClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;

        d_add = ContextCompat.getDrawable(context, R.drawable.ls_g_btn_add);
    }

    public void updateAdapter(List<Store> storeList){
        Log.d(TAG, "updateAdapter: ");
        this.storeList = storeList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_store_dialog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        Store store = storeList.get(position);

        holder.tv_storeName.setText(store.getName());
        holder.tv_storeAddress.setText(store.getStoreAddresses().getAddress());
        holder.tv_storeCity.setText(store.getStoreAddresses().getCity() + ", ");
        holder.tv_storeState.setText(store.getStoreAddresses().getState() + " ");
        holder.tv_storeZip.setText(store.getZipCode());
        holder.iv_add.setImageDrawable(d_add);

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return storeList != null ? storeList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        LSTextView tv_storeName, tv_storeAddress, tv_storeCity, tv_storeState,tv_storeZip;
        ImageView iv_add;

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ViewHolder: ");

            tv_storeName = (LSTextView)itemView.findViewById(R.id.tv_storename_storedialog);
            tv_storeAddress = (LSTextView)itemView.findViewById(R.id.tv_storeaddress_storedialog);
            tv_storeCity = (LSTextView)itemView.findViewById(R.id.tv_storecity_storedialog);
            tv_storeState = (LSTextView)itemView.findViewById(R.id.tv_storestate_storedialog);
            tv_storeZip = (LSTextView)itemView.findViewById(R.id.tv_storezip_storedialog);
            iv_add = (ImageView) itemView.findViewById(R.id.iv_storedialog);

            Typeface fontLight = Typeface.createFromAsset(context.getAssets(), "SourceSansPro-Light.ttf");
            Typeface fontBold = Typeface.createFromAsset(context.getAssets(), "SourceSansPro-Bold.otf");
            tv_storeName.setTypeface(fontBold);
            tv_storeAddress.setTypeface(fontLight);
            tv_storeCity.setTypeface(fontLight);
            tv_storeState.setTypeface(fontLight);
            tv_storeZip.setTypeface(fontLight);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(storeList.get(getAdapterPosition()));
        }
    }

    public interface ClickListener {
        void onClick(Store store);
    }
}
