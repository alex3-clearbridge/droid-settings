package com.livingspaces.proshopper.fragments;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.response.Store;
import com.livingspaces.proshopper.interfaces.IEditTextImeBackListener;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.views.ActionBar;
import com.livingspaces.proshopper.views.LSEditText;
import com.google.android.gms.analytics.HitBuilders;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindStoreFrag extends BaseStackFrag implements IEditTextImeBackListener {
    private final String TAG = "[FindStoreFrag]";

    private List<Store> allStores;
    private DoubleSidedFrag doubleFrag;
    private String zipCode = "";
    private LSEditText editText;

    public static FindStoreFrag newInstance() {
        return new FindStoreFrag();
    }

    public FindStoreFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doubleFrag = new DoubleSidedFrag();
        makeStoreRequests();
    }

    private void makeStoreRequests() {
        Log.d(TAG, "makeStoreRequests: ");

        /*if ((zipCode != null && !zipCode.equals(""))){
            Network.makeGetStoreByZip(zipCode, new IRequestCallback.StoreByZip() {
                @Override
                public void onSuccess(Store store) {
                    Log.e(TAG, "onRSPSuccess: " + doubleFrag.isFrontUp());
                    if (doubleFrag.frontFrag != null) doubleFrag.getFrontFrag().setStore(store);
                }

                @Override
                public void onFailure(String message) {

                }
            });
        }
        else {*/
        
        if (zipCode != null && !zipCode.equals("")){
            Network.makeGetStoreByZip(zipCode, new IRequestCallback.Stores() {
                @Override
                public void onSuccess(List<Store> storeList) {
                    Log.e(TAG, "onRSPSuccess: " + doubleFrag.isFrontUp());

                    allStores = storeList;
                    if (doubleFrag.backFrag != null) {
                        Log.d(TAG, "onSuccess: doubleFrag.backFrag != null");
                        doubleFrag.getBackFrag().setStores(allStores);
                    }
                    if (doubleFrag.frontFrag != null) {
                        Log.d(TAG, "onSuccess: doubleFrag.frontFrag != null");
                        doubleFrag.getFrontFrag().setStore(allStores);
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure: ");
                }
            });
        }
        else {
            Network.makeGetStoresREQ(new IRequestCallback.Stores() {
                @Override
                public void onSuccess(List<Store> storeList) {
                    Log.e(TAG, "onRSPSuccess: " + doubleFrag.isFrontUp());

                    allStores = storeList;
                    if (doubleFrag.backFrag != null) {
                        Log.d(TAG, "onSuccess: doubleFrag.backFrag != null");
                        doubleFrag.getBackFrag().setStores(allStores);
                    }
                    if (doubleFrag.frontFrag != null) {
                        Log.d(TAG, "onSuccess: doubleFrag.frontFrag != null");
                        doubleFrag.getFrontFrag().setStore(allStores);
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.d(TAG, "onFailure: ");
                }
            });    
        }
        
        
        //}

        /*NetworkManager.makeREQ(new IREQCallback() {
            @Override
            public void onRSPFail() {
            }

            @Override
            public String getURL() {
                Log.e(TAG, "getURL: " + zipCode);
                if ((zipCode != null && !zipCode.equals(""))) {// || (zipCode != null && Global.Prefs.hasStore()))) {
                    return "http://api.livingspaces.com/api/v1/store/getAllStoresByZip/" + Global.Prefs.getStore().getZipCode();//Services.API.StoresWithZip.getByZip(zipCode);
                } else {
                    return "http://api.livingspaces.com/api/v1/store/getAllStores/";//Services.API.Stores.get();
                }
            }

            @Override
            public void onRSPSuccess(String rsp) {
                allStores = DataModel.parseStores(rsp);

                Log.e(TAG, "onRSPSuccess: " + doubleFrag.isFrontUp());

                if (doubleFrag.frontFrag != null) doubleFrag.getFrontFrag().setStore(allStores);
                if (doubleFrag.backFrag != null) doubleFrag.getBackFrag().setStores(allStores);
            }

        });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_store, container, false);
        doubleFrag.showFront();

        zipCode = ""; // reset
        editText = (LSEditText) rootView.findViewById(R.id.et_zip);
        editText.setOnEditTextImeBackListener(this);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doubleFrag.getFrontFrag().showOverlay();
                editText.setCursorVisible(true);
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                editText.setCursorVisible(false);
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_NULL
                        || (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {

                    zipCode = editText.getText().toString();
                    makeStoreRequests();

                    Log.e(TAG, "onEditorAction - ENTER");
                    doubleFrag.getFrontFrag().updateStoreList();
                    doubleFrag.getFrontFrag().hideOverlay();

                    /** Google Analytics -- search_by_zip */
                    Utility.gaTracker.send(new HitBuilders.EventBuilder().
                                    setCategory("ui_action")
                                    .setAction("search_by_zip")
                                    .setLabel(zipCode)
                                    .build()
                    );
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    clearEditTextFocus();
                    doubleFrag.getFrontFrag().hideOverlay();
                }
            }
        });

        // requesting focus on launch so initial click shows overlay
        editText.requestFocus();

        return rootView;
    }

    private void clearEditTextFocus() {
        editText.setCursorVisible(false);
        Utility.hideSoftKeyboard(editText);
        editText.clearFocus();
    }

    @Override
    public void onImeBack(LSEditText ctrl, String text) {
        ctrl.clearFocus();
        doubleFrag.getFrontFrag().hideOverlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        clearEditTextFocus();
    }

    private long lastClickTime = 0;

    @Override
    public boolean setTopRight(final TextView topRight) {
        topRight.setText("View List");
        topRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prevent multi-clicking
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
                    return;
                }

                lastClickTime = SystemClock.elapsedRealtime();

                final boolean frontUp = doubleFrag.flip();
                ActionBar.animateBlink(topRight, new Runnable() {
                    @Override
                    public void run() {
                        topRight.setText(frontUp ? "View List" : "View Map");
                        clearEditTextFocus();
                    }
                });
            }
        });

        return true;
    }

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.FIND.title();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        doubleFrag.getFrontFrag().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public class DoubleSidedFrag implements MapFrag.Callback, StoreListFrag.Callback {

        private boolean frontUp;
        private MapFrag frontFrag;
        private StoreListFrag backFrag;

        private MapFrag getFrontFrag() {
            if (frontFrag == null) frontFrag = MapFrag.newInstance(null, this);
            return frontFrag;
        }

        private StoreListFrag getBackFrag() {
            if (backFrag == null) backFrag = StoreListFrag.newInstance(allStores, this);
            return backFrag;
        }

        public boolean isFrontUp() {
            return frontUp;
        }

        private void showFront() {
            frontUp = true;

            getChildFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .add(R.id.container_storeSwap, getFrontFrag())
                    .addToBackStack(null)
                    .commit();
        }

        private boolean flip() {
            frontUp = !frontUp;

            BaseStackFrag fragUp = frontUp ? getFrontFrag() : getBackFrag();
            getChildFragmentManager().popBackStack();

            getChildFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .add(R.id.container_storeSwap, fragUp)
                    .addToBackStack(fragUp.getTitle())
                    .commit();

            return frontUp;
        }

        @Override
        public void onTouchMapFrag() {
            clearEditTextFocus();
        }

        @Override
        public void onTouchStoreListFrag() {
            clearEditTextFocus();
        }
    }
}
