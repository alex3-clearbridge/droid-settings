package com.livingspaces.proshopper.views;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.livingspaces.proshopper.LoginActivity;
import com.livingspaces.proshopper.LoginUtility;
import com.livingspaces.proshopper.MainActivity;
import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.fragments.BaseLoginFrag;
import com.livingspaces.proshopper.fragments.BaseStackFrag;
import com.livingspaces.proshopper.utilities.Global;

/**
 * Created by alexeyredchets on 2017-08-14.
 */

public class ActionBarLogin extends RelativeLayout {

    private static final String TAG = ActionBarLogin.class.getSimpleName();

    private Drawable d_close, d_back;

    private ImageView iv_topLeft;
    private LSTextView tv_topCenter;
    private boolean wasStack = false;

    public ActionBarLogin(Context context) {
        super(context);
        init();
    }

    public ActionBarLogin(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionBarLogin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Log.d(TAG, "init");

        inflate(getContext(), R.layout.actionbar_login, this);

        d_back = ContextCompat.getDrawable(getContext(), R.drawable.ls_g_btn_back);
        d_close = ContextCompat.getDrawable(getContext(), R.drawable.ls_g_btn_cancel);

        tv_topCenter = (LSTextView)findViewById(R.id.tv_log_topCenter);
        iv_topLeft = (ImageView)findViewById(R.id.iv_log_topLeft);

        // Setting font typeface here because the Layout.Font isn't ready yet
        Typeface fontSemiBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Semibold.ttf");
        tv_topCenter.setTypeface(fontSemiBold);

        tv_topCenter.setText("Login");

        iv_topLeft.setClickable(true);
        iv_topLeft.setImageDrawable(d_close);
        iv_topLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onTopLeftButton clicked");
                LoginUtility.FragManager.onBackPressed();
            }
        });

    }

    public void update(final BaseLoginFrag frag) {
        Log.d(TAG, "update");

        FragmentManager fManager = LoginUtility.FragManager.getFragMan();
        int backStackCount = fManager.getBackStackEntryCount();
        Log.d(TAG, "getBackStackEntryCount(): " + backStackCount);
        Log.d(TAG, "wasStack" + wasStack);

        if (frag != null) {

            if (animate(tv_topCenter, true)) tv_topCenter.setText(frag.getTitle());
            else animateBlink(tv_topCenter, new Runnable() {
                @Override
                public void run() {
                    tv_topCenter.setText(frag.getTitle());
                }
            });

        } else {
            animate(tv_topCenter, false);
            //tv_topCenter.setText("Login");
        }

        if (backStackCount == 1 && !wasStack) animateForStackChange(true);
        else if (backStackCount == 0 && wasStack) animateForStackChange(false);
    }

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
        Log.d(TAG, "animateForStackChange");

        //iv_topLeft.setClickable(isStack);
        iv_topLeft.animate().alpha(0).setDuration(250).withEndAction(new Runnable() {
            @Override
            public void run() {
                iv_topLeft.setImageDrawable(isStack ? d_back : d_close);
                iv_topLeft.animate().alpha(1).setDuration(250).start();
            }
        }).start();

        wasStack = isStack;
    }
}
