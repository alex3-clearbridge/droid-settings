package com.livingspaces.proshopper.views;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.livingspaces.proshopper.MainActivity;
import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.data.response.MessageResponse;
import com.livingspaces.proshopper.fragments.AccountFrag;
import com.livingspaces.proshopper.fragments.BaseStackFrag;
import com.livingspaces.proshopper.fragments.LoginFrag;
import com.livingspaces.proshopper.fragments.NavigationFrag;
import com.livingspaces.proshopper.fragments.WebViewFrag;
import com.livingspaces.proshopper.interfaces.IRequestCallback;
import com.livingspaces.proshopper.networking.Network;
import com.livingspaces.proshopper.networking.Services;
import com.livingspaces.proshopper.utilities.Global;
import com.livingspaces.proshopper.utilities.Utility;
import com.google.android.gms.analytics.HitBuilders;

/**
 * Created by rugvedambekar on 15-09-21.
 */
public class ActionBar extends RelativeLayout {

    private static final String TAG = ActionBar.class.getSimpleName();

    private Drawable d_main, d_back, d_cart, d_web, d_close;

    private ImageView iv_topLeft;
    private ImageView iv_topLeftEx;
    private ImageView iv_topRight;
    private ImageView iv_topRightEx;
    private View v_topRightUp;
    private TextView tv_topRight, tv_topRightUp;
    private LSTextView tv_topCenter;
    private String fragTitle;

    //private boolean wasStack = false;

    public ActionBar(Context context) {
        super(context);
        initialize();
    }

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        Log.d(TAG, "initialize");

        inflate(getContext(), R.layout.actionbar_main, this);

        d_main = ContextCompat.getDrawable(getContext(), R.drawable.ls_h_img_logo);
        d_back = ContextCompat.getDrawable(getContext(), R.drawable.ls_g_btn_back);
        d_cart = ContextCompat.getDrawable(getContext(), R.drawable.ls_h_btn_cart);
        d_web = ContextCompat.getDrawable(getContext(), R.drawable.ls_h_btn_web);
        d_close = ContextCompat.getDrawable(getContext(), R.drawable.ls_g_btn_cancel);

        iv_topLeft = (ImageView) findViewById(R.id.iv_topLeft);
        iv_topLeftEx = (ImageView)findViewById(R.id.iv_topLeftExtra);
        iv_topRight = (ImageView) findViewById(R.id.iv_topRight);
        iv_topRightEx = (ImageView) findViewById(R.id.iv_topRightExtra);
        tv_topRight = (TextView) findViewById(R.id.tv_topRight);
        tv_topCenter = (LSTextView) findViewById(R.id.tv_topCenter);
        v_topRightUp = findViewById(R.id.view_topRightUp);
        tv_topRightUp = (TextView) v_topRightUp.findViewById(R.id.tv_topRightUp);

        // Setting font typeface here because the Layout.Font isn't ready yet
        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Bold.otf");
        Typeface fontSemiBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Semibold.ttf");
        tv_topCenter.setTypeface(fontBold);
        tv_topRight.setTypeface(fontSemiBold);

        setViewWebsite();
    }

    public void update(final BaseStackFrag frag) {
        Log.d(TAG, "update");

        fragTitle = "";

        FragmentManager fManager = Global.FragManager.getFragMan();
        int backStackCount = fManager.getBackStackEntryCount();

        Log.d(TAG, "Back stack count = " + backStackCount);

        if (frag != null) {
            Log.d(TAG, "frag != null");

            if (animate(tv_topCenter, true)){
                Log.d(TAG, "animate tv_topCenter true");
                tv_topCenter.setText(frag.getTitle());
            }
            else animateBlink(tv_topCenter, new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "animateBlink tv_topCenter");
                    tv_topCenter.setText(frag.getTitle());
                }
            });

            fragTitle = frag.getTitle();

            animate(tv_topRight, frag.setTopRight(tv_topRight));
            animate(iv_topRight, frag.setTopRight(iv_topRight));
            animate(iv_topRightEx, frag.setTopRightEx(iv_topRightEx));
            //animate(tv_topRightUp, frag.setTvTopRightUp(tv_topRightUp));
            animate(v_topRightUp, frag.setTopRightUp(v_topRightUp));

        } else {
            Log.d(TAG, "frag == null");
            animate(tv_topCenter, false);
            animate(tv_topRight, false);
            animate(iv_topRight, true);
            animate(iv_topRightEx,true);
            //animate(tv_topRightUp, true);
            animate(v_topRightUp, true);
            //setViewWebsite();
        }

        if (backStackCount >= 1) animateForStackChange(true);
        else if (backStackCount == 0) animateForStackChange(false);    }

    public static boolean animate(final View view, final boolean on) {
//        boolean wasOn = view.getAlpha() > 0;
//        if (!on && !wasOn) return false;

        view.animate().alpha(on ? 1 : 0).setDuration(250).setStartDelay(on ? 250 : 0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (on) view.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!on) view.setVisibility(INVISIBLE);
                view.setClickable(on);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        }).start();

        return true;
    }

    public static void animateBlink(final View view, final Runnable viewUpdate) {
        Log.d(TAG, "animateBlink");

        view.setClickable(false);
        view.animate().alpha(0).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                if (viewUpdate != null) viewUpdate.run();
                view.animate().alpha(1).setDuration(250).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setClickable(true);
                    }
                }).start();
            }
        }).start();
    }

    private void animateForStackChange(final boolean isStack) {
        Log.d(TAG, "animateForStackChange :: " + isStack);

        if (fragTitle.isEmpty() || !fragTitle.equals("Login")){
            Log.d(TAG, "not login frag ");
            iv_topLeft.setClickable(isStack);
            iv_topLeft.animate().alpha(0).setDuration(250).withEndAction(new Runnable() {
                @Override
                public void run() {
                    iv_topLeft.setImageDrawable(isStack ? d_back : d_main);
                    iv_topLeft.animate().alpha(1).setDuration(250).start();
                }
            }).start();
            if (!isStack) {
                setCartAndWeb();
            }

        }
        else {
            Log.d(TAG, "login frag");
            iv_topLeft.setClickable(isStack);
            iv_topLeft.animate().alpha(0).setDuration(250).withEndAction(new Runnable() {
                @Override
                public void run() {
                    iv_topLeft.setImageDrawable(d_close);
                    iv_topLeft.animate().alpha(1).setDuration(250).start();
                }
            }).start();
        }

        //wasStack = isStack;
    }

    private void setViewWebsite() {
        Log.d(TAG, "setViewWebsite");

        iv_topLeft.setClickable(false);
        iv_topLeft.setImageDrawable(d_main);
        iv_topLeft.setOnClickListener(v -> Global.FragManager.onBackPressed());

        setCartAndWeb();
    }

    private void setCartAndWeb(){

        Log.d(TAG, "setCartAndWeb: ");

        iv_topRight.setClickable(false);
        iv_topRight.setRotation(0);
        iv_topRight.animate().alpha(0).setDuration(250).withEndAction(() -> {
            iv_topRight.setImageDrawable(d_cart);
            iv_topRight.animate().alpha(1).setDuration(250).start();
        }).start();

        v_topRightUp.animate().alpha(0).setDuration(450).withEndAction(() -> {
            v_topRightUp.setVisibility(VISIBLE);
            v_topRightUp.animate().alpha(1).setDuration(450).start();
        }).start();

        if (Global.Prefs.hasToken()) updateCartCount();
        else tv_topRightUp.setText("0");


        iv_topRight.setOnClickListener(onCartClicked);
        v_topRightUp.setOnClickListener(onCartClicked);

        iv_topRightEx.setClickable(false);
        iv_topRightEx.setRotation(0);
        iv_topRightEx.animate().alpha(0).setDuration(250).withEndAction(() -> {
            iv_topRightEx.setImageDrawable(d_web);
            iv_topRightEx.animate().alpha(1).setDuration(250).start();
        }).start();

        iv_topRightEx.setOnClickListener(onWebHomeClicked);
    }

    public void updateCartCount(){
        Network.makeGetCartCountREQ(new IRequestCallback.Message() {
            @Override
            public void onSuccess(MessageResponse response) {
                Log.d(TAG, "getCartCount::onSuccess: ");
                if (response.getMessage() != null) tv_topRightUp.setText(response.getMessage());
                else onFailure("0");
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "getCartCount::onFailure: ");
                tv_topRightUp.setText("0");
            }
        });
    }

    private View.OnClickListener onCartClicked = view -> {
        if (!Global.Prefs.hasToken()){
            Log.d(TAG, "onClick: has no Token");
            Global.FragManager.stackFrag(LoginFrag.newInstance(false, ""));
            Toast.makeText(getContext(), "You need to login first", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!Global.Prefs.hasStore()){
            Log.d(TAG, "onClick: has no store");
            Global.FragManager.stackFrag(AccountFrag.newInstance());
            Toast.makeText(getContext(), "You need to choose store first", Toast.LENGTH_SHORT).show();
            return;
        }

        Global.FragManager.stackFrag(WebViewFrag.newInstance("Cart", Services.URL.Cart.get()));

            /* Google Analytics -- home_button_click */
        Utility.gaTracker.send(new HitBuilders.EventBuilder()
                .setCategory("ui_action")
                .setAction("cart_button_click")
                .setLabel("View Cart")
                .build()
        );
    };

    private View.OnClickListener onWebHomeClicked = view -> {
        Global.FragManager.stackFrag(WebViewFrag.newInstance("LivingSpaces", Services.URL.Website.get()));

            /* Google Analytics -- home_button_click */
        Utility.gaTracker.send(new HitBuilders.EventBuilder()
                .setCategory("ui_action")
                .setAction("home_button_click")
                .setLabel("View Website")
                .build()
        );
    };
}