package com.livingspaces.proshopper.fragments;


import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

import com.livingspaces.proshopper.R;
//import com.livingspaces.proshopper.data.Item;
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
import com.livingspaces.proshopper.views.BarcodeDialog;
import com.livingspaces.proshopper.views.CameraPreview;
import com.google.android.gms.analytics.HitBuilders;

/**
 * A simple {@link Fragment} subclass.
 */
public class CodeScanFrag extends BaseStackFrag implements BarcodeDialog.ICallback, CameraPreview.ICallback {
    private static final String TAG = CodeScanFrag.class.getSimpleName();

    private Activity mActivity;
    private CameraPreview camPreview;
    private TextView tv_cancelFetch, tv_enterBarcode;
    private ImageView iv_target;
    private LinearLayout layout_enableCameraAccess;
    private View overlay, dialogHelp, rootView;

    private BarcodeDialog dialogBarcode;
    private IWishlistCallback WLCallback;

    private boolean isAccessDenied;

    private boolean reqInProgress, forWishlist;
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

        tv_enterBarcode = (TextView) rootView.findViewById(R.id.tv_enterCode);
        tv_enterBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camPreview.pause() || isAccessDenied) {
                    dialogBarcode.show(false);
                    overlay(true);
                }
            }
        });

        Layout.setViewSize(tv_enterBarcode, PhoneSizes.dialogBarcode, Layout.SizeType.WIDTH);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                Layout.setViewSize(dialogBarcode, PhoneSizes.dialogBarcode);
                Layout.setViewSize(dialogHelp, PhoneSizes.dialogBarcode, Layout.SizeType.WIDTH);

                dialogBarcode.setVisibility(View.GONE);
                dialogHelp.setVisibility(View.GONE);
            }
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
        iv_target.animate().setDuration(250).alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                iv_target.setImageResource(toError ? R.drawable.ls_s_img_scan_camera_not_found : R.drawable.ls_s_img_scan_camera);
                iv_target.animate().setDuration(250).alpha(1).start();
            }
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
        dialogBarcode.cancelREQ();

        return false;
    }

    @Override
    public void onSubmit(final String input) {
        Log.d(TAG, "onSubmit: " + dialogBarcode.getInput());
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

                dialogBarcode.hide();
                startFragForItem(item);

                /* Google Analytics - enter_barcode_success */

                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("enter_barcode_success")
                                .setLabel(input)
                                .build()
                );
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "onSubmit::onFailure: " + message);
                reqInProgress = false;
                if (getActivity() == null || dialogBarcode.reqWasCanceled()) return;

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
/*
        NetworkManager.makeREQ(new IREQCallback() {
            @Override
            public void onRSPSuccess(String rsp) {
                reqInProgress = false;
                if (getActivity() == null || dialogBarcode.reqWasCanceled()) return;

                Item item = DataModel.parseItem(rsp);
                if (item == null) {
                    onRSPFail();
                    return;
                }

                dialogBarcode.hide();
                startFragForItem(item);

                */
/* Google Analytics - enter_barcode_success *//*

                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("enter_barcode_success")
                                .setLabel(input)
                                .build()
                );
            }

            @Override
            public void onRSPFail() {
                reqInProgress = false;
                if (getActivity() == null || dialogBarcode.reqWasCanceled()) return;

                dialogBarcode.showError();

                */
/* Google Analytics - enter_barcode_fail *//*

                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("enter_barcode_fail")
                                .setLabel(input)
                                .build()
                );
            }

            @Override
            public String getURL() {
                return Services.API.Product.get() + dialogBarcode.getInput();
            }
        });
*/
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

        new Handler().postDelayed(new Runnable() {
            public void run() {

                Network.makeGetProductREQ(barcodeData, new IRequestCallback.Product() {
                    @Override
                    public void onSuccess(ProductResponse product) {
                        reqInProgress = false;
                        if (getActivity() == null || dialogBarcode.reqWasCanceled()) return;

                        Product item = product.getProduct();
                        if (item == null) {
                            onFailure("null message");
                            return;
                        }

                        overlay(false);
                        dialogBarcode.hide();
                        startFragForItem(item);

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
                        } else {

                            /** Google Analytics - scan_fail */

                            Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("ui_action")
                                            .setAction("scan_fail")
                                            .setLabel(barcodeData)
                                            .build()
                            );
                        }
                    }
                });
/*
                NetworkManager.makeREQ(new IREQCallback() {
                    @Override
                    public void onRSPSuccess(String rsp) {
                        reqInProgress = false;
                        if (getActivity() == null || dialogBarcode.reqWasCanceled()) return;

                        Product item = DataModel.parseItem(rsp);
                        if (item == null) {
                            onRSPFail();
                            return;
                        }

                        overlay(false);
                        dialogBarcode.hide();
                        startFragForItem(item);

                        if (forWishlist) {
                            */
/** Google Analytics - scan_wishlist_success *//*

                            Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("ui_action")
                                            .setAction("scan_wishlist_success")
                                            .setLabel(barcodeData)
                                            .build()
                            );
                        } else {
                            */
/** Google Analytics - scan_success *//*

                            Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("ui_action")
                                            .setAction("scan_success")
                                            .setLabel(barcodeData)
                                            .build()
                            );
                        }
                    }

                    @Override
                    public void onRSPFail() {
                        reqInProgress = false;
                        if (getActivity() == null || dialogBarcode.reqWasCanceled()) return;
                        dialogBarcode.showError();
                        overlay(false);
                        showError();

                        if (forWishlist) {
                            */
/** Google Analytics - scan_wishlist_fail *//*

                            Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("ui_action")
                                            .setAction("scan_wishlist_fail")
                                            .setLabel(barcodeData)
                                            .build()
                            );
                        } else {
                            */
/** Google Analytics - scan_fail *//*

                            Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("ui_action")
                                            .setAction("scan_fail")
                                            .setLabel(barcodeData)
                                            .build()
                            );
                        }

                    }

                    @Override
                    public String getURL() {
                        return Services.API.Product.get() + barcodeData;
                    }
                });
*/
            }
        }, 1000);

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
}
