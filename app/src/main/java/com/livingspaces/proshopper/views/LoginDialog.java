package com.livingspaces.proshopper.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.livingspaces.proshopper.R;

/**
 * Created by alexeyredchets on 2017-08-16.
 */

public class LoginDialog extends LinearLayout {

    private static final String TAG = LoginDialog.class.getSimpleName();

    private LSTextView tv_header, tv_message, tv_button;
    private Animation slideDownAnim, slideUpAnim;
    private ICallback callback;


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

        tv_header = (LSTextView)findViewById(R.id.tv_head_login_dialog);
        tv_message = (LSTextView)findViewById(R.id.tv_login_dialog);
        tv_button = (LSTextView)findViewById(R.id.tv_login_button);

        Typeface fontSemiBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Light.ttf");
        Typeface fontBold = Typeface.createFromAsset(getContext().getAssets(), "SourceSansPro-Bold.otf");
        tv_header.setTypeface(fontBold);
        tv_message.setTypeface(fontSemiBold);

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

        tv_button.setOnClickListener(onOk);
    }

    private final OnClickListener onOk = new OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.onOk();
        }
    };

    public void show() {
        setVisibility(INVISIBLE);
        startAnimation(slideDownAnim);
        tv_header.setText("INVALID EMAIL");
        tv_message.setText("Your email does not exist in our system, try again.");
    }

    public void hide() {
        startAnimation(slideUpAnim);
    }

    public void setCallback(ICallback cb) {
        callback = cb;
    }

    public interface ICallback {
        void onOk();
    }
}
