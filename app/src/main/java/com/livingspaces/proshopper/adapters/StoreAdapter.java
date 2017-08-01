package com.livingspaces.proshopper.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.Store;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.utilities.sizes.PhoneSizes;
import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rugvedambekar on 15-09-26.
 */
public class StoreAdapter extends BaseAdapter {
    private final String TAG = "[StoreAdapter]";

    public interface Callback {
        void onStoreClick();
    }

    private LayoutInflater inflater;
    private Context context;
    private Callback callback;
    private ArrayList<Store> storeList;
    private ArrayList<Boolean> storeShowingHrs;

    public StoreAdapter(Context context, Callback callback) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.callback = callback;

        storeList = new ArrayList<>();
        storeShowingHrs = new ArrayList<>();
    }

    public void setStoreList(Store[] objects) {
        if (objects == null) return;
        storeList.clear();
        storeShowingHrs.clear();

        Collections.addAll(storeList, objects);
        storeShowingHrs = new ArrayList<>(Collections.nCopies(storeList.size(), Boolean.FALSE));

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return storeList.size();
    }

    @Override
    public Object getItem(int position) {
        return storeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = new FrameLayout(context);
        StoreVManager vManager = convertView.getTag() == null ?
                new StoreVManager((FrameLayout) convertView) : (StoreVManager) convertView.getTag();
        vManager.bindStore((Store) getItem(position));

        vManager.position = position;
        if (storeShowingHrs.size() != 0) {
            if (storeShowingHrs.get(position)) {
                vManager.showHrsIgnoreAnimation(true);
            } else {
                vManager.showHrsIgnoreAnimation(false);
            }
        }

        convertView.setTag(vManager);
        return convertView;
    }

    public class StoreVManager implements View.OnClickListener {

        public int position;

        private Store store;
        private boolean showingHrs;
        private float xOut, xIn;

        private final TextView tv_title, tv_addr, tv_cityStateZip, tv_dist;
        private final FrameLayout storeContainer;
        private final RelativeLayout storeView;
        private final View shadeView, storeHrsView;
        private View tv_storeOpenTill;
        private View tv_storeCall, tv_storeDir;
        private View tv_storeNum_top, tv_storeNum_bottom;
        private View btn_centerMap;
        private View iv_storeHrsOverlay;
        private RelativeLayout rl_storeHrs;

        public StoreVManager(final FrameLayout sContainer) {
            storeContainer = sContainer;
            storeView = (RelativeLayout) inflater.inflate(R.layout.item_store, null);
            tv_title = (TextView) storeView.findViewById(R.id.tv_storeTitle);
            tv_addr = (TextView) storeView.findViewById(R.id.tv_storeAddr);
            tv_dist = (TextView) storeView.findViewById(R.id.tv_storeDist);
            tv_cityStateZip = (TextView) storeView.findViewById(R.id.tv_storeCityStateZIP);
            tv_storeOpenTill = storeView.findViewById(R.id.tv_storeOpenTill);
            tv_storeCall = storeView.findViewById(R.id.tv_storeCall);
            tv_storeDir = storeView.findViewById(R.id.tv_storeDir);
            btn_centerMap = storeView.findViewById(R.id.btn_centerMap);

            storeView.setBackgroundResource(android.R.color.background_light);

            shadeView = new View(context);
            shadeView.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            shadeView.setBackgroundColor(Color.BLACK);
            shadeView.setClickable(false);
            shadeView.setAlpha(0);

            storeHrsView = inflater.inflate(R.layout.item_store_hrs, null);
            RelativeLayout.LayoutParams storeHrsParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            storeHrsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            storeHrsView.setLayoutParams(storeHrsParams);
            storeHrsView.setClickable(false);

            tv_storeNum_top = storeHrsView.findViewById(R.id.tv_storeNum_top);
            tv_storeNum_bottom = storeHrsView.findViewById(R.id.tv_storeNum_bottom);
            iv_storeHrsOverlay = storeHrsView.findViewById(R.id.iv_storeHrsOverlay);
            rl_storeHrs = (RelativeLayout) storeHrsView.findViewById(R.id.rl_storeHrs);

            tv_storeOpenTill.setVisibility(View.GONE);
            tv_storeNum_top.setVisibility(View.GONE);
            tv_storeNum_bottom.setVisibility(View.VISIBLE);
            btn_centerMap.setVisibility(View.INVISIBLE);
            iv_storeHrsOverlay.setVisibility(View.VISIBLE);

            sContainer.setBackgroundColor(Layout.Color.backgroundLight);
            sContainer.addView(storeView);
            sContainer.addView(shadeView);
            sContainer.addView(storeHrsView);
            sContainer.setOnClickListener(this);

            Layout.setViewSize(storeHrsView, PhoneSizes.itemStoreHrs, Layout.SizeType.WIDTH);
            Layout.setMargin(storeView, 10);
            Layout.setPadding(rl_storeHrs, 0);

            storeView.setVisibility(View.VISIBLE);

            showingHrs = false;
            postInit();
        }

        private void postInit() {
            storeView.post(new Runnable() {
                @Override
                public void run() {
                    xOut = storeContainer.getWidth();
                    xIn = xOut - storeHrsView.getWidth();

                    Layout.matchViewSize(shadeView, storeContainer, Layout.SizeType.BOTH);
                    Layout.matchViewSize(storeHrsView, storeContainer, Layout.SizeType.HEIGHT);

                    storeHrsView.setVisibility(View.VISIBLE);
                    storeHrsView.setX(xOut);
                }
            });
        }

        public void bindStore(Store s) {
            this.store = s;

            tv_title.setText(store.name);
            tv_addr.setText(store.address);
            tv_dist.setText(store.distance());
            tv_cityStateZip.setText(store.cityStateZip());
            storeHrsView.clearAnimation();

            addClickEvents();

            storeHrsView.setX(xOut);
        }

        private void addClickEvents() {
            if (tv_storeCall != null) {
                tv_storeCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumber = context.getResources().getString(R.string.phoneNumber);
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                        context.startActivity(intent);

                        /** Google Analytics -- call_store */
                        Utility.gaTracker.send(new HitBuilders.EventBuilder().
                                        setCategory("ui_action")
                                        .setAction("call_store")
                                        .setLabel(store.name)
                                        .build()
                        );
                    }
                });
            }
            if (tv_storeDir != null) {
                tv_storeDir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utility.openGoogleMaps(store.latitude, store.longitude, store.address);

                        /** Google Analytics -- store_directions */
                        Utility.gaTracker.send(new HitBuilders.EventBuilder().
                                        setCategory("ui_action")
                                        .setAction("store_directions")
                                        .setLabel(store.name)
                                        .build()
                        );
                    }
                });
            }
        }

        public void showHrs(boolean show) {
            if (showingHrs == show) return;

            storeShowingHrs.set(position, show);
            showingHrs = show;
            storeHrsView.animate().setDuration(250).x(showingHrs ? xIn : xOut).withStartAction(new Runnable() {
                @Override
                public void run() {
                    shadeView.animate().setDuration(250).alpha(showingHrs ? 0.5f : 0).start();
                }
            }).start();
        }

        public void showHrsIgnoreAnimation(boolean show) {

            storeShowingHrs.set(position, show);
            showingHrs = show;
            storeHrsView.animate().setDuration(0).x(showingHrs ? xIn : xOut).withStartAction(new Runnable() {
                @Override
                public void run() {
                    shadeView.animate().setDuration(0).alpha(showingHrs ? 0.5f : 0).start();
                }
            }).start();
        }

        @Override
        public void onClick(View v) {
            showHrs(!showingHrs);
            callback.onStoreClick();

            /** Google Analytics -- store_info */
            Utility.gaTracker.send(new HitBuilders.EventBuilder().
                            setCategory("ui_action")
                            .setAction("store_info")
                            .setLabel(store.name)
                            .build()
            );
        }
    }
}
