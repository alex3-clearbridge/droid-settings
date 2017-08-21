package com.livingspaces.proshopper.views;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.livingspaces.proshopper.R;

/**
 * Created by alexeyredchets on 2017-08-16.
 */

public class LoginDialog extends LinearLayout {

    private static final String TAG = LoginDialog.class.getSimpleName();

    private LSTextView tv_header, tv_message, tv_button, tv_cancelF;
    private View v_loading;
    private Animation slideDownAnim, slideUpAnim;
    private ICallback callback;
    private boolean reqCanceled;


    public LoginDialog(Context context) {
        super(context);
    }

    public LoginDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoginDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.dialog_login, this);

        slideUpAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_dialog_up);
        slideUpAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }
        });

        slideDownAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_dialog_down);
        slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(VISIBLE);
            }
        });

        tv_header = (LSTextView)findViewById(R.id.tv_head_login_dialog);
        tv_message = (LSTextView)findViewById(R.id.tv_login_dialog);
        tv_button = (LSTextView)findViewById(R.id.tv_login_button);
        tv_cancelF = (LSTextView) findViewById(R.id.tv_cancelFetch_login);
        v_loading = findViewById(R.id.shade_loginDialog);

        Typeface fontSemiBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Light.ttf");
        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Bold.otf");
        tv_header.setTypeface(fontBold);
        tv_message.setTypeface(fontSemiBold);

        v_loading.setVisibility(GONE);

        tv_button.setOnClickListener(onOk);
        tv_cancelF.setOnClickListener(onCancelFetch);

    }

    private final OnClickListener onOk = new OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.onOk();
        }
    };
    private final OnClickListener onCancelFetch = new OnClickListener() {
        @Override
        public void onClick(View view) {
            cancelREQ();
            if (callback != null) callback.onOk();
        }
    };

    public void cancelREQ() {
        Log.d(TAG, "cancelREQ");
        reqCanceled = true;
    }

    public void show(boolean forLoading){
        Log.d(TAG, "show for loading " + forLoading);
        setVisibility(INVISIBLE);
        startAnimation(slideDownAnim);

        if (forLoading) showLoading(true);
    }

    private void showLoading(final boolean show) {
        Log.d(TAG, "show loading " + show);
        if (show) v_loading.setVisibility(INVISIBLE);
        v_loading.animate().setDuration(250).setStartDelay(show ? 0 : 250).alpha(show ? 1 : 0)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (show) v_loading.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!show) {
                            v_loading.setVisibility(View.GONE);
                            //popUp_dialog.setVisibility(VISIBLE);
                        }
                    }
                }).start();
    }

    public void show(String method) {
        Log.d(TAG, "show()");
        //v_loading.setVisibility(GONE);
        //setVisibility(VISIBLE);
        //startAnimation(slideDownAnim);
        switch (method) {
            case "empty":
                Log.d(TAG, method);
                tv_header.setText("Error");
                tv_message.setText("All fields are required.");
                break;
            case "notValid":
                Log.d(TAG, method);
                tv_header.setText("Invalid email");
                tv_message.setText("Email is not valid. Try again.");
                break;
            case "ok":
                Log.d(TAG, method);
                tv_header.setText("Success");
                tv_message.setText("You successfully logged in.");
                break;
            case "notMatch":
                Log.d(TAG, method);
                tv_header.setText("Error");
                tv_message.setText("Passwords do not match.");
                break;
            case "smallPass":
                Log.d(TAG, method);
                tv_header.setText("Error");
                tv_message.setText("Passwords cannot be less than 6 characters.");
                break;
            case "createFailed":
                Log.d(TAG, method);
                tv_header.setText("Error");
                tv_message.setText("Server error or email is already taken.");
                break;
            case "createSuccess":
                Log.d(TAG, method);
                tv_header.setText("Success");
                tv_message.setText("Account was created successfully.");
                break;
            case "emailSent":
                Log.d(TAG, method);
                tv_header.setText("Email sent");
                tv_message.setText("We have sent an email with instructions on how to reset your password.");
                break;
            case "invalidEmail":
                Log.d(TAG, method);
                tv_header.setText("INVALID EMAIL");
                tv_message.setText("Your email does not exist in our system, try again.");
                break;
            default:
                Log.d(TAG, method);
                tv_header.setText("INVALID EMAIL");
                tv_message.setText("Your email does not exist in our system, try again.");
                break;
        }
    }

    private boolean isLoading() {
        Log.d(TAG, "isLoading");
        return v_loading.getVisibility() != GONE;
    }

    public void hide() {
        Log.d(TAG, "hide");
        if (isLoading()) {
            v_loading.setVisibility(GONE);//showLoading(false);
            setVisibility(VISIBLE);
            return;
        }
        startAnimation(slideUpAnim);
    }

    public void setCallback(ICallback cb) {
        callback = cb;
    }

    public interface ICallback {
        void onOk();
    }
}
