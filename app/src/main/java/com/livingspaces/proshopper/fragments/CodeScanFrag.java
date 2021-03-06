package com.livingspaces.proshopper.fragments;


import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.livingspaces.proshopper.R;
//import com.livingspaces.proshopper.data.Item;
import com.livingspaces.proshopper.data.response.Store;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.interfaces.IWishlistCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.data.response.Product;
import com.livingspaces.proshopper.data.response.ProductResponse;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.Utility;
import com.livingspaces.proshopper.utilities.sizes.PhoneSizes;
import com.livingspaces.proshopper.views.ActionBar;
import com.livingspaces.proshopper.views.BarcodeDialog;
import com.livingspaces.proshopper.views.CameraPreview;
import com.google.android.gms.analytics.HitBuilders;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CodeScanFrag extends BaseStackFrag implements BarcodeDialog.ICallback, CameraPreview.ICallback, DialogFrag.IZipCallback, StoreDialog.ICallback {
    private static final String TAG = CodeScanFrag.class.getSimpleName();

    private Activity mActivity;
    private CameraPreview camPreview;
    private TextView tv_cancelFetch, tv_enterBarcode;
    private ImageView iv_target;
    private LinearLayout layout_enableCameraAccess;
    private View overlay, dialogHelp, rootView;
    private DialogFrag mDialogFrag;
    private Bundle args;
    private ProductResponse mProduct;
    private StoreDialog mStoreDialog;
    private Store mStore;

    private BarcodeDialog dialogBarcode;
    private IWishlistCallback WLCallback;

    private boolean isAccessDenied;

    private boolean reqInProgress, forWishlist, isDialogFragShowing, isStoreDialogShowing;
    private int CAMERA_CODE_SCAN_REQUEST_CODE;

    public static CodeScanFrag newInstance() {
        return new CodeScanFrag();
    }

    public CodeScanFrag() {
    }

    public CodeScanFrag forWishList(IWishlistCallback cb) {
        forWishlist = true;
        WLCallback = cb;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        camPreview = new CameraPreview(getActivity(), this);
        rootView = inflater.inflate(R.layout.fragment_code_scan, container, false);

        mActivity = getActivity();

        overlay = rootView.findViewById(R.id.shade_scanFrag);
        overlay.setVisibility(View.GONE);

        iv_target = (ImageView) rootView.findViewById(R.id.iv_target);
        layout_enableCameraAccess = (LinearLayout) rootView.findViewById(R.id.layout_enableCameraAccess);
        dialogBarcode = (BarcodeDialog) rootView.findViewById(R.id.dialog_code);
        dialogHelp = rootView.findViewById(R.id.dialog_help);

        dialogBarcode.setCallback(this);
        dialogBarcode.registerHelpDialog(dialogHelp);

        mDialogFrag = new DialogFrag();
        mDialogFrag.setZipCallback(this);
        args = new Bundle();

        mStoreDialog = new StoreDialog();
        mStoreDialog.setCallback(this);

        tv_enterBarcode = (TextView) rootView.findViewById(R.id.tv_enterCode);
        tv_enterBarcode.setOnClickListener(v -> {
            if (camPreview.pause() || isAccessDenied) {
                dialogBarcode.show(false);
                overlay(true);
            }
        });

        Layout.setViewSize(tv_enterBarcode, PhoneSizes.dialogBarcode, Layout.SizeType.WIDTH);

        rootView.post(() -> {
            Layout.setViewSize(dialogBarcode, PhoneSizes.dialogBarcode);
            Layout.setViewSize(dialogHelp, PhoneSizes.dialogBarcode, Layout.SizeType.WIDTH);

            dialogBarcode.setVisibility(View.GONE);
            dialogHelp.setVisibility(View.GONE);
        });

        if (canAccessCamera()) {
            onStart();
            layout_enableCameraAccess.setVisibility(View.GONE);
            isAccessDenied = false;
        } else {
            if (this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Log.e(TAG, "Camera Access Denied");

                // Access Denied
                onStop();
                layout_enableCameraAccess.setVisibility(View.VISIBLE);
                isAccessDenied = true;
            } else {
                CAMERA_CODE_SCAN_REQUEST_CODE = 133;
                Log.e(TAG, "requestPermissions: " + Integer.toString(CAMERA_CODE_SCAN_REQUEST_CODE));

                // Request Permission
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE_SCAN_REQUEST_CODE);
            }
        }

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e(TAG, "onRequestPermissionsResult: " + Integer.toString(requestCode));

        Log.e(TAG, "GrantResults: " + Integer.toString(grantResults[0])
                        + " Permissions: " + permissions[0]
        );
        if (requestCode == CAMERA_CODE_SCAN_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.e(TAG, "onRequestPermissionsResult GRANTED");
                // permission was granted, yay! Do the
                // camera-related task you need to do.
                onStart();
                layout_enableCameraAccess.setVisibility(View.GONE);
                isAccessDenied = false;
            } else {
                Log.e(TAG, "onRequestPermissionsResult DENIED");

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                onStop();
                layout_enableCameraAccess.setVisibility(View.VISIBLE);
                isAccessDenied = true;
            }
            return;
        }
    }

    private Boolean canAccessCamera() {
        return ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResurface() {
        Log.d(TAG, "onResurface :: Starting Surface");
        camPreview.surfaceCreated(null);
        camPreview.resume();
        overlay(false);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop :: Destroying Surface");
        camPreview.surfaceDestroyed(null);
        super.onStop();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart :: Starting Surface");
        camPreview.surfaceCreated(null);
        super.onStart();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        if (nextAnim == 0) return null;
        Animation animation = AnimationUtils.loadAnimation(getContext(), nextAnim);
        if (animation != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FrameLayout camContainer = (FrameLayout) rootView.findViewById(R.id.container_camera);
                            camContainer.addView(camPreview);
                        }
                    }, 500);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        return animation;
    }

    private void showError() {
        flip(true);
    }

    private void flip(final boolean toError) {
        iv_target.animate().setDuration(250).alpha(0).withEndAction(() -> {
            iv_target.setImageResource(toError ? R.drawable.ls_s_img_scan_camera_not_found : R.drawable.ls_s_img_scan_camera);
            iv_target.animate().setDuration(250).alpha(1).start();
        }).start();
        tv_enterBarcode.animate().setDuration(250).scaleY(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                tv_enterBarcode.setText(toError ? "Product not found" : "Enter Product ID or Barcode");
                tv_enterBarcode.setBackgroundResource(toError ? R.drawable.button_error : R.drawable.button);
                tv_enterBarcode.animate().setDuration(250).scaleY(1).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (toError) {
                            tv_enterBarcode.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    flip(false);
                                }
                            }, 2000);

                        } else camPreview.resume();
                    }
                });
            }
        });
    }

    public void overlay(final boolean show) {
        if (overlay == null || (show && isViewShowing(overlay)) || (!show && !isViewShowing(overlay)))
            return;

        if (show) overlay.setVisibility(View.INVISIBLE);
        overlay.animate().setDuration(500).alpha(show ? 1 : 0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
                if (show) overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) overlay.setVisibility(View.GONE);
            }
        }).start();
    }

    private boolean isViewShowing(View view) {
        return view.getVisibility() != View.GONE;
    }

    @Override
    public String getTitle() {
        return NavigationFrag.NavItem.SCAN.title();
    }

    @Override
    public boolean handleBackPress() {
        if (isViewShowing(dialogHelp)) {
            dialogBarcode.hideHelp();
            return true;
        } else if (isViewShowing(dialogBarcode)) {
            dialogBarcode.hide();
            camPreview.resume();
            overlay(false);
            return true;
        }
        else if (isDialogFragShowing){
            mDialogFrag.dismiss();
            isDialogFragShowing = false;
            camPreview.resume();
            overlay(false);
            return true;
        }
        else if (isStoreDialogShowing){
            mStoreDialog.dismiss();
            isStoreDialogShowing = false;
            camPreview.resume();
            overlay(false);
            return true;
        }
        else return super.handleBackPress();
    }

    @Override
    public void onSubmit(final String input) {
        Log.d(TAG, "onSubmit: " + dialogBarcode.getInput());

        if (dialogBarcode.getInput().length() == 20){
            onBarcodeFound(dialogBarcode.getInput());
            return;
        }

        if (reqInProgress) return;

        reqInProgress = true;

        Network.makeGetProductREQ(dialogBarcode.getInput(), new IRequestCallback.Product() {
            @Override
            public void onSuccess(ProductResponse product) {
                Log.d(TAG, "onSubmit::onSuccess: ");
                reqInProgress = false;
                if (getActivity() == null) return;

                Product item = product.getProduct();
                if (item == null) {
                    Log.d(TAG, "onSubmit::onSuccess: item == null");
                    onFailure("null message");
                    return;
                }

                mProduct = product;

                dialogBarcode.hide();
                //startFragForItem(item);

                /* Google Analytics - enter_barcode_success */

                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("enter_barcode_success")
                                .setLabel(input)
                                .build()
                );
                checkUserZip();
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "onSubmit::onFailure: " + message);
                reqInProgress = false;
                if (getActivity() == null) //|| dialogBarcode.reqWasCanceled())
                    return;

                dialogBarcode.showError();

                /* Google Analytics - enter_barcode_fail */

                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("enter_barcode_fail")
                                .setLabel(input)
                                .build()
                );
            }
        });
    }

    private void startFragForItem(Product item) {

        Log.d(TAG, item.toString());
        if (forWishlist)
            Global.FragManager.stackFrag(ItemDetailFrag.newInstance(item).forWishlist(WLCallback));
        else
            Global.FragManager.stackFrag(WebViewFrag.newInstance(Services.URL.Product.get()).withProduct(item));
    }

    @Override
    public void onCancel() {
        overlay(false);
        dialogBarcode.hide();
        camPreview.resume();
    }

    @Override
    public void onCancelFetch() {
        onCancel();
    }

    @Override
    public void onBarcodeFound(final String barcodeData) {
        if (reqInProgress) return;

        Log.d(TAG, "onBarcodeFound: " + barcodeData);

        reqInProgress = true;
        dialogBarcode.show(true);
        overlay(true);

        new Handler().postDelayed(() -> Network.makeGetProductREQ(barcodeData, new IRequestCallback.Product() {
            @Override
            public void onSuccess(ProductResponse product) {
                Log.d(TAG, "onBarcodeFound::onSuccess ");
                reqInProgress = false;
                if (getActivity() == null) return;

                Product item = product.getProduct();
                if (item == null) {
                    onFailure("null message");
                    return;
                }

                mProduct = product;
                dialogBarcode.hide();
                //startFragForItem(item);

                if (forWishlist) {
                        /** Google Analytics - scan_wishlist_success */

                    Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("ui_action")
                                    .setAction("scan_wishlist_success")
                                    .setLabel(barcodeData)
                                    .build()
                    );
                } else {
                        /** Google Analytics - scan_success */

                    Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("ui_action")
                                    .setAction("scan_success")
                                    .setLabel(barcodeData)
                                    .build()
                    );
                }

                checkUserZip();
            }

            @Override
            public void onFailure(String message) {
                reqInProgress = false;
                if (getActivity() == null || dialogBarcode.reqWasCanceled()) return;
                dialogBarcode.showError();
                overlay(false);
                showError();

                if (forWishlist) {

                        /** Google Analytics - scan_wishlist_fail */

                    Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("ui_action")
                                    .setAction("scan_wishlist_fail")
                                    .setLabel(barcodeData)
                                    .build()
                    );
                }
                else {

                    /** Google Analytics - scan_fail */

                    Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("ui_action")
                                    .setAction("scan_fail")
                                    .setLabel(barcodeData)
                                    .build()
                    );
                }
            }
        }), 1000);

    }

    private void checkUserZip(){
        Log.d(TAG, "checkUserZip: ");
        if (!Global.Prefs.hasUserZip() || Global.Prefs.getUserZip().isEmpty()){
            showDialogFrag();
        }
        else {
            checkStoreId();
        }
    }

    private void checkStoreId(){
        Log.d(TAG, "checkStoreId: ");
        if (mProduct.getCurStoreId() != null && !mProduct.getCurStoreId().isEmpty()){
            Global.Prefs.saveCurrentStore(mProduct.getCurStoreId());
            startFragForItem(mProduct.getProduct());
        }
        else if (Global.Prefs.hasStore()){
            Global.Prefs.saveCurrentStore(Global.Prefs.getStore().getId());
            startFragForItem(mProduct.getProduct());
        }
        else {
            showStoreDialog();
        }
    }

    private void showDialogFrag(){
        Log.d(TAG, "showDialogFrag: ");
        isDialogFragShowing = true;
        overlay(true);
        args.putString("case", "showZip");
        mDialogFrag.setArguments(args);
        mDialogFrag.show(getFragmentManager(), "dialogFragment");
    }

    private void showStoreDialog(){
        isStoreDialogShowing = true;
        overlay(true);
        mStoreDialog.show(getFragmentManager(), "storeDialogFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        overlay(false);
        dialogBarcode.hideWithOutAnimation();
        camPreview.resume();
        dialogBarcode.cancelREQ();
    }

    @Override
    public void onResume() {
        super.onResume();
        camPreview.surfaceCreated(null);
        camPreview.resume();
        overlay(false);
    }

    private boolean isGpsAvailable(){
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onZipOk(String zip) {
        isDialogFragShowing = false;
        Global.Prefs.saveUserZip(zip);
        mDialogFrag.dismiss();
        checkStoreId();
    }

    @Override
    public void onZipCancel() {
        isDialogFragShowing = false;
        overlay(false);
        mDialogFrag.dismiss();
        camPreview.resume();
    }

    @Override
    public void onZipCreated() {
        mDialogFrag.setCont();
    }

    @Override
    public void onStoreSelected(Store store) {
        isStoreDialogShowing = false;
        Global.Prefs.saveStore(store);
        Global.Prefs.saveCurrentStore(store.getId());
        overlay(false);
        mStoreDialog.dismiss();
        startFragForItem(mProduct.getProduct());
    }

    @Override
    public void onStoreCancel() {
        isStoreDialogShowing = false;
        mStoreDialog.dismiss();
        overlay(false);
        camPreview.resume();
    }
}