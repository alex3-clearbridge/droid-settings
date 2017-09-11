package com.livingspaces.proshopper.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.livingspaces.proshopper.R;
//import com.livingspaces.proshopper.data.Item;
import com.livingspaces.proshopper.fragments.ItemDetailFrag;
import com.livingspaces.proshopper.interfaces.IWishlistCallback;
import com.livingspaces.proshopper.data.response.Product;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.sizes.PhoneSizes;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rugvedambekar on 15-09-21.
 */
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishHolder> {
    private static final String TAG = WishlistAdapter.class.getSimpleName();

    private List<Product> items;
    private final List<Product> itemsToDelete;

    private IWishlistCallback WLCallback;
    private boolean inEditMode;
    private boolean itemOpen;
    private Context context;

    private long mLastClickTime = 0;

    public WishlistAdapter(Context context, IWishlistCallback cb, List<Product> wItems) {
        this.context = context;
        this.items = wItems;// == null ? new ArrayList<Product>() : wItems;
        this.itemsToDelete = new ArrayList<>();
        this.WLCallback = cb;
        this.itemOpen = false;

    }

    public void updateAdapter(List<Product> productList){
        this.items = productList;
        notifyDataSetChanged();
    }

    @Override
    public WishHolder onCreateViewHolder(ViewGroup pView, int i) {

        View view = LayoutInflater.from(pView.getContext()).inflate(R.layout.item_wish, pView, false);
        return new WishHolder(view);
    }


    @Override
    public void onBindViewHolder(WishHolder wishHolder, int i) {
        wishHolder.bindItem(i);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void deleteAll() {
        itemOpen =false;
        items.clear();
        itemsToDelete.clear();
        Global.Prefs.clearWishList();
        notifyDataSetChanged();
        WLCallback.updateView();
    }

    public void setItemOpen(boolean open)
    {
        itemOpen = open;
    }

    public void setForEdit(boolean forEdit) {
        WLCallback.onEditStateChanged(forEdit);
        inEditMode = forEdit;
        notifyDataSetChanged();
        itemsToDelete.clear();
        WLCallback.updateView();
    }
    public void commitEdit() {
        if (!inEditMode) return;
        inEditMode = false;
        WLCallback.onEditStateChanged(inEditMode);

        for (Product item : itemsToDelete) {
            WLCallback.deleteItem(item.getSku());
            Global.Prefs.editWishItem(item.getSku(), false);
        }
        items.removeAll(itemsToDelete);
        itemsToDelete.clear();
        WLCallback.updateView();

        notifyDataSetChanged();
    }

    public class WishHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Product item;
        private int index;

        private final RelativeLayout container, itemFooter;
        private final LinearLayout wishContainer;
        private final NetworkImageView iv_item;
        private final ImageView iv_edit;
        private final TextView tv_title, tv_sku, tv_cost, tv_delete;

        private final int xThresholdLeft, xThresholdRight;
        private boolean open = false;

        public WishHolder(View itemView) {
            super(itemView);

            container = (RelativeLayout) itemView.findViewById(R.id.front);
            itemFooter = (RelativeLayout) itemView.findViewById(R.id.item_footer);
            wishContainer = (LinearLayout) container.findViewById(R.id.ll_wishItem);

            iv_item = (NetworkImageView) container.findViewById(R.id.niv_wishImg);
            tv_title = (TextView) container.findViewById(R.id.tv_wishTitle);
            tv_sku = (TextView) container.findViewById(R.id.tv_wishSKU);
            tv_cost = (TextView) container.findViewById(R.id.tv_wishCost);

            tv_delete = (TextView) itemView.findViewById(R.id.tv_deleteItem);
            tv_delete.setOnClickListener(onDelete);

            iv_edit = (ImageView) container.findViewById(R.id.ib_editWish);
            iv_edit.setOnClickListener(new View.OnClickListener() {
                boolean markedForDelete = false;

                @Override
                public void onClick(View v) {
                    itemOpen = false;
                    markedForDelete = !markedForDelete;

                    if (markedForDelete) itemsToDelete.add(item);
                    else itemsToDelete.remove(item);

                    iv_edit.setImageResource(markedForDelete ? R.drawable.ls_w_btn_check_01 : R.drawable.ls_w_btn_check_00);
                }
            });

            Layout.setViewSize(tv_delete, PhoneSizes.itemWishDelete, Layout.SizeType.WIDTH);
            xThresholdLeft = -(int) Layout.Calc(PhoneSizes.itemWishDelete).width;

            Layout.setViewSize(iv_edit, PhoneSizes.itemWishEdit, Layout.SizeType.WIDTH);
            xThresholdRight = (int) Layout.Calc(PhoneSizes.itemWishEdit).width;

            wishContainer.setOnClickListener(this);


            itemView.setTag(this);
        }

        public void bindItem(int i) {
            this.item = items.get(i);
            this.index = i;


            if (item.getImages() != null && item.getImages().size() > 0 && item.getImages().get(0) != null && item.getImages().get(0).getImgUrl() != null) {
                Picasso.with(context).load(item.getImages().get(0).getImgUrl()).into(iv_item);

                //iv_item.setImageUrl(item.getImages().get(0).getImgUrl(), NetworkManager.getIMGLoader());
            }
            else {
                iv_item.setDefaultImageResId(R.drawable.ls_w_img_default);
            }
            tv_title.setText(item.getTitle());
            tv_cost.setText("$ " + item.getPrice());
            tv_sku.setText(item.getSku());

            if(items.size()-1 == i) {
                itemFooter.setVisibility(View.VISIBLE);
            }
            else {
                itemFooter.setVisibility(View.GONE);
            }


            iv_edit.setImageResource(R.drawable.ls_w_btn_check_00);
            wishContainer.animate().setDuration(150).x(inEditMode ? xThresholdRight : 0).start();
        }

        private View.OnClickListener onDelete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                Log.d("Wish Adapter ::onDelete", "");
                mLastClickTime = SystemClock.elapsedRealtime();
                items.remove(index);
                Global.Prefs.editWishItem(item.getSku(), false);
                notifyItemRemoved(index);
                notifyDataSetChanged();
                WLCallback.updateView();
                WLCallback.closeItem();
            }
        };

        @Override
        public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            Log.d(TAG, "onClick");
            if (inEditMode) iv_edit.callOnClick();
            else if(itemOpen){
                WLCallback.closeItem();
            }
            else {
                Global.FragManager.stackFrag(ItemDetailFrag.newInstance(item).fromWishlist(WLCallback));
                WLCallback.onEditStateChanged(false);
            }
        }
    }
}

