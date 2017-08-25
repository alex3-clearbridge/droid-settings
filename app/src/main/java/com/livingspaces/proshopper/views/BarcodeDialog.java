package com.livingspaces.proshopper.views;

import android.animation.Animator;
import android.content.Context;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.livingspaces.proshopper.R;
import com.livingspaces.proshopper.utilities.Layout;
import com.livingspaces.proshopper.utilities.Utility;
import com.google.android.gms.analytics.HitBuilders;

/**
 * Created by rugvedambekar on 15-09-30.
 */
public class BarcodeDialog extends RelativeLayout implements EditTextKeyCB.IKeyListener {

    private static final String TAG = BarcodeDialog.class.getSimpleName();

    private TextView tv_submit, tv_cancel, tv_ok;
    private TextView tv_cancelF, tv_error;
    private EditTextKeyCB et_barcode;
    private View v_underline, v_loading, error_dialog;

    private View dialogHelp;

    private Animation slideDownAnim, slideUpAnim, shakeAnim;
    private Animation slideInAnim, slideOutAnim;
    private boolean reqCanceled;

    private ICallback callback;

    public BarcodeDialog(Context context) {
        super(context);
    }

    public BarcodeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public BarcodeDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        inflate(getContext(), R.layout.dialog_barcode, this);

        shakeAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shake);

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

        slideInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_dialog_in);
        slideInAnim.setStartOffset(250);
        slideInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (dialogHelp != null) dialogHelp.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        slideOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_dialog_out);
        slideOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (dialogHelp != null) dialogHelp.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancelF = (TextView) findViewById(R.id.tv_cancelFetch);
        //tv_error = (TextView) findViewById(R.id.tv_error);
        tv_ok = (TextView) findViewById(R.id.tv_error_okBtn);
        et_barcode = (EditTextKeyCB) findViewById(R.id.et_barcode);


        v_underline = findViewById(R.id.v_etUnderline);
        v_loading = findViewById(R.id.shade_codeDialog);
        error_dialog = findViewById(R.id.error_codeDialog);

        et_barcode.setKeyListener(this);
        et_barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSubmit(inputValid(s.toString()));
            }
        });
        setSubmit(false);

        tv_submit.setOnClickListener(onSubmitListener);
        tv_cancelF.setOnClickListener(onCancelFetch);
        tv_cancel.setOnClickListener(onCancel);
        tv_ok.setOnClickListener(onOk);

        v_loading.setVisibility(GONE);
        error_dialog.setVisibility(GONE);

    }

    public void setCallback(ICallback cb) {
        callback = cb;
    }

    public void registerHelpDialog(View view) {
        if (view.getId() != R.id.dialog_help) return;

        dialogHelp = view;
        dialogHelp.findViewById(R.id.tv_enterCodeInHelp).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideHelp();
            }
        });

        findViewById(R.id.tv_needHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /** Google Analytics -- help_clicked */
                Utility.gaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("ui_action")
                                .setAction("help_clicked")
                                .build()
                );

                hide();
                showHelp();
            }
        });
    }

    public void cancelREQ() {
        reqCanceled = true;
    }

    public boolean reqWasCanceled() {
        boolean wasCanceled = reqCanceled;
        reqCanceled = false;
        return wasCanceled;
    }

    public void show(boolean forLoading) {
        Log.d(TAG, "show: " + forLoading);
        setVisibility(INVISIBLE);
        startAnimation(slideDownAnim);

        if (forLoading) showLoading(true);
        else showKeyboard();
    }

    public void hide() {
        if (isLoading()) showLoading(false);
        startAnimation(slideUpAnim);
        hideKeyboard();
    }

    public void hideWithOutAnimation() {
        if (isLoading()) showLoading(false);
        setVisibility(View.GONE);
        hideKeyboard();
    }

    public void showError() {
        if (isLoading()) showLoading(false);
        ((Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
        startAnimation(shakeAnim);

        error_dialog.setVisibility(VISIBLE);

        /*
        v_underline.setBackgroundColor(Layout.Color.dialogError_txt);
        tv_error.setVisibility(View.VISIBLE);
        tv_error.setText("Product Not Found");
        */
    }

    public String getInput() {
        if (et_barcode == null) return "";
        return et_barcode.getText().toString();
    }

    @Override
    public void onUserDismiss() {
        if (callback != null) callback.onCancel();
    }

    private void showLoading(final boolean show) {
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
                        if (!show) v_loading.setVisibility(View.GONE);
                    }
                }).start();
    }

    private boolean isLoading() {
        return v_loading.getVisibility() != GONE;
    }

    private void setSubmit(boolean on) {
        if (on) {
            tv_submit.setBackground(getResources().getDrawable(R.drawable.button));
            tv_submit.setClickable(on);
        } else {
            tv_submit.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        }
    }

    private boolean inputValid(String input) {
        return input.length() >= 5 && input.length() <= 20;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_barcode.getWindowToken(), 0);
    }

    private void showKeyboard() {
        et_barcode.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_barcode, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showHelp() {
        if (dialogHelp == null) return;
        dialogHelp.setVisibility(INVISIBLE);
        dialogHelp.startAnimation(slideInAnim);
    }

    public void hideHelp() {
        if (dialogHelp == null) return;
        dialogHelp.startAnimation(slideOutAnim);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                show(false);
            }
        }, 250);
    }

    private final OnClickListener onCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            et_barcode.setText("");
            v_underline.setBackgroundColor(Layout.Color.main_txt);
            //tv_error.setVisibility(View.GONE);

            if (callback != null) callback.onCancel();
        }
    };
    private final OnClickListener onCancelFetch = new OnClickListener() {
        @Override
        public void onClick(View v) {
            cancelREQ();
            if (callback != null) callback.onCancelFetch();
        }
    };
    private final OnClickListener onSubmitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String inputCode = et_barcode.getText().toString();
            if (!inputValid(inputCode)) return;

            if (callback != null) callback.onSubmit(inputCode);

            hideKeyboard();
            showLoading(true);
            et_barcode.setText("");
        }
    };

    private final OnClickListener onOk = new OnClickListener() {
        @Override
        public void onClick(View v) {
            error_dialog.setVisibility(GONE);
            onCancel.onClick(v);
        }
    };

    public interface ICallback {
        void onSubmit(String input);

        void onCancel();

        void onCancelFetch();
    }

}
